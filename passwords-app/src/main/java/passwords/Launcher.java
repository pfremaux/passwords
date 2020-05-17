package passwords;


import commons.lib.FileUtils;
import commons.lib.UnrecoverableException;
import commons.lib.gui.MessageDialog;
import commons.lib.server.socket.MessageConsumerManager;
import commons.lib.server.socket.Server;
import commons.lib.server.socket.Wrapper;
import commons.lib.server.socket.WrapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.commandline.ActionChoice;
import passwords.commandline.ChooseNumberCommandLine;
import passwords.communication.shared.message.consumer.GetCredentialConsumer;
import passwords.communication.shared.message.pojo.GetCredential;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.EncryptionService;
import passwords.encryption.annotation.InitAllAnnotations;
import passwords.expectation.Expectation;
import passwords.expectation.KeysExist;
import passwords.gui.ChooseNumberDialog;
import passwords.gui.ClientCredentialDialog;
import passwords.gui.CredentialsTreeDialog;
import passwords.gui.DebugWindow;
import passwords.pojo.CredentialDatum;
import passwords.pojo.Node;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO instantiate this class (or another one) in order to use attributes instead of N parameters per methods.
public final class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private final static Expectation[] expectationsCli = {
            new KeysExist()
    };

    public static void main(String... args) {
        manageArguments(args);
        if (InputParameters.DISTANT_SERVER_HOSTNAME.getPropertyString().length() > 0
                && InputParameters.DISTANT_SERVER_PORT.getPropertyInt() > 0) {
            ClientCredentialDialog clientCredentialDialog = new ClientCredentialDialog(null);
            clientCredentialDialog.setVisible(true);
            System.exit(-1);
        }
        final InitAllAnnotations initAllAnnotations = new InitAllAnnotations();
        final ResourceBundle uiMessages = ResourceBundle.getBundle("lang/ui_messages", Locale.ENGLISH);
        final EncryptionFactory encryptionServiceFactory = initAllAnnotations.getEncryptionFactory();
        if (Boolean.parseBoolean(InputParameters.COMMAND_LINE_MODE.getPropertyString())) {
            for (Expectation expectation : expectationsCli) {
                expectation.resolve();
            }
            manageCommandLine(encryptionServiceFactory);
        } else {
            final DebugWindow debugWindow = new DebugWindow();
            debugWindow.setVisible(true);
            manageGui(uiMessages, debugWindow, encryptionServiceFactory);
        }
    }

    private static void manageGui(ResourceBundle uiMessages, DebugWindow debugWindow, EncryptionFactory encryptionServiceFactory) {
        final CompletableFuture<CredentialsSettings> future = new CompletableFuture<>();
        final ChooseNumberDialog chooseNumberDialog = new ChooseNumberDialog(debugWindow, uiMessages, future);
        chooseNumberDialog.setVisible(true);
        final CredentialsSettings credentialsSettings;
        try {
            credentialsSettings = future.get();
            final List<CredentialDatum> allCredentials = manageFilesEncryptions(encryptionServiceFactory, credentialsSettings, uiMessages);
            manageAccountsCredentials(debugWindow, encryptionServiceFactory, credentialsSettings, allCredentials, uiMessages);
            debugWindow.dispose();
        } catch (UnrecoverableException e) {
            logger.error("Unrecoverable error...", e);
            if (e.getMessageInformUser() != null) {
                final MessageDialog messageDialog = new MessageDialog(null, e.getMessageInformUser());
                messageDialog.setVisible(true);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Execution error...", e);
            final MessageDialog messageDialog = new MessageDialog(null, new String[]{e.getMessage()});
            messageDialog.setVisible(true);
        } catch (Throwable e) {
            logger.error("Fatal error...", e);
        }
    }

    // TODO move another class or use the new class created in the lib
    private static void manageCommandLine(EncryptionFactory encryptionFactory) {
        final ChooseNumberCommandLine chooseNumberCommandLine = new ChooseNumberCommandLine();
        final Optional<CredentialsSettings> credentialsSettings = chooseNumberCommandLine.readAndGetCredentialsSettings();
        if (credentialsSettings.isEmpty()) {
            logger.error("No credential settings loaded. Can't continue.");
            System.exit(-3);
        }
        final List<CredentialDatum> credentialData = manageFilesEncryptions(encryptionFactory, credentialsSettings.get(), null);
        final Console console = System.console();
        Node<CredentialDatum> nodes = new Node<>("Root", null, new ArrayList<>());
        CredentialsTreeDialog.credentialDataToNodes(credentialData, nodes);
        int i = 0;
        boolean isDirty = false;
        final Set<Integer> allowedChoices = Stream.of(ActionChoice.values()).map(ActionChoice::getChoice).collect(Collectors.toSet());
        while (true) {
            while (!allowedChoices.contains(i)) {
                console.printf("What now ?\n");
                for (ActionChoice value : ActionChoice.values()) {
                    console.printf("%d. %s", value.getChoice(), value.name());
                    if (ActionChoice.SAVE == value && isDirty) {
                        console.printf(" *");
                    }
                    console.printf("\n");
                }
                i = ChooseNumberCommandLine.readInt(console);
            }
            console.flush();
            if (i == ActionChoice.LIST.getChoice()) {
                showCreds(credentialData, nodes, console);
            }
            if (i == ActionChoice.DETAILS.getChoice()) {
                int anInt = -1;
                while (anInt < 0 || anInt >= credentialData.size()) {
                    console.printf("Enter a number between %d and %d\n", 0, (credentialData.size() - 1));
                    anInt = ChooseNumberCommandLine.readInt(console);
                }
                final CredentialDatum credentialDatum = credentialData.get(anInt);
                console.printf("URL = %s\nLogin = %s\npassword = *******\nDescription = %s\n",
                        credentialDatum.getUrl(),
                        credentialDatum.getLogin(),
                        credentialDatum.getComments());
            }
            if (i == ActionChoice.ADD.getChoice()) {
                console.printf("URL ? ");
                final String url = console.readLine();
                console.printf("Login ? ");
                final String login = console.readLine();
                console.printf("Password ? ");
                final String password = new String(console.readPassword());
                console.printf("Comment ? ");
                final String comment = console.readLine();
                // TODO manage hierarchy
                final CredentialDatum credentialDatum = new CredentialDatum("", url, login, password, comment);
                nodes.getOrCreate("", credentialDatum);
                credentialData.add(credentialDatum);
                isDirty = true;
            }
            if (i == ActionChoice.ADD_DIR.getChoice()) {
                console.printf("What name ?\n");
                final String nameDir = console.readLine();
                nodes.getOrCreate(nameDir, null);
            }
            if (i == ActionChoice.DELETE.getChoice()) {
                showCreds(credentialData, nodes, console);
                console.printf("Which one ?\n");
                int i1 = ChooseNumberCommandLine.readInt(console);
                credentialData.remove(i1);
                isDirty = true;
            }
            if (i == ActionChoice.SAVE.getChoice()) {
                EncryptionService service = encryptionFactory.getService(InputParameters.ENCRYPT_VERSION.getPropertyInt());
                final Path fullPathSaveDir = InputParameters.SAVE_DIR.getPropertyPath();
                service.encrypt(fullPathSaveDir, credentialData, credentialsSettings.get());
                /*logger.info("decrypt right now to validate...");
                List<CredentialDatum> decrypt = service.decrypt(fullPathSaveDir, credentialsSettings.get());
                logger.info("{} lines decrypted", decrypt.size());
                for (CredentialDatum credentialDatum : decrypt) {
                    logger.info("URL = {}", credentialDatum.getUrl());
                }*/
                isDirty = false;
            }
            if (i == ActionChoice.MOVE.getChoice()) {

                isDirty = true;
            }
            if (i == ActionChoice.EXIT.getChoice()) {
                return;
            }
            i = 0;
        }
    }

    private static Set<String> opened = new HashSet<>();

    private static void showCreds(List<CredentialDatum> credentialData, Node<CredentialDatum> nodes, Console console) {
        int i = 0;
        displayCreds(credentialData, console, nodes, i);
        //
    }

    private static void displayCreds(List<CredentialDatum> credentialData, Console console, Node<CredentialDatum> nodes, int i) {
        String infoStat;
        logger.info("Iterate over {} elements thanks to the node {}", nodes.getSubValues().size(), nodes.getName());
        for (Node<CredentialDatum> subValue : nodes.getSubValues()) {
            if (subValue.isLeaf()) {
                logger.info("leaf");
                infoStat = "  ";
                console.printf("%d. %s%s\n", i, infoStat, credentialData.get(i).getDisplayableInfo());
            } else {
                logger.info("not a leaf");
                String name = subValue.getName();
                logger.info("name {}", name);
                if (opened.contains(name)) {// buggy if duplicate name a different level
                    infoStat = "v ";
                    Map<String, CredentialDatum> children = subValue.getChildren(subValue.getName());
                    int j = 0;
                    console.printf("%d. %s%s\n", i, infoStat, credentialData.get(i).getDisplayableInfo());
                    //todo gérer le level pour indenter les sublevels
                    //for (CredentialDatum value : children.values()) {
                    displayCreds(new ArrayList<>(children.values()), console, subValue, j);
                    //1j++;
                    //}
                } else {
                    infoStat = "> ";
                }
            }

            i++;
        }
    }

    private static void manageArguments(String[] s) {
        final ResourceBundle appInfo = ResourceBundle.getBundle("app-info", Locale.ENGLISH);
        if (s.length > 0) {
            if (s[0].equalsIgnoreCase("-h")) {
                final String version = appInfo.getString("app.version");
                System.out.println("Version : " + version);
                System.out.println("Example : ");
                System.out.println("<this app> " + InputParameters.toCommandLineFormat());
                for (InputParameters value : InputParameters.values()) {
                    System.out.println("\t" + value.getCommandLineKey() + "\t\t" + appInfo.getString(value.getKey()));
                }
                System.exit(0);
            } else {
                for (int i = 0; i < s.length; i = i + 2) {
                    if (i % 2 == 0) {
                        Optional<InputParameters> inputParameter = InputParameters.fromCommandLineKey(s[i]);
                        if (inputParameter.isPresent()) {
                            System.setProperty(inputParameter.get().getKey(), s[i + 1]);
                        } else {
                            System.err.println("Unexpected parameter : " + s[i]);
                            System.exit(-1);
                        }
                    }
                }
            }
        }
    }

    private static List<CredentialDatum> manageFilesEncryptions(EncryptionFactory encryptionFactory, CredentialsSettings securitySettings, ResourceBundle uiMessages) {
        final Path fullPathSaveDir = InputParameters.SAVE_DIR.getPropertyPath();
        final Path fullPathSaveFile = fullPathSaveDir.resolve(InputParameters.ENCRYPTED_FILENAME.getPropertyPath());
        final List<CredentialDatum> allCredentials;
        if (FileUtils.isFileExist(fullPathSaveFile)) {
            logger.debug("Getting the Decryptor version {}", InputParameters.DECRYPT_VERSION.getPropertyInt());
            final EncryptionService service = encryptionFactory.getService(InputParameters.DECRYPT_VERSION.getPropertyInt());
            logger.debug("EncryptionService found : {}", service);
            allCredentials = service.decrypt(fullPathSaveDir, securitySettings);
        } else {
            logger.debug("No encrypted file found at {}", fullPathSaveFile.toAbsolutePath().toString());
            logger.info("No encrypted file found.");
            allCredentials = new ArrayList<>();
        }
        return allCredentials;
    }

    private static void manageAccountsCredentials(DebugWindow debugWindow, EncryptionFactory encryptionFactory, CredentialsSettings securitySettings, List<CredentialDatum> allCredentials, ResourceBundle uiMessages) {
        final CredentialsTreeDialog credentialsTreeDialog = new CredentialsTreeDialog(debugWindow, encryptionFactory, allCredentials, securitySettings, uiMessages);
        final int listeningPort = InputParameters.LISTENING_PORT.getPropertyInt();
        if (listeningPort != 0) {
            Map<Integer, Function<List<String>, Wrapper>> wrappers = new HashMap<>();
            wrappers.put(GetCredential.CODE, strings -> new Wrapper(GetCredential.CODE, new GetCredential(strings)));
            WrapperFactory wrapperFactory = new WrapperFactory(wrappers);
            MessageConsumerManager messageConsumerManager = new MessageConsumerManager();
            messageConsumerManager.register(GetCredential.CODE, new GetCredentialConsumer(allCredentials));
            Server server = new Server("localhost", listeningPort, 1, messageConsumerManager, wrapperFactory);
            try {
                server.listen();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            credentialsTreeDialog.setVisible(true);
        }
    }

}

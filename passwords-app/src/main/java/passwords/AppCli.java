package passwords;

import commons.lib.SystemUtils;
import commons.lib.console.ConsoleFactory;
import commons.lib.console.CustomConsole;
import commons.lib.console.v2.item.Crud;
import commons.lib.console.v2.item.DescriptibleConsoleItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.commandline.ActionChoice;
import passwords.commandline.ChooseNumberCommandLine;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.EncryptionService;
import passwords.encryption.FileAccess;
import passwords.gui.CredentialsTreeDialog;
import passwords.pojo.CredentialDatum;
import passwords.pojo.CredentialDatumDirForConsole;
import passwords.pojo.CredentialDatumForConsole;
import passwords.pojo.Node;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppCli {
    private static final Logger logger = LoggerFactory.getLogger(AppCli.class);

    // TODO move another class or use the new class created in the lib
    public static void commandLineInteraction(EncryptionFactory encryptionFactory) {
        final ChooseNumberCommandLine chooseNumberCommandLine = new ChooseNumberCommandLine();
        final Optional<CredentialsSettings> credentialsSettings = chooseNumberCommandLine.readAndGetCredentialsSettings();
        if (credentialsSettings.isEmpty()) {
            logger.error("No credential settings loaded. Can't continue.");
            SystemUtils.failUser();
        }
        final List<CredentialDatum> credentialData = FileAccess.decipher(encryptionFactory, credentialsSettings.get(), null);

        final CustomConsole customConsole = ConsoleFactory.getInstance(InputParameters.CONSOLE_INPUT.getPropertyPath());

        Node<CredentialDatum> nodes = new Node<>("Root", null, new ArrayList<>());
        CredentialsTreeDialog.credentialDataToNodes(credentialData, nodes);
        int i = 0;
        boolean isDirty = false;
        final Set<Integer> allowedChoices = Stream.of(ActionChoice.values()).map(ActionChoice::getChoice).collect(Collectors.toSet());
        while (true) {
            while (!allowedChoices.contains(i)) {
                customConsole.printf("What now ?");
                for (ActionChoice value : ActionChoice.values()) {
                    customConsole.printf("%d. %s", value.getChoice(), value.name());
                    if (ActionChoice.SAVE == value && isDirty) {
                        customConsole.printf(" *");
                    }
                }
                i = ChooseNumberCommandLine.readInt(customConsole);
            }
            // customConsole.flush();
            if (i == ActionChoice.LIST.getChoice()) {
                showCreds(credentialData, nodes, customConsole);
            }
            if (i == ActionChoice.DETAILS.getChoice()) {
                int anInt = -1;
                while (anInt < 0 || anInt >= credentialData.size()) {
                    customConsole.printf("Enter a number between %d and %d", 0, (credentialData.size() - 1));
                    anInt = ChooseNumberCommandLine.readInt(customConsole);
                }
                final CredentialDatum credentialDatum = credentialData.get(anInt);
                customConsole.printf("URL = %s\nLogin = %s\npassword = *******\nDescription = %s\n",
                        credentialDatum.getUrl(),
                        credentialDatum.getLogin(),
                        credentialDatum.getComments());
            }
            if (i == ActionChoice.ADD.getChoice()) {
                customConsole.printf("URL ? ");
                final String url = customConsole.readLine();
                customConsole.printf("Login ? ");
                final String login = customConsole.readLine();
                customConsole.printf("Password ? ");
                final String password = new String(customConsole.readPassword());
                customConsole.printf("Comment ? ");
                final String comment = customConsole.readLine();
                // TODO manage hierarchy
                final CredentialDatum credentialDatum = new CredentialDatum("", url, login, password, comment);
                nodes.getOrCreate("", credentialDatum);
                credentialData.add(credentialDatum);
                isDirty = true;
            }
            if (i == ActionChoice.ADD_DIR.getChoice()) {
                customConsole.printf("What name ?\n");
                final String nameDir = customConsole.readLine();
                nodes.getOrCreate(nameDir, null); // TODO maybe bug if multiples level
            }
            if (i == ActionChoice.DELETE.getChoice()) {
                showCreds(credentialData, nodes, customConsole);
                customConsole.printf("Which one ?\n");
                int i1 = ChooseNumberCommandLine.readInt(customConsole);
                credentialData.remove(i1);
                isDirty = true;
            }
            if (i == ActionChoice.SAVE.getChoice()) {
                EncryptionService service = encryptionFactory.getService(InputParameters.ENCRYPT_VERSION.getPropertyInt());
                final Path fullPathSaveDir = InputParameters.SAVE_DIR.getPropertyPath();
                service.encrypt(fullPathSaveDir, credentialData, credentialsSettings.get());
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

    private static void showCreds(List<CredentialDatum> credentialData, Node<CredentialDatum> nodes, CustomConsole console) {
        int i = 0;
        displayCreds(credentialData, console, nodes, i);
        //
    }

    private static Set<String> opened = new HashSet<>();

    private static void displayCreds(List<CredentialDatum> credentialData, CustomConsole customConsole, Node<CredentialDatum> nodes, int i) {
        String infoStat;
        logger.info("Iterate over {} elements thanks to the node {}", nodes.getSubValues().size(), nodes.getName());


        final List<DescriptibleConsoleItem> credentialsForConsole = getDescriptibleConsoleItems(nodes, customConsole);
        Crud<DescriptibleConsoleItem> credentialDatumInConsoleCrud = new Crud<>(ConsoleFactory.getInstance(), credentialsForConsole);
        DescriptibleConsoleItem interact = null;
        final LinkedList<String> parents = new LinkedList<>();
        boolean elementFound = false;
        while (!elementFound) {
            interact = credentialDatumInConsoleCrud.interact(
                    new CredentialDatumForConsole(
                            customConsole,
                            new CredentialDatum("devrait pas etre affiché", "url chiote", "login", "pawd", "comments")));
            if (interact instanceof CredentialDatumForConsole) {
                elementFound = true;
            } else {
                final CredentialDatumDirForConsole element = (CredentialDatumDirForConsole) interact;
                parents.add(element.name());
                final Node<CredentialDatum> selectedNode = nodes.findByHierarchy(parents);
                List<DescriptibleConsoleItem> descriptibleConsoleItems = getDescriptibleConsoleItems(selectedNode, customConsole);
                credentialDatumInConsoleCrud = new Crud<>(ConsoleFactory.getInstance(), descriptibleConsoleItems);
            }
        }


        /*for (Node<CredentialDatum> subValue : nodes.getSubValues()) {
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
                    Map<String, CredentialDatum> children = subValue.getChildrenV2(subValue.getName());
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
        }*/
    }

    private static List<DescriptibleConsoleItem> getDescriptibleConsoleItems(Node<CredentialDatum> nodes, CustomConsole customConsole) {
        return nodes.getSubValues()
                .stream()
                .map(credentialDatumNode ->
                        credentialDatumNode.getValue() == null
                                ? new CredentialDatumDirForConsole(customConsole, credentialDatumNode.getName(), credentialDatumNode.getValue())
                                : new CredentialDatumForConsole(customConsole, credentialDatumNode.getValue()))
                .collect(Collectors.toList());
    }

}

package passwords;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.init.CliApp;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleRunner;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;
import passwords.commandline.v2.LoadCredentialDataAction;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.annotation.InitAnnotationsForVersionedEncryptionClasses;
import passwords.expectation.Expectation;
import passwords.expectation.KeysExist;
import passwords.gui.ClientCredentialDialog;
import passwords.gui.DebugWindow;
import passwords.settings.InputParameters;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

public class LauncherNew extends CliApp {

    private final static Expectation[] expectationsCli = {
            new KeysExist()
    };

    public static final String PUBLIC_FILE_EXTENSION = "--pbext";
    public static final String PRIVATE_FILE_EXTENSION = "--pvext";
    public static final String ENCRYPTED_FILENAME = "-o";
    public static final String DECRYPT_VERSION = "--dcv";
    public static final String ENCRYPT_VERSION = "--ecv";
    public static final String FILE_DATUM_SEPARATOR = "--fsep";
    public static final String NBR_KEYPAIRS = "--nkp";
    public static final String SAVE_DIR = "--sdir";
    public static final String KEYS_DIR = "--kdir";
    public static final String COMMAND_LINE_MODE = "--cli";
    public static final String LISTENING_PORT = "--lp";
    public static final String DISTANT_SERVER_HOSTNAME = "--sh";
    public static final String DISTANT_SERVER_PORT = "--sp";
    public static final String CONSOLE_INPUT = "--con";
    public static final String MAIN_CONTEXT = "main";

    public void init() {
        register(PUBLIC_FILE_EXTENSION, "public.key.ext", "pub", "File extension of public keys.");
        register(PRIVATE_FILE_EXTENSION, "private.key.ext", "priv", "File extension of private keys.");
        register(ENCRYPTED_FILENAME, "encrypted.file.name", "enc", "Encrypted file name. this is the file where all passwords will be stored.");
        register(DECRYPT_VERSION, "decrypt.version", "5", "Implementation version of the decryptor.");
        register(ENCRYPT_VERSION, "encrypt.version", "5", "Implementation version of the encryptor.");
        register(FILE_DATUM_SEPARATOR, "file.datum.separator", ";", "Datum separator. For example in a CSV file the separator is a ; . The separator is used in the password store. It's advised to not include this character in your passwords.");
        register(NBR_KEYPAIRS, "keypairs.number", "10", "Number of key pair you want to generate.");
        register(SAVE_DIR, "saveRecursivly.directory", ".", "Path where you want to store and read the encrypted password store.");
        register(KEYS_DIR, "keys.directory", "D:\\keys", "Path where the keys are generated and read.");
        register(COMMAND_LINE_MODE, "mode.cli", "false", "Set to true if you want to use the command line interface instead of the GUI.");
        register(LISTENING_PORT, "server.listening.port", "0", "");
        register(DISTANT_SERVER_HOSTNAME, "server.distant.hostname", "", "");
        register(DISTANT_SERVER_PORT, "server.distant.port", "0", "");
        register(CONSOLE_INPUT, "console.input", "not set", "If you want to feed automatically inputs while executing the CLI.");
    }

    public static void main(String[] args) {
        final LauncherNew launcherNew = new LauncherNew();
        for (Expectation expectation : expectationsCli) {
            expectation.resolve();
        }
        launcherNew.validateAndLoad(args);
        ConsoleFactory.getInstance(Paths.get(launcherNew.getValueWithCommandLine(CONSOLE_INPUT))); // Maybe refactor and include it in the init with args[]
        launcherNew.beServerOrIgnore();

        // Initialize the encryption/decipher
        final InitAnnotationsForVersionedEncryptionClasses annotationsConfig = new InitAnnotationsForVersionedEncryptionClasses();
        final EncryptionFactory encryptionFactory = annotationsConfig.getEncryptionFactory();
        System.out.println(encryptionFactory);
        AllConsoleContexts.initContext(MAIN_CONTEXT);
        AllConsoleContexts.allContexts.get(MAIN_CONTEXT).put(encryptionFactory);
        final ResourceBundle uiMessages = ResourceBundle.getBundle("lang/ui_messages", Locale.ENGLISH);
        AllConsoleContexts.allContexts.get(MAIN_CONTEXT).put(uiMessages);
        if (Boolean.parseBoolean(InputParameters.COMMAND_LINE_MODE.getPropertyString())) {
            final LoadCredentialDataAction loadCredentialDataAction = new LoadCredentialDataAction(MAIN_CONTEXT);
            ConsoleRunner consoleRunner = new ConsoleRunner(MAIN_CONTEXT, new ConsoleItem[]{loadCredentialDataAction});
            consoleRunner.run();
        } else {
            final DebugWindow debugWindow = new DebugWindow();
            debugWindow.setVisible(true);
            AppGui.manageGui(uiMessages, debugWindow, encryptionFactory);
        }
    }

    private void beServerOrIgnore() {
        final String distantServerHostname = getValueWithCommandLine(DISTANT_SERVER_HOSTNAME);
        final String distantServerPort = getValueWithCommandLine(DISTANT_SERVER_PORT);

        if (distantServerHostname.length() > 0 && Integer.parseInt(distantServerPort) > 0) {
            ClientCredentialDialog clientCredentialDialog = new ClientCredentialDialog(null);
            clientCredentialDialog.setVisible(true);
            System.exit(-1);
        }
    }

}

package passwords;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.annotation.InitAnnotationsForVersionedEncryptionClasses;
import passwords.expectation.Expectation;
import passwords.expectation.KeysExist;
import passwords.gui.ClientCredentialDialog;
import passwords.gui.DebugWindow;
import passwords.settings.InputParameters;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

// TODO instantiate this class (or another one) in order to use attributes instead of N parameters per methods.
public final class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private final static Expectation[] expectationsCli = {
            new KeysExist()
    };

    public static void main(String... args) {
        manageArguments(args);
        beServerOrIgnore();
        final InitAnnotationsForVersionedEncryptionClasses annotationsConfig = new InitAnnotationsForVersionedEncryptionClasses();
        final EncryptionFactory encryptionFactory = annotationsConfig.getEncryptionFactory();
        final ResourceBundle uiMessages = ResourceBundle.getBundle("lang/ui_messages", Locale.ENGLISH);
        if (Boolean.parseBoolean(InputParameters.COMMAND_LINE_MODE.getPropertyString())) {
            for (Expectation expectation : expectationsCli) {
                expectation.resolve();
            }
            AppCli.commandLineInteraction(encryptionFactory);
        } else {
            final DebugWindow debugWindow = new DebugWindow();
            debugWindow.setVisible(true);
            AppGui.manageGui(uiMessages, debugWindow, encryptionFactory);
        }
    }

    private static void beServerOrIgnore() {
        if (InputParameters.DISTANT_SERVER_HOSTNAME.getPropertyString().length() > 0
                && InputParameters.DISTANT_SERVER_PORT.getPropertyInt() > 0) {
            ClientCredentialDialog clientCredentialDialog = new ClientCredentialDialog(null);
            clientCredentialDialog.setVisible(true);
            System.exit(-1);
        }
    }

    private static void manageArguments(String[] s) {
        final ResourceBundle appInfo = ResourceBundle.getBundle("app-info", Locale.ENGLISH);
        if (s.length > 0) {
            if (s[0].equalsIgnoreCase("-h")) {
                final String version = appInfo.getString("app.version");// TODO gerer le versioning pour de vrai....
                System.out.println("Version : " + version);
                System.out.println("Example : ");
                System.out.println("<this app> " + InputParameters.toCommandLineFormat());
                for (InputParameters value : InputParameters.values()) {
                    System.out.println("\t" + value.getCommandLineKey() + "\t\t" + appInfo.getString(value.getKey()));
                }
                System.exit(0);
            } else {
                for (int i = 0; i < s.length; i = i + 2) {
                    final Optional<InputParameters> inputParameter = InputParameters.fromCommandLineKey(s[i]);
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

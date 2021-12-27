package passwords;

import commons.lib.extra.gui.MessageDialog;
import commons.lib.extra.server.socket.*;
import commons.lib.main.UnrecoverableException;
import commons.lib.main.console.v3.init.CliApp;
import commons.lib.main.os.LogUtils;
import passwords.communication.shared.message.consumer.GetCredentialConsumer;
import passwords.communication.shared.message.pojo.GetCredential;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.FileAccess;
import passwords.gui.ChooseNumberDialog;
import passwords.gui.CredentialsTreeDialogv2;
import passwords.gui.DebugWindow;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Logger;

public class AppGui {
    private static final Logger logger = LogUtils.initLogs();

    public static void manageGui(ResourceBundle uiMessages, DebugWindow debugWindow, EncryptionFactory encryptionServiceFactory) {
        final CompletableFuture<CredentialsSettings> future = new CompletableFuture<>();
        final ChooseNumberDialog chooseNumberDialog = new ChooseNumberDialog(debugWindow, uiMessages, future);
        chooseNumberDialog.setVisible(true);
        final CredentialsSettings credentialsSettings;
        try {
            credentialsSettings = future.get();
            final List<CredentialDatum> allCredentials = FileAccess.decipher(encryptionServiceFactory, credentialsSettings, uiMessages);
            manageAccountsCredentials(debugWindow, encryptionServiceFactory, credentialsSettings, allCredentials, uiMessages);
            debugWindow.dispose();
        } catch (UnrecoverableException e) {
            logger.throwing("AppGui", "manageGui", e);
            if (e.getMessageInformUser() != null) {
                final MessageDialog messageDialog = new MessageDialog(null, e.getMessageInformUser());
                messageDialog.setVisible(true);
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.throwing("AppGui", "manageGui", e);
            final MessageDialog messageDialog = new MessageDialog(null, new String[]{e.getMessage()});
            messageDialog.setVisible(true);
        } catch (Throwable e) {
            logger.throwing("AppGui", "manageGui", e);
        }
    }


    private static void manageAccountsCredentials(DebugWindow debugWindow, EncryptionFactory encryptionFactory, CredentialsSettings securitySettings, List<CredentialDatum> allCredentials, ResourceBundle uiMessages) {
        final CredentialsTreeDialogv2 credentialsTreeDialog = new CredentialsTreeDialogv2(debugWindow, encryptionFactory, allCredentials, securitySettings, uiMessages);

        final int listeningPort = CliApp.PARAMETERS.fromCommandLineKey(Launcher.LISTENING_PORT).orElseThrow().getPropertyInt();
        if (listeningPort != 0) {
            final Map<Integer, Function<List<byte[]>, Wrapper>> wrappers = new HashMap<>();
            wrappers.put(GetCredential.CODE, bytes -> new Wrapper(
                    GetCredential.CODE,
                    new GetCredential(
                            Message.bytesToString(bytes.get(1)),
                            Message.bytesToInt(bytes.get(2)),
                            Message.bytesToBool(bytes.get(3)),
                            Message.bytesToString(bytes.get(4))
                    )));
            final WrapperFactory wrapperFactory = new WrapperFactory(wrappers);
            final MessageConsumerManager messageConsumerManager = new MessageConsumerManager();
            messageConsumerManager.register(GetCredential.CODE, new GetCredentialConsumer(allCredentials));
            final Server server = new Server("localhost", listeningPort, 1, messageConsumerManager, wrapperFactory);
            try {
                server.listen();
            } catch (IOException e) {
                logger.throwing("AppGui", "manageAccountsCredentials",  e);
            }
        } else {
            credentialsTreeDialog.setVisible(true);
        }
    }

}

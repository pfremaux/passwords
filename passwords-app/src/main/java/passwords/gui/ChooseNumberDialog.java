package passwords.gui;

import commons.lib.extra.gui.Positioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.expectation.Expectation;
import passwords.expectation.KeysExist;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class ChooseNumberDialog extends Dialog {

    private final Logger logger = LoggerFactory.getLogger(ChooseNumberDialog.class);
    private final static Expectation[] expectations = {
            new KeysExist()
    };

    private CredentialsSettings credentialsSettings;

    public ChooseNumberDialog(Frame owner, ResourceBundle uiMessages, CompletableFuture<CredentialsSettings> completableFuture) throws HeadlessException {
        super(owner);
        for (Expectation expectation : expectations) {
            expectation.resolve();
        }
        setLayout(null);
        final Positioner positioner = new Positioner();
        final String keyNumberChoiceMsg = uiMessages.getString("symmetric.key.number.choice");
        positioner.addLabel(
                keyNumberChoiceMsg,
                keyNumberChoiceMsg.length() * 8,
                Positioner.DEFAULT_BUTTON_HEIGHT);
        final Path keysPath = InputParameters.KEYS_DIR.getPropertyPath();

        positioner.newLine();
        for (int i = 0; i < 20; i++) {
            if (i % 10 == 0) {
                positioner.newLine();
            }
            final int j = i;
            positioner.addButton(Integer.toString(i), 15, Positioner.DEFAULT_BUTTON_HEIGHT, actionEvent -> {
                final DecryptDialog decryptDialog = new DecryptDialog(
                        owner,
                        j,
                        uiMessages);
                decryptDialog.setModal(true);
                decryptDialog.setVisible(true);
                logger.debug("decryptDialog closed");
                final List<TextField> symmetricKeysGui = decryptDialog.getSymmetricKeysGui();
                final List<String> passwords = new ArrayList<>();
                for (TextField textField : symmetricKeysGui) {
                    String text = textField.getText();
                    passwords.add(text);
                }
                for (String pwd : passwords) {
                    logger.debug(pwd);
                }
                CredentialSettingsManager credentialSettingsManager = new CredentialSettingsManager();
                this.credentialsSettings = credentialSettingsManager.getCredentialsSettings(keysPath, passwords, decryptDialog.getKeyPairIndexes());
                completableFuture.complete(credentialsSettings);
                this.dispose();
            });
        }
        positioner.addAllToDialog(this);
        setBounds(positioner.getWindowBound(100, 100));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

}

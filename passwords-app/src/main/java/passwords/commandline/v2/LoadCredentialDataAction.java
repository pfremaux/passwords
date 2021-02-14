package passwords.commandline.v2;

import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import passwords.commandline.v1.ChooseNumberCommandLine;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.FileAccess;
import passwords.encryption.annotation.InitAnnotationsForVersionedEncryptionClasses;
import passwords.gui.CredentialsTreeDialogv2;
import passwords.pojo.CredentialDatum;
import commons.lib.NodeV2;
import passwords.settings.CredentialsSettings;

import java.util.List;
import java.util.Optional;

public class LoadCredentialDataAction extends ConsoleAction {

    public LoadCredentialDataAction() {
        super("LOAD");
    }

    @Override
    public ConsoleItem[] go() {
        final ChooseNumberCommandLine chooseNumberCommandLine = new ChooseNumberCommandLine();
        final Optional<CredentialsSettings> credentialsSettings = chooseNumberCommandLine.readAndGetCredentialsSettings();
        if (credentialsSettings.isEmpty()) {
            logger.severe("No credential settings loaded. Can't continue.");
            SystemUtils.failUser();
        }
        final InitAnnotationsForVersionedEncryptionClasses annotationsConfig = new InitAnnotationsForVersionedEncryptionClasses();
        final EncryptionFactory encryptionFactory = annotationsConfig.getEncryptionFactory();
        final List<CredentialDatum> credentialData = FileAccess.decipher(encryptionFactory, credentialsSettings.get(), null);
        NodeV2<CredentialDatum> target = NodeV2.root();
        CredentialsTreeDialogv2.credentialDataToNodes(credentialData, target);
        ConsoleContext.workingObject = target;
        ConsoleContext.parentMenuStack.push(ConsoleContext.currentMenu);
        return new ConsoleItem[] {
                new SaveCredentialDataAction(),
                new ListCredentialAction(),
                new CreateCredentialDataAction()
        };
    }
}

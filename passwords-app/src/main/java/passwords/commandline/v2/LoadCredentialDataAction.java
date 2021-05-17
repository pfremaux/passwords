package passwords.commandline.v2;

import commons.lib.NodeV2;
import commons.lib.main.SystemUtils;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;
import passwords.commandline.v1.ChooseNumberCommandLine;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.FileAccess;
import passwords.gui.CredentialsTreeDialogv2;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoadCredentialDataAction extends ConsoleAction {

    private String contextName;

    public LoadCredentialDataAction(String contextName) {
        super("LOAD");
        this.contextName = contextName;
    }

    @Override
    public ConsoleItem[] go() {
        final ChooseNumberCommandLine chooseNumberCommandLine = new ChooseNumberCommandLine();
        final Optional<CredentialsSettings> credentialsSettings = chooseNumberCommandLine.readAndGetCredentialsSettings();
        if (credentialsSettings.isEmpty()) {
            logger.severe("No credential settings loaded. Can't continue.");
            SystemUtils.failUser();
        }
        final ConsoleContext consoleContext = AllConsoleContexts.allContexts.get(contextName);
        final EncryptionFactory encryptionFactory = consoleContext.get(EncryptionFactory.class);
        final ResourceBundle messages = consoleContext.get(ResourceBundle.class);
        final List<CredentialDatum> credentialData = FileAccess.decipher(
                encryptionFactory,
                credentialsSettings.get(),
                messages);
        NodeV2<CredentialDatum> target = NodeV2.root();
        CredentialsTreeDialogv2.credentialDataToNodes(credentialData, target);
        consoleContext.workingObject = target;
        consoleContext.parentMenuStack.push(consoleContext.currentMenu);
        consoleContext.put(credentialsSettings.get());
        return new ConsoleItem[] {
                new CreateCredentialDataAction(contextName),
                new ListCredentialAction(contextName),
                new SaveCredentialDataAction(contextName),
                 ConsoleNavigation.GO_BACK.withContextName(contextName)
        };
    }
}

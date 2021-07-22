package passwords.commandline.v2;

import commons.lib.NodeV2;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;
import commons.lib.main.os.LogUtils;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.FileAccess;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;

import java.util.*;

public class SaveCredentialDataAction extends ConsoleAction {

    private String contextName;

    public SaveCredentialDataAction(String contextName) {
        super("Save");
        this.contextName = contextName;
    }

    @Override
    public int ordering() {
        return 0;
    }

    @Override
    public ConsoleItem[] go() {
        final EncryptionFactory encryptionFactory = AllConsoleContexts.allContexts.get(contextName).get(EncryptionFactory.class);
        final ConsoleContext consoleContext = AllConsoleContexts.allContexts.get(contextName);
        final NodeV2<CredentialDatum> credentials  = (NodeV2<CredentialDatum>) consoleContext.workingObject;
        final List<CredentialDatum> credentialData = flattenAllNodes(credentials);
        final ResourceBundle bundle = consoleContext.get(ResourceBundle.class);
        final CredentialsSettings settings = consoleContext.get(CredentialsSettings.class);
        credentialData.forEach(c -> LogUtils.initLogs().debug("hierarchy de " + c + " : " + c.getHierarchy()));
        FileAccess.cipher(credentialData, encryptionFactory, settings, bundle);
        return consoleContext.currentMenu;
    }

    private List<CredentialDatum> flattenAllNodes(NodeV2<CredentialDatum> rootNode) {
        Map<String, CredentialDatum> children = rootNode.getHierarchy(Collections.emptyList());
        for (Map.Entry<String, CredentialDatum> entry : children.entrySet()) {
            entry.setValue(entry.getValue().move(entry.getKey()));
        }
        return new ArrayList<>(children.values());
    }
}

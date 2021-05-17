package passwords.commandline.v2;

import commons.lib.NodeV2;
import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import passwords.pojo.CredentialDatum;

public class CreateCredentialDataAction extends ConsoleAction {

    private String contextName;

    public CreateCredentialDataAction(String contextName) {
        super("Create");
        this.contextName = contextName;
    }

    @Override
    public int ordering() {
        return 0;
    }

    @Override
    public ConsoleItem[] go() {
        final String currentHierarchy = AllConsoleContexts.allContexts.get(contextName).cache.get("currentHierarchy"); /// TODO update hierarchy through navigating in CRUD
        ConsoleFactory.getInstance().printf("url");
        final String url = ConsoleFactory.getInstance().readLine();
        ConsoleFactory.getInstance().printf("login");
        final String login = ConsoleFactory.getInstance().readLine();
        ConsoleFactory.getInstance().printf("passw");
        final char[] password = ConsoleFactory.getInstance().readPassword();
        ConsoleFactory.getInstance().printf("Description");
        final String description = ConsoleFactory.getInstance().readLine();
        final CredentialDatum credentialDatum = new CredentialDatum(currentHierarchy, url, login, new String(password), description);
        // TODO WIP
        NodeV2<CredentialDatum> root = (NodeV2<CredentialDatum>) AllConsoleContexts.allContexts.get(contextName).workingObject;
        root.addLeaf(credentialDatum);
        //root.
        // CredentialDatumForConsole credentialDatumForConsole = new CredentialDatumForConsole(ConsoleFactory.getInstance(), credentialDatum);

        return AllConsoleContexts.allContexts.get(contextName).currentMenu;
    }
}

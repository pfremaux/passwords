package passwords.commandline.v2;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import passwords.pojo.CredentialDatum;

public class CreateCredentialDataAction extends ConsoleAction {

    public CreateCredentialDataAction() {
        super("Create");
    }

    @Override
    public int ordering() {
        return 0;
    }

    @Override
    public ConsoleItem[] go() {
        String currentHierarchy = ConsoleContext.cache.get("currentHierarchy"); /// TODO update hierarchy through navigating in CRUD
        ConsoleFactory.getInstance().printf("url");
        String url = ConsoleFactory.getInstance().readLine();
        ConsoleFactory.getInstance().printf("login");
        String login = ConsoleFactory.getInstance().readLine();
        ConsoleFactory.getInstance().printf("passw");
        char[] password = ConsoleFactory.getInstance().readPassword();
        ConsoleFactory.getInstance().printf("Description");
        String description = ConsoleFactory.getInstance().readLine();
        CredentialDatum credentialDatum = new CredentialDatum(currentHierarchy, url, login, new String(password), description);
        //CredentialDatumForConsole credentialDatumForConsole = new CredentialDatumForConsole(ConsoleFactory.getInstance(), credentialDatum);

        return ConsoleContext.currentMenu;
    }
}

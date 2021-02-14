package passwords.pojo;

import commons.lib.main.console.CustomConsole;
import commons.lib.main.console.item.DescriptibleConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleContext;

public class CredentialDatumForConsole extends DescriptibleConsoleItem {
    // TODO WIP
    private CredentialDatum credentialDatum;

    public CredentialDatumForConsole(CustomConsole console, CredentialDatum credentialDatum) {
        super(console);
        this.credentialDatum = credentialDatum;
        this.id(credentialDatum.getHierarchy() + ">" + credentialDatum.getDisplayableInfo());
        this.humanId();
        this.name(credentialDatum.getUrl());
    }

    @Override
    public DescriptibleConsoleItem interactiveInit() {
        getConsole().printf("URL ?");
        String url = getConsole().readLine();
        getConsole().printf("Login ?");
        String login = getConsole().readLine();
        getConsole().printf("Comment ?");
        String comments = getConsole().readLine();
        getConsole().printf("Password ?");
        char[] password = getConsole().readPassword();
        this.credentialDatum = new CredentialDatum("not set", url, login, new String(password), comments);
        //(Node<CredentialDatum>) ConsoleContext.cache.get("creds");
        return this;
    }


    public CredentialDatum getCredentialDatum() {
        return credentialDatum;
    }
}

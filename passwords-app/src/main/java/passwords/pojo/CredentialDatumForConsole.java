package passwords.pojo;

import commons.lib.console.CustomConsole;
import commons.lib.console.v2.item.DescriptibleConsoleItem;

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
        String login = getConsole().readLine();
        String comments = getConsole().readLine();
        char[] password = getConsole().readPassword();
        this.credentialDatum = new CredentialDatum("root", url, login, new String(password), comments);
        return this;
    }


}

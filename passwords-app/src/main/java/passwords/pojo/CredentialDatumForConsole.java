package passwords.pojo;

import commons.lib.SystemUtils;
import commons.lib.UnrecoverableException;
import commons.lib.console.CustomConsole;
import commons.lib.console.v2.item.DescriptibleConsoleItem;

public class CredentialDatumForConsole extends DescriptibleConsoleItem {

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
        throw new UnrecoverableException("Not yet implemented", "Sorry, feature not available.", SystemUtils.EXIT_PROGRAMMER_ERROR);
       // return null;
    }


}

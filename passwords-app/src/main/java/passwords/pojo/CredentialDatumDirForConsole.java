package passwords.pojo;

import commons.lib.main.SystemUtils;
import commons.lib.main.UnrecoverableException;
import commons.lib.main.console.CustomConsole;
import commons.lib.main.console.item.DescriptibleConsoleItem;

import java.util.UUID;

public class CredentialDatumDirForConsole extends DescriptibleConsoleItem {

    private String name;
    private CredentialDatum credentialDatum;

    public CredentialDatumDirForConsole(CustomConsole console, String name, CredentialDatum credentialDatum) {
        super(console);
        this.name = name;
        this.credentialDatum = credentialDatum;
        this.id(UUID.randomUUID().toString());
        this.humanId(name);
        this.name(name);
    }

    @Override
    public DescriptibleConsoleItem interactiveInit() {
        throw new UnrecoverableException("Not yet implemented", "Sorry, feature not available.", SystemUtils.EXIT_PROGRAMMER_ERROR);
        // return null;
    }

    public CredentialDatum getCredentialDatum() {
        return credentialDatum;
    }
}

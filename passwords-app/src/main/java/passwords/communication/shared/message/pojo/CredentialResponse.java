package passwords.communication.shared.message.pojo;


import commons.lib.server.socket.Message;
import passwords.pojo.CredentialDatum;

import java.util.List;

public class CredentialResponse extends Message {
    public static final Integer CODE = 2;
    private CredentialDatum credentialDatum;

    public CredentialResponse(
            String responseHostname,
            int responsePort,
            boolean requireResponse,
            CredentialDatum credentialDatum) {
        super(responseHostname, responsePort, requireResponse);
        this.credentialDatum = credentialDatum;
    }

    public CredentialResponse(List<String> list) {
        super(list.get(1), Integer.parseInt(list.get(2)), Boolean.parseBoolean(list.get(3)));
        String url = list.get(4);
        String login = list.get(5);
        String password = list.get(6);
        this.credentialDatum = new CredentialDatum("", url, login, password, "");
    }

    @Override
    public String[] serializeStrings() {
        return new String[]{
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse()),
                credentialDatum.getUrl(),
                credentialDatum.getLogin(),
                credentialDatum.getPassword()
        };
    }

    public CredentialDatum getCredentialDatum() {
        return credentialDatum;
    }
}

package passwords.communication.shared.message.pojo;

import commons.lib.server.socket.Message;

import java.util.List;

public class GetCredential extends Message {
    public static final Integer CODE = 1;
    private final String url;

    public GetCredential(
            String responseHostname,
            int responsePort,
            boolean requireResponse,
            String url) {
        super(responseHostname, responsePort, requireResponse);
        this.url = url;
    }

    public GetCredential(List<String> strings) {
        super(
                strings.get(1),
                Integer.parseInt(strings.get(2)),
                Boolean.parseBoolean(strings.get(3)));
        this.url = strings.get(4);
    }

    @Override
    public String[] serializeStrings() {
        return new String[]{
                getResponseHostname(),
                Integer.toString(getResponsePort()),
                Boolean.toString(isRequireResponse()),
                url
        };
    }

    public String getUrl() {
        return url;
    }
}

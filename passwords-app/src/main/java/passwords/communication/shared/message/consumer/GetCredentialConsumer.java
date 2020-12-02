package passwords.communication.shared.message.consumer;

import commons.lib.extra.server.socket.MessageConsumer;
import commons.lib.extra.server.socket.Wrapper;
import passwords.communication.shared.message.pojo.CredentialResponse;
import passwords.communication.shared.message.pojo.GetCredential;
import passwords.pojo.CredentialDatum;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GetCredentialConsumer implements MessageConsumer {

    private final List<CredentialDatum> credentialData;

    public GetCredentialConsumer(List<CredentialDatum> credentialData) {
        this.credentialData = Collections.unmodifiableList(credentialData);
    }

    @Override
    public Optional<Wrapper> process(Wrapper inputWrapper, String consumerHostname, int consumerPort) {
        GetCredential getCredential = (GetCredential) inputWrapper.getDatum();
        String url = getCredential.getUrl();
        Optional<CredentialDatum> credentialDatum = credentialData.stream().filter(cd -> cd.getUrl().equalsIgnoreCase(url)).findFirst();
        if (credentialDatum.isPresent()) {
            final Wrapper outputWrapper = new Wrapper(
                    CredentialResponse.CODE,
                    new CredentialResponse(
                            consumerHostname,
                            consumerPort,
                            false,
                            credentialDatum.get())
            );
            return Optional.of(outputWrapper);
        } else {
            return Optional.empty();
        }
    }
}

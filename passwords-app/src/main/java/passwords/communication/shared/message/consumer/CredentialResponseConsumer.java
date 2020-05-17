package passwords.communication.shared.message.consumer;

import commons.lib.server.socket.MessageConsumer;
import commons.lib.server.socket.Wrapper;
import passwords.communication.shared.message.pojo.CredentialResponse;
import passwords.pojo.CredentialDatum;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CredentialResponseConsumer implements MessageConsumer {

    private final CompletableFuture<CredentialDatum> credentialDatumCompletableFuture;

    public CredentialResponseConsumer(CompletableFuture<CredentialDatum> credentialDatumCompletableFuture) {
        this.credentialDatumCompletableFuture = credentialDatumCompletableFuture;
    }

    @Override
    public Optional<Wrapper> process(Wrapper inputWrapper, String consumerHostname, int consumerPort) {
        CredentialResponse datum = (CredentialResponse) inputWrapper.getDatum();
        CredentialDatum credentialDatum = datum.getCredentialDatum();
        if (credentialDatum != null) {
            credentialDatumCompletableFuture.complete(credentialDatum);
        }
        return Optional.empty();

    }
}

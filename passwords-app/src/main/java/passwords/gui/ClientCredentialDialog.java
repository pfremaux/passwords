package passwords.gui;

import commons.lib.gui.Positioner;
import commons.lib.server.socket.*;
import passwords.communication.shared.message.consumer.CredentialResponseConsumer;
import passwords.communication.shared.message.pojo.CredentialResponse;
import passwords.communication.shared.message.pojo.GetCredential;
import passwords.pojo.CredentialDatum;
import passwords.settings.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static commons.lib.gui.Positioner.DEFAULT_BUTTON_HEIGHT;

public class ClientCredentialDialog extends Dialog {
    private static final Logger logger = LoggerFactory.getLogger(ClientCredentialDialog.class);
    private final Positioner positioner = new Positioner();

    public ClientCredentialDialog(Frame owner) {
        super(owner);
        setModal(true);
        positioner.addLabel("Url", 300, Positioner.DEFAULT_TEXTFIELD_HEIGHT);
        TextField textField = positioner.addTextField(300, Positioner.DEFAULT_TEXTFIELD_HEIGHT);
        positioner.newLine();
        positioner.addButton("Get credential", 500, Positioner.DEFAULT_TEXTFIELD_HEIGHT, event -> {
            Client client = new Client(
                    InputParameters.DISTANT_SERVER_HOSTNAME.getPropertyString(),
                    InputParameters.DISTANT_SERVER_PORT.getPropertyInt());
            final GetCredential getCredential = new GetCredential("localhost", 9999, true, textField.getText());
            final Wrapper wrapper = new Wrapper(GetCredential.CODE, getCredential);
            byte[] getCredentialBytes = wrapper.serialize();

            final MessageConsumerManager messageConsumerManager = new MessageConsumerManager();
            final CompletableFuture<CredentialDatum> completableFuture = new CompletableFuture<>();
            messageConsumerManager.register(CredentialResponse.CODE, new CredentialResponseConsumer(completableFuture));
            final Map<Integer, Function<List<String>, Wrapper>> wrappers = new HashMap<>();
            wrappers.put(CredentialResponse.CODE, strings -> new Wrapper(CredentialResponse.CODE, new CredentialResponse(strings)));
            WrapperFactory wrapperFactory = new WrapperFactory(wrappers);
            final Server server = new Server("localhost", 9999, 1, messageConsumerManager, wrapperFactory);

            try {
                client.connectSendClose(getCredentialBytes);
                server.listen();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            try {
                CredentialDatum credentialDatum = completableFuture.get();
                TextArea result = positioner.getComponentByName("result", TextArea.class);
                result.setText(credentialDatum.getLogin() + "\n" + credentialDatum.getPassword());
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        });

        positioner.newLine();
        final TextArea textArea = positioner.addTextArea("", 500, DEFAULT_BUTTON_HEIGHT);
        textArea.setName("result");

        positioner.addAllToDialog(this);
        this.pack();
        setBounds(positioner.getWindowBound(100, 100));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }
}

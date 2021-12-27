package passwords.gui;

import commons.lib.extra.gui.Positioner;
import commons.lib.extra.server.socket.*;
import commons.lib.main.os.LogUtils;
import passwords.communication.shared.message.consumer.CredentialResponseConsumer;
import passwords.communication.shared.message.pojo.CredentialResponse;
import passwords.communication.shared.message.pojo.GetCredential;
import passwords.pojo.CredentialDatum;
import passwords.settings.InputParameters;

import javax.crypto.NoSuchPaddingException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Logger;

import static commons.lib.extra.gui.Positioner.DEFAULT_BUTTON_HEIGHT;

public class ClientCredentialDialog extends Dialog {
    private static final Logger logger = LogUtils.initLogs();
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
            final Map<Integer, Function<List<byte[]>, Wrapper>> wrappers = new HashMap<>();
            wrappers.put(CredentialResponse.CODE, strings -> new Wrapper(CredentialResponse.CODE, new CredentialResponse(
                    Message.bytesToString(strings.get(1)),
                    Message.bytesToInt(strings.get(2)),
                    Message.bytesToBool(strings.get(3)),
                    new CredentialDatum(
                            "",
                            Message.bytesToString(strings.get(4)),
                            Message.bytesToString(strings.get(5)),
                            Message.bytesToString(strings.get(6)),
                            ""
                    )

            )));
            WrapperFactory wrapperFactory = new WrapperFactory(wrappers);
            final Server server = new Server("localhost", 9999, 1, messageConsumerManager, wrapperFactory);

            try {
                client.connectSendClose(getCredentialBytes);
                server.listen();
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                logger.throwing("ClientCredentialDialog", "ClientCredentialDialog",  e);
            }
            try {
                CredentialDatum credentialDatum = completableFuture.get();
                TextArea result = positioner.getComponentByName("result", TextArea.class);
                result.setText(credentialDatum.getLogin() + "\n" + credentialDatum.getPassword());
            } catch (InterruptedException | ExecutionException e) {
                logger.throwing("ClientCredentialDialog", "ClientCredentialDialog",  e);
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

package passwords.gui;

import commons.lib.extra.gui.Positioner;
import passwords.settings.InputParameters;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static commons.lib.extra.gui.Positioner.*;

final class DecryptDialog extends Dialog {
    private final List<Integer> keyPairIndexes = new ArrayList<>();
    private List<TextField> symmetricKeysGui = new ArrayList<>();

    DecryptDialog(Frame owner, int cycleNumber, ResourceBundle uiMessages) {
        super(owner);
        final Positioner modalPositioner = new Positioner();
        for (int i = 0; i < InputParameters.NBR_KEYPAIRS.getPropertyInt(); i++) {
            final int j = i;
            modalPositioner.addButton(Integer.toString(i), 15, DEFAULT_BUTTON_HEIGHT, e -> keyPairIndexes.add(j));
        }
        modalPositioner.newLine();
        for (int i = 0; i < cycleNumber; i++) {
            modalPositioner.addLabel(
                    uiMessages.getString("symmetric.key.lbl.password.nbr") + i + " :", DEFAULT_LABEL_WIDTH, DEFAULT_TEXTFIELD_HEIGHT);
            final TextField textField = modalPositioner.addTextField(200, DEFAULT_TEXTFIELD_HEIGHT);
            textField.setEchoChar('*');
            symmetricKeysGui.add(textField);
            modalPositioner.newLine();
        }

        modalPositioner.newLine();
        modalPositioner.addButton(
                uiMessages.getString("symmetric.key.btn.decrypt"),
                100,
                DEFAULT_BUTTON_HEIGHT,
                e -> this.setVisible(false));
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                setVisible(false);
            }
        });
        this.setBounds(modalPositioner.getWindowBound(200, 200));
        modalPositioner.addAllToDialog(this);
    }

    List<Integer> getKeyPairIndexes() {
        return keyPairIndexes;
    }


    List<TextField> getSymmetricKeysGui() {
        return symmetricKeysGui;
    }
}

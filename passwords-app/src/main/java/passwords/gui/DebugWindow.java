package passwords.gui;

import commons.lib.UnrecoverableException;
import commons.lib.gui.Positioner;
import passwords.settings.InputParameters;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO useless but now is the mother frame
public class DebugWindow extends Frame {

    public DebugWindow() throws HeadlessException {
        final Path saveDir = InputParameters.SAVE_DIR.getPropertyPath();
        Positioner positioner
                = new Positioner();
        setLayout(null);
        positioner.addButton("Export settings",
                150,
                Positioner.DEFAULT_BUTTON_HEIGHT,
                e -> {
                    try {
                        Files.writeString(
                                saveDir.resolve(Paths.get("config.properties")),
                                InputParameters.toPropertiesFileFormat());
                    } catch (IOException e1) {
                        throw new UnrecoverableException("Writing error ", "Can't export the configuration", e1, -2);
                    }
                });
        positioner.addAllToPanel(this);
        setBounds(positioner.getWindowBound(100, 100));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }
}

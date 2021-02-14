package passwords.commandline.v2;

import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;

public class SaveCredentialDataAction extends ConsoleAction {

    public SaveCredentialDataAction() {
        super("Save");
    }

    @Override
    public int ordering() {
        return 0;
    }

    @Override
    public ConsoleItem[] go() {
        return new ConsoleItem[0];
    }
}

package passwords.commandline.v2;

import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleRunner;
import passwords.commandline.v2.libcommon.NavigateNodeV2;
import passwords.pojo.CredentialDatum;
import passwords.pojo.NodeV2;

public class ListCredentialAction extends ConsoleAction {

    public ListCredentialAction() {
        super("LIST");
    }

    @Override
    public ConsoleItem[] go() {
        NodeV2<CredentialDatum> credentialData = (NodeV2<CredentialDatum>) ConsoleContext.workingObject;
        ConsoleRunner consoleRunner = new ConsoleRunner(new NavigateNodeV2<>("Root", credentialData).navigate());
        consoleRunner.run();
        return ConsoleContext.parentMenuStack.pop();
    }

}

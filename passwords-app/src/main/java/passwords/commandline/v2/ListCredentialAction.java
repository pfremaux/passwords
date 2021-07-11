package passwords.commandline.v2;

import commons.lib.NodeV2;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleRunner;
import commons.lib.main.console.v3.interaction.NavigateNodeV2;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import commons.lib.main.console.v3.interaction.context.ConsoleContext;
import passwords.pojo.CredentialDatum;

public class ListCredentialAction extends ConsoleAction {

    private String contextName;

    public ListCredentialAction(String contextName) {
        super("LIST");
        this.contextName = contextName;
    }

    @Override
    public ConsoleItem[] go() {
        final NodeV2<CredentialDatum> credentialData = (NodeV2<CredentialDatum>) AllConsoleContexts.allContexts.get(contextName).workingObject;
        final String listNodes = "listNodes";
        ConsoleContext consoleContext = new ConsoleContext();
        consoleContext.workingObject = credentialData;

        AllConsoleContexts.allContexts.put(listNodes, consoleContext);
        final ConsoleRunner consoleRunner = new ConsoleRunner(listNodes, new NavigateNodeV2<>(listNodes, "Root", credentialData).navigate());
        consoleRunner.run();
        return AllConsoleContexts.allContexts.get(contextName).currentMenu;
    }

}

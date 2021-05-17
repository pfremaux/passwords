package passwords.commandline.v2;

import commons.lib.NodeV2;
import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;
import commons.lib.main.console.v3.interaction.context.AllConsoleContexts;
import passwords.pojo.CredentialDatum;

import java.util.ArrayList;
import java.util.List;

public class NavigateCredentialDataAction extends ConsoleAction {

    private final String contextName;

    public NavigateCredentialDataAction(String contextName, String label) {
        super(label);
        this.contextName = contextName;
    }

    @Override
    public int ordering() {
        return 0;
    }

    @Override
    public ConsoleItem[] go() {
        final NodeV2<CredentialDatum> root = (NodeV2<CredentialDatum>) AllConsoleContexts.allContexts.get(contextName).workingObject;
        // root.getChildren();
        final List<ConsoleItem> menu = new ArrayList<>();
        for (NodeV2<CredentialDatum> child : root.getChildren()) {
            if (child.isLeaf()) {
                menu.add(readCredentialAction(child));
            } else {
                menu.add(navigateCredentialChildDir(child));
            }
        }
        //Crud<CredentialDatumForConsole> credentialDatumForConsoleCrud = new Crud<>(ConsoleFactory.getInstance(), new ArrayList<>());
        return menu.toArray(new ConsoleItem[0]);
    }

    private ConsoleNavigation navigateCredentialChildDir(NodeV2<CredentialDatum> child) {
        return new ConsoleNavigation(contextName, child.getNodeName()) {
            @Override
            public ConsoleItem[] navigate() {
                return child.getChildren().stream()
                        .map(node -> {
                            if (node.isLeaf()) {
                                return readCredentialAction(node);
                            } else {
                                return navigateCredentialChildDir(node);
                            }
                        }).toArray(ConsoleItem[]::new);
            }
        };
    }

    private ConsoleAction readCredentialAction(NodeV2<CredentialDatum> child) {
        return new ConsoleAction(child.getNodeName()) {
            @Override
            public ConsoleItem[] go() {
                System.out.println("todo");
                return AllConsoleContexts.allContexts.get(contextName).currentMenu;
            }
        };
    }
}

package passwords.commandline.v2.libcommon;

import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;
import passwords.pojo.NodeV2;

import java.util.ArrayList;
import java.util.List;

// TODO MOVE IN LIB COMMON
public class NavigateNodeV2<T> extends ConsoleNavigation {

    private NodeV2<T> node;

    public NavigateNodeV2(String label, NodeV2<T> node) {
        super(label);
        if (node.isLeaf()) {
            // TODO ERROR
        }
        this.node = node;
    }

    @Override
    public ConsoleItem[] navigate() {
        final List<ConsoleItem> consoleItems = new ArrayList<>();
        for (NodeV2<T> nodeV2 : node.getChildren()) {
            if (nodeV2.isLeaf()) {
                consoleItems.add(new ActionNodeV2<T>(nodeV2.getNodeName(), nodeV2));
            } else {
                consoleItems.add(new NavigateNodeV2<T>(nodeV2.getNodeName(), nodeV2));
            }
        }
        if (!ConsoleContext.parentMenuStack.empty()) {
            consoleItems.add(GO_BACK);
        }
        ConsoleContext.currentMenu = consoleItems.toArray(new ConsoleItem[0]);
        return ConsoleContext.currentMenu;
    }

}

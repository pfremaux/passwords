package passwords.commandline.v2.libcommon;

import commons.lib.main.console.v3.interaction.ConsoleAction;
import commons.lib.main.console.v3.interaction.ConsoleContext;
import commons.lib.main.console.v3.interaction.ConsoleItem;
import commons.lib.main.console.v3.interaction.ConsoleNavigation;
import passwords.pojo.NodeV2;

import java.util.ArrayList;
import java.util.List;

// TODO MOVE IN LIB COMMON
public class ActionNodeV2<T> extends ConsoleAction {

    private NodeV2<T> node;

    public ActionNodeV2(String label, NodeV2<T> node) {
        super(label);
        if (!node.isLeaf()) {
            // TODO ERROR
        }
        this.node = node;
    }

    @Override
    public ConsoleItem[] go() {
        System.out.println("action " + node.toString());
        return ConsoleContext.currentMenu;
    }
}

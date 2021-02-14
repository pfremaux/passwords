package passwords.pojo;

import commons.lib.main.console.ConsoleFactory;
import commons.lib.main.console.v3.interaction.ConsoleRunner;
import passwords.commandline.v2.libcommon.NavigateNodeV2;

import java.util.Arrays;

public class TestNodev2 {

    public static void main(String[] args) {
        ConsoleFactory.getInstance(Arrays.asList("1", "1", "4", "3"));
        NodeV2<Integer> nodeRoot = NodeV2.root();
        NodeV2<Integer> rep1 = nodeRoot.addDir("rep1...");
        NodeV2<Integer> rep2 = nodeRoot.addDir("rep2...");
        rep1.addLeaf(10);
        rep1.addLeaf(11);
        rep1.addDir("rep11...").addLeaf(111);
        rep2.addLeaf(22);
        NodeV2<Integer> result = nodeRoot.getNode(Arrays.asList("rep1...", "rep11...", "111"));
        System.out.println(result.getNodeName());
        ConsoleRunner consoleRunner = new ConsoleRunner(new NavigateNodeV2<>("Root", nodeRoot).navigate());
        consoleRunner.run();
    }
}

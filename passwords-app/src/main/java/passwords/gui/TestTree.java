package passwords.gui;

import commons.lib.extra.gui.Positioner;
import passwords.pojo.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TestTree extends Dialog {

    static class Toto {
        final int i;
        final int j;

        public Toto(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return "Toto{" +
                    "i=" + i +
                    ", j=" + j +
                    '}';
        }
    }

    public TestTree(Frame owner) {
        super(owner);
        setModal(true);
        Positioner positioner = new Positioner();
        final Node<Toto> rootNode = new Node<>("Root", null, new ArrayList<>());
        Toto toto = new Toto(1, 1);
        Toto toto2 = new Toto(2, 2);
        rootNode.getOrCreate(toto.toString(), toto);
        Node<Toto> toto2Node = rootNode.getOrCreate(toto2.toString(), toto2);
        toto2Node.getOrCreate(toto.toString(), toto);
        positioner.addTree(rootNode.getTreeNodeInstance(true), 400, 400, e -> {
            final JTree source = (JTree) e.getSource();
            System.out.println(source);
        });
        positioner.endCreation(this, "my title");
    }

    public static void main(String[] args) {
        TestTree testTree = new TestTree(null);
        testTree.setVisible(true);
    }

}

package passwords.pojo;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class NodeTest extends TestCase {

    private Node<Integer> root;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root = new Node<>("GrandParent", 1900);
        Node<Integer> aunt = root.getOrCreate("Aunt", 1925);
        Node<Integer> father = root.getOrCreate("Father", 1927);
        Node<Integer> character = father.getOrCreate("character", 1950);
        Node<Integer> sister = father.getOrCreate("sister", 1952);
        Node<Integer> son = character.getOrCreate("son", 1970);
    }

    @Test
    public void testGetChildrenV2() {
        Map<String, Integer> myRoot = root.getChildrenV2(root.getName());
        System.out.println(myRoot);
        final List<Map.Entry<String, Integer>> entries = new ArrayList<>(myRoot.entrySet());
        entries.sort(Comparator.comparingInt(o -> o.getKey().length()));
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        // TODO finish unit test
    }

}
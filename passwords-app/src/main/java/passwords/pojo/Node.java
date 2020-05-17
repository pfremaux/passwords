package passwords.pojo;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node<T> {

    public static final String EMPTY = ".";

    private final Node<T> parent;
    /**
     * The displayed name.
     */
    private final String name;
    /**
     * The children nodes. (if allowed)
     */
    private final List<Node<T>> subValues;
    private final Map<String, Node<T>> valuesMapped;
    private T value;

    public Node(String name, T value, List<Node<T>> subValues) {
        this(null, name, value, subValues);
    }

    public Node(Node<T> parent, String name, T value, List<Node<T>> subValues) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.subValues = subValues;
        this.valuesMapped = new HashMap<>();
        for (Node<T> v : subValues) {
            valuesMapped.put(v.getName(), v);
        }
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public List<Node<T>> getSubValues() {
        return subValues;
    }

    public Node<T> getOrCreate(String hierarchyLevelName, T nodeValue) {
        final Node<T> tNode = valuesMapped.get(hierarchyLevelName);
        if (tNode == null) {
            Node<T> tNode1 = new Node<T>(this, hierarchyLevelName, nodeValue, new ArrayList<>());
            subValues.add(tNode1);
            valuesMapped.put(hierarchyLevelName, tNode1);
            return tNode1;
        } else {
            return tNode;
        }
    }

    public DefaultMutableTreeNode getTreeNodeInstance(boolean allowChildren) {
        final DefaultMutableTreeNode currentNode;
        currentNode = new DefaultMutableTreeNode(this, allowChildren);

        for (Node<T> subValue : getSubValues()) {
            currentNode.add(subValue.getTreeNodeInstance(!getSubValues().isEmpty()));
        }
        return currentNode;
    }

    public boolean isLeaf() {
        return this.value != null;
    }

    public Map<String, T> getChildren(String parent) {
        final Map<String, T> map = new HashMap<>();
        for (Node<T> subValue : this.getSubValues()) {
            if (subValue.isLeaf()) {
                map.put(parent, subValue.getValue());
            } else {
                map.putAll(subValue.getChildren(parent + ">" + subValue.getName()));
            }
        }
        return map;
    }

    public void clearSubValues() {
        this.subValues.clear();
        this.valuesMapped.clear();
    }

    @Override
    public String toString() {
        if (value == null) {
            return name;
        }
        return value.toString();
    }

    public Node<T> getParent() {
        return parent;
    }

    public void replaceLeaf(T credentialDatum) {
        value = credentialDatum;
    }
}

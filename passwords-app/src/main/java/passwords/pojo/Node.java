package passwords.pojo;

import commons.lib.main.os.LogUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.util.logging.Logger;

@Deprecated
public class Node<T> {

    private static final Logger logger = LogUtils.initLogs();

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

    public Node(String name, T value) {
        this(null, name, value, new ArrayList<>());
    }

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


    public Node<T> findByHierarchy(String fullHierarchy) {
        final String[] split = fullHierarchy.split(" > ");
        final LinkedList<String> hierarchyLevels = new LinkedList<>(Arrays.asList(split));
        return findByHierarchy(hierarchyLevels);
    }

    public Node<T> findByHierarchy(LinkedList<String> hierarchyLevels) {
        String currentLevel = hierarchyLevels.poll();
        if (currentLevel == null) return null;
        for (Node<T> subValue : subValues) {
            if (currentLevel.equals(subValue.getName())) {
                if (hierarchyLevels.isEmpty()) {
                    return subValue;
                }
                return findByHierarchy(hierarchyLevels);
            }
        }
        return null;
    }

    public Node<T> getOrCreate(String currentHierarchyLevelName, T nodeValue) {
        LogUtils.debug("getOrCreate({}, {})", currentHierarchyLevelName, nodeValue);
        Node<T> tNode = null;
        tNode = valuesMapped.get(currentHierarchyLevelName);
        if (tNode == null) {
            LogUtils.debug("Adding a new child node to {}. Consequently its hierarchy is {} and value is {}", name, currentHierarchyLevelName, nodeValue);
            Node<T> tNode1 = new Node<T>(this, currentHierarchyLevelName, nodeValue, new ArrayList<>());
            subValues.add(tNode1);
            valuesMapped.put(currentHierarchyLevelName, tNode1);
            LogUtils.debug("Map of children has {} children.", valuesMapped.size());
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
        return this.subValues.isEmpty();
    }

    public Map<String, T> getChildrenV2(String parent) {
        final Map<String, T> map = new HashMap<>();
        for (Node<T> subValue : this.getSubValues()) {
            if (subValue.isLeaf()) {
                map.put(parent + " > " + subValue.getName(), subValue.getValue());
            } else {
                map.putAll(subValue.getChildrenV2(parent + ">" + subValue.getName()));
            }
        }
        return map;
    }

    @Deprecated
    public Map<String, List<T>> getChildrenV1(String parent) {
        final Map<String,  List<T>> map = new HashMap<>();
        for (Node<T> subValue : this.getSubValues()) {
            if (subValue.isLeaf()) {
                map.computeIfAbsent(parent, k -> new ArrayList<>()).add(subValue.getValue());
            } else {
                map.putAll(subValue.getChildrenV1(parent + ">" + subValue.getName()));
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

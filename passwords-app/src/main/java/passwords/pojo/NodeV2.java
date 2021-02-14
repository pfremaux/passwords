package passwords.pojo;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class NodeV2<T> {

    public static final String EMPTY = ".";
    private final NodeV2<T> parent;
    private final String nodeName;
    private T value; // MUTABLE
    private final List<NodeV2<T>> children;

    public static <T> NodeV2<T> root() {
        return dir(null, "root", new ArrayList<>());
    }

    public static <T> NodeV2<T> leaf(NodeV2<T> parent, T value) {
        return new NodeV2<T>(parent, value, null, null);
    }

    public static <T> NodeV2<T> dir(NodeV2<T> parent, String nodeName, List<NodeV2<T>> children) {
        return new NodeV2<T>(parent, null, nodeName, children);
    }

    private NodeV2(NodeV2<T> parent, T value, String nodeName, List<NodeV2<T>> children) {
        this.parent = parent;
        this.nodeName = nodeName;
        this.value = value;
        this.children = children;
    }

    public NodeV2<T> getNode(List<String> hierarchy) {
        if (hierarchy.isEmpty()) {
            return null;
        }
        String levelNodeName = hierarchy.get(0);
        Optional<NodeV2<T>> first = children.stream().filter(node -> node.getNodeName().equals(levelNodeName)).findFirst();
        if (first.isEmpty()) {
            return null;
        }
        final NodeV2<T> tNodeV2 = first.get();
        if (hierarchy.size() == 1) {
            return tNodeV2;
        }
        return tNodeV2.getNode(hierarchy.subList(1, hierarchy.size()));
    }

    public NodeV2<T> getParent() {
        return parent;
    }

    public List<NodeV2<T>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    public String getNodeName() {
        if (nodeName == null) {
                return value.toString();
        }
        return nodeName;
    }

    public DefaultMutableTreeNode getTreeNodeInstance(boolean allowChildren) {
        final DefaultMutableTreeNode currentNode;
        currentNode = new DefaultMutableTreeNode(this, allowChildren);

        for (NodeV2<T> subValue : getChildren()) {
            currentNode.add(subValue.getTreeNodeInstance(!getChildren().isEmpty()));
        }
        return currentNode;
    }

    public Map<String,T> getHierarchy(List<String> parentContext) {
        final Map<String, T> result = new HashMap<>();
        for (NodeV2<T> child : getChildren()) {
            final List<String> s =new ArrayList<>(parentContext);
            s.add(child.getNodeName());
            if (!child.isLeaf()) {
                result.putAll(getHierarchy(s));
            } else {
                final String key = String.join(" > ", s);
                result.put(key, child.value);
            }
        }
        return result;
    }

    public NodeV2<T> addLeaf(T value) {
        if (isLeaf()) {
            // TODO ERROR
            return null;
        }
        NodeV2<T> leaf = NodeV2.leaf(this, value);
        children.add(leaf);
        return leaf;
    }

    public NodeV2<T> addDir(String value) {
        if (isLeaf()) {
            // TODO ERROR
            return null;
        }
        NodeV2<T> dir = NodeV2.dir(this, value, new ArrayList<>());
        children.add(dir);
        return dir;
    }

    public boolean isLeaf() {
        return value != null;
    }

    public void replaceValueWith(T credentialDatum) {
        if (!isLeaf()) {
            // TODO ERROR
        }
        value = credentialDatum;
    }

    @Override
    public String toString() {
        return getNodeName();
    }
}
package passwords.gui;

import commons.lib.NodeV2;
import commons.lib.extra.gui.AskDialog;
import commons.lib.extra.gui.Positioner;
import commons.lib.main.console.v3.init.CliApp;
import commons.lib.main.os.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import passwords.encryption.EncryptionFactory;
import passwords.encryption.EncryptionService;
import passwords.expectation.Expectation;
import passwords.expectation.SaveDirExist;
import passwords.pojo.CredentialDatum;
import passwords.settings.CredentialsSettings;
import passwords.settings.InputParameters;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

import static commons.lib.extra.gui.Positioner.*;

public class CredentialsTreeDialogv2 extends Dialog {
    private static final Logger logger = LoggerFactory.getLogger(CredentialsTreeDialogv2.class);

    private static final String ADD_LEVEL_BTN_NAME = "addLevelBtn";
    private static final String ADD_NODE_BTN_NAME = "addNodeBtn";
    private static final String URL_ELEMENT_LABEL_NAME = "lblUrl";
    private static final String URL_ELEMENT_NAME = "url";
    private static final String LOGIN_ELEMENT_LABEL_NAME = "lblLogin";
    private static final String LOGIN_ELEMENT_NAME = "login";
    private static final String PASSWORD_ELEMENT_LABEL_NAME = "lblPassword";
    private static final String PASSWORD_ELEMENT_NAME = "password";
    private static final String COMMENT_ELEMENT_LABEL_NAME = "lblComment";
    private static final String COMMENT_ELEMENT_NAME = "comment";
    private static final String SAVE_ELEMENT_NAME = "save";
    private static final String COPY_PASSWORD_ELEMENT_NAME = "btnCopyPwd";
    private final Positioner positioner = new Positioner();
    private DefaultMutableTreeNode selectedItem = null;
    private JTree jTree;

    private final static Expectation[] expectations = {
            new SaveDirExist()
    };

    public CredentialsTreeDialogv2(Frame owner, EncryptionFactory encryptionFactory, List<CredentialDatum> datumList, CredentialsSettings securitySettings, ResourceBundle uiMessages) {
        super(owner);
        setModal(true);
        for (Expectation expectation : expectations) {
            expectation.resolve();
        }

        final NodeV2<CredentialDatum> rootNode = NodeV2.root();
        credentialDataToNodes(datumList, rootNode);

        // Add the Nodes to the graphical tree
        jTree = positioner.addTree(rootNode.getTreeNodeInstance(true), 400, 400, e -> {
            // action when an element is selected in the tree
            final JTree source = (JTree) e.getSource();
            selectedItem = (DefaultMutableTreeNode) source.getLastSelectedPathComponent();
            LogUtils.debug("Selection in tree : {} of type {}", selectedItem, (selectedItem == null ? "null" : selectedItem.getUserObject().getClass().getSimpleName()));
            // If the selected element is not valid or is Root (the only element that might be a String otherwise it's a Node)
            if (selectedItem == null || selectedItem.getUserObject() instanceof String) {
                final Button addLvlBtn = positioner.getComponentByName(ADD_LEVEL_BTN_NAME, Button.class);
                addLvlBtn.setVisible(false);
                if (selectedItem != null && selectedItem.getUserObject().equals(".")) {
                    final Button addNodeBtn = positioner.getComponentByName(ADD_NODE_BTN_NAME, Button.class);
                    addNodeBtn.setVisible(true);
                    return;
                } else {
                    final Button addNodeBtn = positioner.getComponentByName(ADD_NODE_BTN_NAME, Button.class);
                    addNodeBtn.setVisible(false);
                    return;
                }
            }
            final NodeV2<CredentialDatum> node = getTypedUserObject(selectedItem);
            if (node.getNodeName().equalsIgnoreCase(NodeV2.EMPTY)) {
                return;
            }
            // Depending on the Node selected (a leaf or not = a directory or a credential)...
            // ... they'll be visible or not.
            final TextArea comment = positioner.getComponentByName(COMMENT_ELEMENT_NAME, TextArea.class);
            final TextField url = positioner.getComponentByName(URL_ELEMENT_NAME, TextField.class);
            final TextField login = positioner.getComponentByName(LOGIN_ELEMENT_NAME, TextField.class);
            final TextField password = positioner.getComponentByName(PASSWORD_ELEMENT_NAME, TextField.class);
            // If the selected element is a leaf
            if (node.getValue() != null || node.getNodeName().equalsIgnoreCase(NodeV2.EMPTY)) {
                comment.setText(node.getValue().getComments());
                url.setText(node.getValue().getUrl());
                login.setText(node.getValue().getLogin());
                password.setText(node.getValue().getPassword());
                final Button addLvlBtn = positioner.getComponentByName(ADD_LEVEL_BTN_NAME, Button.class);
                addLvlBtn.setVisible(false);
                final Button addNodeBtn = positioner.getComponentByName(ADD_NODE_BTN_NAME, Button.class);
                addNodeBtn.setVisible(false);
                credentialDatumFormSetVisible(true, false);
            } else {
                // If the selected element is a directory
                credentialDatumFormSetVisible(false, false);
                final Button addLvlBtn = positioner.getComponentByName(ADD_LEVEL_BTN_NAME, Button.class);
                addLvlBtn.setVisible(true);
                final Button addNodeBtn = positioner.getComponentByName(ADD_NODE_BTN_NAME, Button.class);
                addNodeBtn.setVisible(node.getParent() != null);
            }
            final Button copyPassword = positioner.getComponentByName(COPY_PASSWORD_ELEMENT_NAME, Button.class);
            copyPassword.setVisible(node.isLeaf());
        });
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.setName("tree");
        // Create the button for creating a directory
        final Button addDirectoryButton = positioner.addButton(
                uiMessages.getString("tree.credentials.new.dir.btn"),
                200,
                DEFAULT_BUTTON_HEIGHT,
                e -> {
                    // When the button is clicked, the user has to specify the name
                    if (selectedItem != null && (selectedItem.getAllowsChildren() || selectedItem.isRoot())) {
                        final AskDialog askDialog = new AskDialog(this);
                        askDialog.setModal(true);
                        askDialog.ask(uiMessages.getString("tree.credentials.new.dir.name.choice"));
                        final String name = askDialog.getAnswer();
                        // Add the "Node" directory to the selected element(which is a directory itself)
                        final NodeV2<CredentialDatum> nodeSelected = getTypedUserObject(selectedItem);
                        NodeV2<CredentialDatum> addDir = nodeSelected.addDir(name);
                        final DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(addDir, true);
                        newChild.add(new DefaultMutableTreeNode(NodeV2.EMPTY, false));
                        selectedItem.add(newChild);
                        DefaultTreeModel defaultTreeModel = (DefaultTreeModel) jTree.getModel();
                        // Refresh the Tree, no idea how to not collapse the tree...
                        defaultTreeModel.reload();
                    }
                });
        addDirectoryButton.setName(ADD_LEVEL_BTN_NAME);
        positioner.setPutUnder(true);
        // Create the "add credential" button
        final Button addNodeBtn = positioner.addButton(
                uiMessages.getString("btn.add"),
                100,
                DEFAULT_BUTTON_HEIGHT,
                e -> {
                    // When the button is clicked
                    if (selectedItem != null) {
                        LogUtils.debug("Adding creds in {}", selectedItem);
                        credentialDatumFormSetVisible(true, true);
                    }
                });
        addNodeBtn.setName(ADD_NODE_BTN_NAME);

        // Add all the element related to the credentials (used for creation/update/display)
        final Label labelUrl = positioner.addLabel("URL :", 300, DEFAULT_TEXTFIELD_HEIGHT);
        labelUrl.setName(URL_ELEMENT_LABEL_NAME);
        final TextField url = positioner.addTextField(300, DEFAULT_TEXTFIELD_HEIGHT);
        url.setName(URL_ELEMENT_NAME);
        final Label labelLogin = positioner.addLabel("Login :", 300, DEFAULT_TEXTFIELD_HEIGHT);
        labelLogin.setName(LOGIN_ELEMENT_LABEL_NAME);
        final TextField login = positioner.addTextField(300, DEFAULT_TEXTFIELD_HEIGHT);
        login.setName(LOGIN_ELEMENT_NAME);
        final Label labelPassword = positioner.addLabel("Password :", 300, DEFAULT_TEXTFIELD_HEIGHT);
        labelPassword.setName(PASSWORD_ELEMENT_LABEL_NAME);
        final TextField password = positioner.addPasswordField(300, DEFAULT_TEXTFIELD_HEIGHT);
        password.setName(PASSWORD_ELEMENT_NAME);
        final Button btnCopyPassword = positioner.addButton(uiMessages.getString("btn.copy.password"), 200, DEFAULT_BUTTON_HEIGHT, e -> {
            copyToClipboard(password.getText());
        });
        btnCopyPassword.setVisible(false);
        btnCopyPassword.setName(COPY_PASSWORD_ELEMENT_NAME);
        final Label labelDescription = positioner.addLabel("Description :", 300, DEFAULT_TEXTFIELD_HEIGHT);
        labelDescription.setName(COMMENT_ELEMENT_LABEL_NAME);
        final TextArea comment = positioner.addTextArea("", 300, DEFAULT_TEXTFIELD_HEIGHT * 3);
        comment.setName(COMMENT_ELEMENT_NAME);
        // Create the "save credential" button.
        final Button saveInTreeButton = positioner.addButton(uiMessages.getString("btn.save.credential"), 200, DEFAULT_BUTTON_HEIGHT, e -> {
            LogUtils.debug("Saving the credential in the tree...");
            // When the button is clicked
            final NodeV2<CredentialDatum> selectedCredentialDatumNode = getTypedUserObject(selectedItem);
            final CredentialDatum credentialDatum = new CredentialDatum("",
                    url.getText(),
                    login.getText(),
                    password.getText(),
                    comment.getText());
            if (selectedCredentialDatumNode != null && selectedCredentialDatumNode.isLeaf()) {
                LogUtils.debug("The selected element is a leaf. Replacing its value by {}. [selectedElementName={}, selectedElementValues={}]",
                        credentialDatum,
                        selectedCredentialDatumNode.getNodeName(),
                        selectedCredentialDatumNode.getValue());
                selectedCredentialDatumNode.replaceValueWith(credentialDatum);
            }
            if (selectedCredentialDatumNode == null) {
                DefaultMutableTreeNode parentTreeNode = (DefaultMutableTreeNode) selectedItem.getParent();
                NodeV2<CredentialDatum> parentNode = (NodeV2<CredentialDatum>) parentTreeNode.getUserObject();
                LogUtils.debug("Selected Item is not a leaf. [selectedItem={}]", selectedItem.getUserObject());
                NodeV2<CredentialDatum> leaf = parentNode.addLeaf(credentialDatum);
                removeEmptyLeaf(parentTreeNode);
                final DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(leaf, false);
                parentTreeNode.add(newChild);
            } else {
                LogUtils.debug("Selected Item is not a leaf. [selectedItem={}]", selectedItem.getUserObject());
                removeEmptyLeaf(selectedItem);
                NodeV2<CredentialDatum> leaf = selectedCredentialDatumNode.addLeaf(credentialDatum);
                final DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(leaf, false);
                selectedItem.add(newChild);
            }
            DefaultTreeModel defaultTreeModel = (DefaultTreeModel) jTree.getModel();
            defaultTreeModel.reload();
            credentialDatumFormSetVisible(false, true);
        });
        saveInTreeButton.setName(SAVE_ELEMENT_NAME);
        // Create the "save to filesystem" button.
        positioner.addButton(uiMessages.getString("tree.credentials.save.to.file"), DEFAULT_LABEL_WIDTH, DEFAULT_BUTTON_HEIGHT, e -> {
            // When the button is clicked
            final int encryptionVersion = CliApp.PARAMETERS.fromProperty("encrypt.version").orElse(null).getPropertyInt();
            LogUtils.debug("Encryption version {}", encryptionVersion);
            final EncryptionService service = encryptionFactory.getService(encryptionVersion);
            LogUtils.debug("service = {}", service);
            final Path fullPathSaveDir = InputParameters.SAVE_DIR.getPropertyPath();
            final List<CredentialDatum> data = new ArrayList<>(getCredentialsUpToDate().values());
            service.encrypt(fullPathSaveDir, data, securitySettings);
        });
        credentialDatumFormSetVisible(false, true);
        this.setTitle(uiMessages.getString("tree.credentials.title"));
        positioner.addAllToDialog(this);
        this.pack();
        setBounds(positioner.getWindowBound(100, 100));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public static void credentialDataToNodes(List<CredentialDatum> datumList, NodeV2<CredentialDatum> rootNode) {
        // Iterate over the decrypted data and build the Nodes relationship.
        for (CredentialDatum credentialDatum : datumList) {
            final String hierarchy = credentialDatum.getHierarchy();
            final StringTokenizer hierarchyLevelNames = new StringTokenizer(hierarchy, ">");
            NodeV2<CredentialDatum> currentNode = rootNode;
            while (hierarchyLevelNames.hasMoreElements()) {
                final String hierarchyLevelName = hierarchyLevelNames.nextToken().trim();
                if (hierarchyLevelNames.hasMoreTokens()) {
                    NodeV2<CredentialDatum> childByNodeName = currentNode.getChildByNodeName(hierarchyLevelName);
                    if (childByNodeName == null) {
                        currentNode = currentNode.addDir(hierarchyLevelName);
                    } else {
                        currentNode = childByNodeName;
                    }
                }
            }
            final NodeV2<CredentialDatum> credentialDatumNode = NodeV2.leaf(currentNode, credentialDatum);
            currentNode.getChildren().add(credentialDatumNode);
        }
    }

    public static void credentialDataToNodesLegacy(List<CredentialDatum> datumList, NodeV2<CredentialDatum> rootNode) {
        // Iterate over the decrypted data and build the Nodes relationship.
        for (CredentialDatum credentialDatum : datumList) {
            final String hierarchy = credentialDatum.getHierarchy();
            final StringTokenizer hierarchyLevelNames = new StringTokenizer(hierarchy, ">");
            NodeV2<CredentialDatum> currentNode = rootNode;
            while (hierarchyLevelNames.hasMoreElements()) {
                final String hierarchyLevelName = hierarchyLevelNames.nextToken().trim();
                if (hierarchyLevelNames.hasMoreTokens()) {
                    // The parameter is null because it's not a leaf but a directory
                    currentNode = currentNode.addDir(hierarchyLevelName);
                }
            }
            final NodeV2<CredentialDatum> credentialDatumNode = NodeV2.leaf(currentNode, credentialDatum);
            currentNode.getChildren().add(credentialDatumNode);
        }
    }

    @SuppressWarnings("unchecked")
    private NodeV2<CredentialDatum> getTypedUserObject(DefaultMutableTreeNode selectedItem) {
        if (selectedItem.getUserObject() instanceof String) {
            return null;//(Node<CredentialDatum>)  ((DefaultMutableTreeNode) selectedItem.getParent()).getUserObject();
        }

        return (NodeV2<CredentialDatum>) selectedItem.getUserObject();
    }

    private void removeEmptyLeaf(DefaultMutableTreeNode node) {
        Enumeration<TreeNode> children = node.children();
        MutableTreeNode mutableTreeNodeToRemove = null;
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) children.nextElement();
            if (treeNode.isLeaf()) {
                if (treeNode.getUserObject() instanceof String) {
                    if (NodeV2.EMPTY.equals(treeNode.getUserObject())) {
                        mutableTreeNodeToRemove = treeNode;
                    }
                }
            }
        }
        if (mutableTreeNodeToRemove != null) {
            node.remove(mutableTreeNodeToRemove);
        }
    }

    private Map<String, CredentialDatum> getCredentialsUpToDate() {
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTree.getModel().getRoot();
        NodeV2<CredentialDatum> rootNode = getTypedUserObject(root);
        Map<String, CredentialDatum> children = rootNode.getHierarchy(Collections.emptyList());
        for (Map.Entry<String, CredentialDatum> entry : children.entrySet()) {
            entry.setValue(entry.getValue().move(entry.getKey()));
        }
        return children;
    }

    private void credentialDatumFormSetVisible(boolean v, boolean clear) {
        final Label lblUrl = positioner.getComponentByName(URL_ELEMENT_LABEL_NAME, Label.class);
        final Label lblLogin = positioner.getComponentByName(LOGIN_ELEMENT_LABEL_NAME, Label.class);
        final Label lblPassword = positioner.getComponentByName(PASSWORD_ELEMENT_LABEL_NAME, Label.class);
        final Label lblComment = positioner.getComponentByName(COMMENT_ELEMENT_LABEL_NAME, Label.class);
        final TextField url = positioner.getComponentByName(URL_ELEMENT_NAME, TextField.class);
        final TextField login = positioner.getComponentByName(LOGIN_ELEMENT_NAME, TextField.class);
        final TextField password = positioner.getComponentByName(PASSWORD_ELEMENT_NAME, TextField.class);
        final TextArea comment = positioner.getComponentByName(COMMENT_ELEMENT_NAME, TextArea.class);
        final Button save = positioner.getComponentByName(SAVE_ELEMENT_NAME, Button.class);
        comment.setVisible(true);
        if (clear) {
            comment.setText("");
            login.setText("");
            password.setText("");
            url.setText("");
        }
        lblUrl.setVisible(v);
        url.setVisible(v);
        lblLogin.setVisible(v);
        login.setVisible(v);
        lblPassword.setVisible(v);
        password.setVisible(v);
        lblComment.setVisible(v);
        comment.setVisible(v);
        save.setVisible(v);
    }

    private void copyToClipboard(String value) {
        StringSelection stringSelection = new StringSelection(value);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

}
public void testNodePointerAsPathIncludesAttributesIndexesAndContainerParents() {
    NodePointerPathTestPointer root =
        new NodePointerPathTestPointer(null, "root", false, false);
    NodePointerPathTestPointer item =
        new NodePointerPathTestPointer(root, "item", true, false);
    item.setIndex(1);
    NodePointerPathTestPointer attribute =
        new NodePointerPathTestPointer(root, "id", false, false);
    attribute.setAttribute(true);
    NodePointerPathTestPointer container =
        new NodePointerPathTestPointer(root, "container", false, true);
    NodePointerPathTestPointer contained =
        new NodePointerPathTestPointer(container, "ignored", false, false);

    assertEquals("/root", root.asPath());
    assertEquals("/root/item[2]", item.asPath());
    assertEquals("/root/@id", attribute.asPath());
    assertEquals("/root/container", contained.asPath());
}

public void testNodePointerCloneClonesItsParentChain() {
    NodePointerPathTestPointer root =
        new NodePointerPathTestPointer(null, "root", false, false);
    NodePointerPathTestPointer child =
        new NodePointerPathTestPointer(root, "child", false, false);

    NodePointer clone = (NodePointer) child.clone();

    assertTrue(clone != child);
    assertTrue(clone.getParent() != root);
    assertEquals(child.asPath(), clone.asPath());
    assertEquals(root.asPath(), clone.getParent().asPath());
}

private static final class NodePointerPathTestPointer extends NodePointer {
    private final org.apache.commons.jxpath.ri.QName name;
    private final boolean collection;
    private final boolean container;
    private Object value;

    private NodePointerPathTestPointer(
            NodePointer parent, String name, boolean collection, boolean container) {
        super(parent);
        this.name = new org.apache.commons.jxpath.ri.QName(name);
        this.collection = collection;
        this.container = container;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean isCollection() {
        return collection;
    }

    public int getLength() {
        return collection ? 1 : 0;
    }

    public org.apache.commons.jxpath.ri.QName getName() {
        return name;
    }

    public Object getBaseValue() {
        return value;
    }

    public Object getImmediateNode() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isContainer() {
        return container;
    }
}
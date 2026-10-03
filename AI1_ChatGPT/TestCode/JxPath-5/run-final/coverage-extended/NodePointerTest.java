package org.apache.commons.jxpath.ri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;

/**
 * Tests for {@link NodePointer}.
 */
public class NodePointerTest extends TestCase {

    public void testBasicPointerStateAndActualityBoundaries() {
        TestNodePointer pointer =
                new TestNodePointer(null, Locale.US, "item", "value");

        assertTrue(pointer.isRoot());
        assertFalse(pointer.isAttribute());
        assertFalse(pointer.isContainer());
        assertTrue(pointer.isNode());
        assertEquals(NodePointer.WHOLE_COLLECTION, pointer.getIndex());
        assertTrue(pointer.isActual());

        pointer.setAttribute(true);
        assertTrue(pointer.isAttribute());

        pointer.collection = true;
        pointer.length = 2;

        pointer.setIndex(0);
        assertTrue(pointer.isActual());

        pointer.setIndex(1);
        assertTrue(pointer.isActual());

        pointer.setIndex(-1);
        assertFalse(pointer.isActual());

        pointer.setIndex(2);
        assertFalse(pointer.isActual());

        pointer.setIndex(NodePointer.WHOLE_COLLECTION);
        assertTrue(pointer.isActual());
    }

    public void testParentLocaleRootAndContainerTraversal() {
        TestNodePointer root =
                new TestNodePointer(null, Locale.CANADA_FRENCH, "root", "root-value");
        TestNodePointer container =
                new TestNodePointer(root, null, "variable", "container-value");
        container.container = true;
        TestNodePointer child =
                new TestNodePointer(container, null, "child", "child-value");

        assertSame(container, child.getImmediateParentPointer());
        assertSame(root, child.getParent());
        assertFalse(child.isRoot());

        assertSame(Locale.CANADA_FRENCH, child.getLocale());
        assertTrue(child.isLanguage("fr"));
        assertTrue(child.isLanguage("FR-ca"));
        assertFalse(child.isLanguage("en"));

        assertEquals("root-value", child.getRootNode());
        assertEquals("root-value", root.getRootNode());
    }

    public void testValuePointerRecursionAndNodeValueDelegation() {
        TestNodePointer value =
                new TestNodePointer(null, Locale.US, "value", "ultimate-value");
        TestNodePointer middle =
                new TestNodePointer(null, Locale.US, "middle", "middle-value");
        TestNodePointer outer =
                new TestNodePointer(null, Locale.US, "outer", "outer-value");

        middle.immediateValuePointer = value;
        outer.immediateValuePointer = middle;

        assertSame(value, outer.getValuePointer());
        assertEquals("ultimate-value", outer.getValue());
        assertEquals("ultimate-value", outer.getNode());
        assertEquals("ultimate-value", outer.getNodeValue());

        assertSame(value, value.getValuePointer());
        assertEquals("ultimate-value", value.getValue());
    }

    public void testTestNodeAndDefaultNamespaceOperations() {
        TestNodePointer pointer =
                new TestNodePointer(null, Locale.US, "item", "value");

        assertTrue(pointer.testNode(null));
        assertNull(pointer.namespaceIterator());
        assertNull(pointer.namespacePointer("prefix"));
        assertNull(pointer.getNamespaceURI("prefix"));
        assertNull(pointer.getNamespaceURI());
        assertNull(pointer.getDefaultNamespaceURIForTest());
        assertTrue(pointer.isDefaultNamespaceForTest(null));
        assertFalse(pointer.isDefaultNamespaceForTest("prefix"));

        pointer.container = true;
        assertFalse(pointer.isNode());
    }

    public void testNodeNameAndNodeTypeTestsCoverMatchingAndNamespaceRules() {
        TestNodePointer pointer =
                new TestNodePointer(null, Locale.US, "item", "value");

        assertTrue(pointer.testNode(new NodeNameTest(new QName("item"))));
        assertFalse(pointer.testNode(new NodeNameTest(new QName("other"))));
        assertTrue(pointer.testNode(new NodeNameTest(new QName("*"))));
        assertTrue(pointer.testNode(
                new NodeTypeTest(Compiler.NODE_TYPE_NODE)));

        pointer.name = new QName("node", "item");
        pointer.namespaceURI = "urn:test";

        assertTrue(pointer.testNode(
                new NodeNameTest(new QName("test", "item"))));

        pointer.namespaceURI = null;
        assertFalse(pointer.testNode(
                new NodeNameTest(new QName("test", "item"))));

        pointer.name = null;
        assertFalse(pointer.testNode(new NodeNameTest(new QName("item"))));

        pointer.name = new QName("item");
        pointer.container = true;
        assertFalse(pointer.testNode(new NodeNameTest(new QName("item"))));
        assertFalse(pointer.testNode(
                new NodeTypeTest(Compiler.NODE_TYPE_NODE)));
    }

    public void testDefaultNamespaceMatchesOnlyResolvedDefaultNamespace() {
        TestNodePointer pointer =
                new TestNodePointer(null, Locale.US, "item", "value");

        pointer.namespaceURI = "urn:default";
        pointer.defaultNamespaceURI = "urn:default";

        assertTrue(pointer.isDefaultNamespaceForTest("prefix"));

        pointer.defaultNamespaceURI = "urn:other";
        assertFalse(pointer.isDefaultNamespaceForTest("prefix"));
    }

    public void testCreatePathSetsValueAndUnsupportedCreationReportsPath() {
        TestNodePointer pointer =
                new TestNodePointer(null, Locale.US, "parent", "original");

        assertSame(pointer, pointer.createPath(null, "replacement"));
        assertEquals("replacement", pointer.assignedValue);
        assertSame(pointer, pointer.createPath(null));

        try {
            pointer.createChild(null, new QName("child"), 0, "value");
            fail("Creating a child on a generic NodePointer must fail");
        }
        catch (JXPathException ex) {
            assertTrue(ex.getMessage().indexOf("/parent/child[1]") >= 0);
        }

        try {
            pointer.createChild(null, new QName("child"), 2);
            fail("Creating a collection child on a generic NodePointer must fail");
        }
        catch (JXPathException ex) {
            assertTrue(ex.getMessage().indexOf("/parent/child[3]") >= 0);
        }

        try {
            pointer.createAttribute(null, new QName("attribute"));
            fail("Creating an attribute on a generic NodePointer must fail");
        }
        catch (JXPathException ex) {
            assertTrue(ex.getMessage().indexOf("/parent/@attribute") >= 0);
        }
    }

    public void testPathsAttributesCollectionIndexesAndContainerPaths() {
        TestNodePointer root =
                new TestNodePointer(null, Locale.US, "root", "root");
        TestNodePointer child =
                new TestNodePointer(root, Locale.US, "item", "item");
        child.collection = true;
        child.setIndex(0);

        assertEquals("/root/item[1]", child.asPath());
        assertEquals(child.asPath(), child.toString());

        child.setAttribute(true);
        assertEquals("/root/@item[1]", child.asPath());

        child.setIndex(NodePointer.WHOLE_COLLECTION);
        assertEquals("/root/@item", child.asPath());

        TestNodePointer container =
                new TestNodePointer(root, Locale.US, "variable", "variable");
        container.container = true;
        TestNodePointer contained =
                new TestNodePointer(container, Locale.US, "contained", "contained");

        assertEquals("/root/variable", contained.asPath());
    }

    public void testCloneCreatesIndependentParentChain() {
        TestNodePointer parent =
                new TestNodePointer(null, Locale.US, "parent", "parent-value");
        TestNodePointer child =
                new TestNodePointer(parent, Locale.US, "child", "child-value");
        child.setAttribute(true);
        child.collection = true;
        child.setIndex(1);

        NodePointer cloned = (NodePointer) child.clone();

        assertNotSame(child, cloned);
        assertNotSame(child.getImmediateParentPointer(),
                cloned.getImmediateParentPointer());
        assertEquals(child.asPath(), cloned.asPath());
        assertEquals(child.getIndex(), cloned.getIndex());
        assertTrue(cloned.isAttribute());
    }

    public void testCompareToForSiblingsAndAncestorRelationships() {
        TestNodePointer root =
                new TestNodePointer(null, Locale.US, "root", "root");
        TestNodePointer first =
                new TestNodePointer(root, Locale.US, "first", "first");
        TestNodePointer second =
                new TestNodePointer(root, Locale.US, "second", "second");
        first.order = 1;
        second.order = 2;

        assertTrue(first.compareTo(second) < 0);
        assertTrue(second.compareTo(first) > 0);
        assertEquals(0, first.compareTo(first));

        assertTrue(root.compareTo(first) < 0);
        assertTrue(first.compareTo(root) > 0);

        try {
            first.compareTo("not a pointer");
            fail("Comparing to a non-NodePointer must throw ClassCastException");
        }
        catch (ClassCastException expected) {
            assertNotNull(expected);
        }
    }

    public void testCompareToRecursivelyComparesDescendantsAndRejectsDifferentTrees() {
        TestNodePointer root =
                new TestNodePointer(null, Locale.US, "root", "root");
        TestNodePointer firstParent =
                new TestNodePointer(root, Locale.US, "first-parent", "first-parent");
        TestNodePointer secondParent =
                new TestNodePointer(root, Locale.US, "second-parent", "second-parent");
        firstParent.order = 1;
        secondParent.order = 2;

        TestNodePointer firstChild =
                new TestNodePointer(firstParent, Locale.US, "first-child", "first-child");
        TestNodePointer secondChild =
                new TestNodePointer(secondParent, Locale.US, "second-child", "second-child");

        assertTrue(firstChild.compareTo(secondChild) < 0);
        assertTrue(secondChild.compareTo(firstChild) > 0);

        TestNodePointer unrelatedRoot =
                new TestNodePointer(null, Locale.US, "unrelated", "unrelated");

        try {
            root.compareTo(unrelatedRoot);
            fail("Pointers from unrelated trees must not be comparable");
        }
        catch (JXPathException expected) {
            assertTrue(expected.getMessage().indexOf(
                    "Cannot compare pointers that do not belong to the same tree") >= 0);
        }
    }

    public void testValuePointerDelegatesChildAndAttributeIteration() {
        DelegatingNodePointer value =
                new DelegatingNodePointer(null, Locale.US, "value", "value");
        TestNodePointer outer =
                new TestNodePointer(null, Locale.US, "outer", "outer");
        outer.immediateValuePointer = value;

        assertNull(outer.childIterator(null, false, null));
        assertTrue(value.childIteratorCalled);

        assertNull(outer.attributeIterator(new QName("attribute")));
        assertTrue(value.attributeIteratorCalled);

        TestNodePointer ordinary =
                new TestNodePointer(null, Locale.US, "ordinary", "ordinary");
        assertNull(ordinary.childIterator(null, false, null));
        assertNull(ordinary.attributeIterator(new QName("attribute")));
    }

    public void testNewNodePointerForSupportedBeansAndChildren() {
        NamedBean bean = new NamedBean("node-value");

        NodePointer root =
                NodePointer.newNodePointer(new QName("root"), bean, Locale.US);
        NodePointer child =
                NodePointer.newChildNodePointer(root, new QName("name"), "value");

        assertNotNull(root);
        assertNotNull(child);
        assertSame(root, child.getImmediateParentPointer());
    }

    public void testNewNodePointerForNullBeanCreatesNullPointer() {
        NodePointer pointer =
                NodePointer.newNodePointer(new QName("missing"), null, Locale.US);

        assertTrue(pointer instanceof NullPointer);
        assertTrue(pointer.isRoot());
    }

    /**
     * Regression test for JXPATH-89. A union containing a variable pointer
     * and a regular object-graph node must be sortable and iterable without
     * throwing the "different tree" comparison exception.
     */
    public void testUnionOfVariableAndNodePointerDoesNotThrowComparisonException() {
        JXPathContext context = JXPathContext.newContext(new NamedBean("node-value"));
        context.getVariables().declareVariable("var", "variable-value");

        Iterator pointers = context.iteratePointers("$var | /name");
        Set values = new HashSet();

        while (pointers.hasNext()) {
            Pointer pointer = (Pointer) pointers.next();
            values.add(pointer.getValue());
        }

        assertEquals(2, values.size());
        assertTrue(values.contains("variable-value"));
        assertTrue(values.contains("node-value"));
    }

    public static class NamedBean {
        private final String name;

        public NamedBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static class TestNodePointer extends NodePointer {
        private QName name;
        private Object immediateNode;
        private Object assignedValue;
        private boolean collection;
        private int length = 1;
        private boolean container;
        private int order;
        private NodePointer immediateValuePointer;
        private String namespaceURI;
        private String defaultNamespaceURI;

        TestNodePointer(
                NodePointer parent,
                Locale locale,
                String name,
                Object immediateNode) {
            super(parent, locale);
            this.name = new QName(name);
            this.immediateNode = immediateNode;
        }

        public boolean isLeaf() {
            return true;
        }

        public boolean isContainer() {
            return container;
        }

        public boolean isCollection() {
            return collection;
        }

        public int getLength() {
            return length;
        }

        public QName getName() {
            return name;
        }

        public Object getBaseValue() {
            return immediateNode;
        }

        public Object getImmediateNode() {
            return immediateNode;
        }

        public void setValue(Object value) {
            assignedValue = value;
            immediateNode = value;
        }

        public int compareChildNodePointers(
                NodePointer pointer1,
                NodePointer pointer2) {
            TestNodePointer first = (TestNodePointer) pointer1;
            TestNodePointer second = (TestNodePointer) pointer2;
            return first.order - second.order;
        }

        public NodePointer getImmediateValuePointer() {
            return immediateValuePointer == null ? this : immediateValuePointer;
        }

        public String getNamespaceURI(String prefix) {
            return namespaceURI;
        }

        protected String getDefaultNamespaceURI() {
            return defaultNamespaceURI;
        }

        boolean isDefaultNamespaceForTest(String prefix) {
            return isDefaultNamespace(prefix);
        }

        String getDefaultNamespaceURIForTest() {
            return getDefaultNamespaceURI();
        }
    }

    private static class DelegatingNodePointer extends TestNodePointer {
        private boolean childIteratorCalled;
        private boolean attributeIteratorCalled;

        DelegatingNodePointer(
                NodePointer parent,
                Locale locale,
                String name,
                Object immediateNode) {
            super(parent, locale, name, immediateNode);
        }

        public NodeIterator childIterator(
                NodeTest test,
                boolean reverse,
                NodePointer startWith) {
            childIteratorCalled = true;
            return null;
        }

        public NodeIterator attributeIterator(QName qname) {
            attributeIteratorCalled = true;
            return null;
        }
    }
}

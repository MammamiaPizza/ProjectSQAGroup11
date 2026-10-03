```java
package org.apache.commons.jxpath.ri.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
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
        private final QName name;
        private Object immediateNode;
        private Object assignedValue;
        private boolean collection;
        private int length = 1;
        private boolean container;
        private int order;
        private NodePointer immediateValuePointer;

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

        boolean isDefaultNamespaceForTest(String prefix) {
            return isDefaultNamespace(prefix);
        }

        String getDefaultNamespaceURIForTest() {
            return getDefaultNamespaceURI();
        }
    }
}
```

Test coverage summary:

- `testBasicPointerStateAndActualityBoundaries`: attribute state, default index, collection index boundaries, `isActual`, `isNode`, and `isContainer`.
- `testParentLocaleRootAndContainerTraversal`: parent resolution through container pointers, locale inheritance, language matching, root-node lookup.
- `testValuePointerRecursionAndNodeValueDelegation`: recursive transparent-container value-pointer resolution and `getValue`/`getNode` delegation.
- `testTestNodeAndDefaultNamespaceOperations`: null node tests, default namespace behavior, and default namespace-related methods.
- `testCreatePathSetsValueAndUnsupportedCreationReportsPath`: successful value assignment and all unsupported child/attribute creation exception paths.
- `testPathsAttributesCollectionIndexesAndContainerPaths`: normal, attribute, collection-indexed, and container-managed XPath rendering.
- `testCloneCreatesIndependentParentChain`: cloning and recursive parent cloning.
- `testCompareToForSiblingsAndAncestorRelationships`: sibling ordering, ancestor ordering, equality, and invalid comparison type handling.
- `testNewNodePointerForNullBeanCreatesNullPointer`: null-bean factory behavior.
- `testUnionOfVariableAndNodePointerDoesNotThrowComparisonException`: regression coverage for JXPATH-89, ensuring a union of a variable pointer and ordinary node pointer can be iterated without the erroneous cross-tree comparison exception.
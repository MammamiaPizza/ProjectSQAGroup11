package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

/**
 * Tests for {@link Expression}.
 */
public class ExpressionGeneratedTest extends TestCase {

    public void testContextDependencyIsComputedOnlyOnceAndComputeValueDelegatesToSubclass() {
        TestExpression expression = new TestExpression("value", true);

        assertTrue(expression.isContextDependent());
        assertTrue(expression.isContextDependent());
        assertEquals(1, expression.getContextDependencyComputations());

        assertEquals("value", expression.computeValue(null));
        assertEquals("value", expression.compute(null));
    }

    public void testContextDependencyFalseResultIsAlsoCached() {
        TestExpression expression = new TestExpression("value", false);

        assertFalse(expression.isContextDependent());
        assertFalse(expression.isContextDependent());
        assertEquals(1, expression.getContextDependencyComputations());
    }

    public void testIterateConvertsPointersReturnedByEvalContextToTheirValues() {
        NodePointer first = pointerFor("Nested: Name 1");
        NodePointer second = pointerFor("Nested: Name 2");
        ListEvalContext nodeSet =
                new ListEvalContext(pointerFor("root"), new Object[] { first, second });

        TestExpression expression = new TestExpression(nodeSet, false);
        Iterator values = expression.iterate(null);

        assertTrue(values.hasNext());
        assertEquals("Nested: Name 1", values.next());
        assertTrue(values.hasNext());
        assertEquals("Nested: Name 2", values.next());
        assertFalse(values.hasNext());
    }

    public void testIterateReturnsElementsForOrdinaryCollectionResult() {
        TestExpression expression =
                new TestExpression(Arrays.asList(new String[] { "first", "second" }), false);

        Iterator values = expression.iterate(null);

        assertTrue(values.hasNext());
        assertEquals("first", values.next());
        assertTrue(values.hasNext());
        assertEquals("second", values.next());
        assertFalse(values.hasNext());
    }

    public void testIteratePointersReturnsEvalContextUnchangedForNodeSetResult() {
        ListEvalContext nodeSet =
                new ListEvalContext(pointerFor("root"),
                        new Object[] { pointerFor("value") });
        TestExpression expression = new TestExpression(nodeSet, false);

        Iterator pointers = expression.iteratePointers(null);

        assertSame(nodeSet, pointers);
        assertTrue(pointers.hasNext());
        assertSame(nodeSet.getExpectedElement(0), pointers.next());
    }

    public void testIteratePointersWrapsOrdinaryCollectionValuesUsingRootContextLocale() {
        Object plainValue = new Object();
        ListEvalContext rootContext =
                new ListEvalContext(pointerFor("root"), new Object[0]);
        TestExpression expression =
                new TestExpression(Arrays.asList(new Object[] { plainValue }), false);

        Iterator pointers = expression.iteratePointers(rootContext);

        assertTrue(pointers.hasNext());
        Object result = pointers.next();
        assertTrue(result instanceof Pointer);
        assertSame(plainValue, ((Pointer) result).getValue());
        assertFalse(pointers.hasNext());
    }

    public void testPointerIteratorWrapsPlainValuesAndPreservesExistingPointers() {
        Object plainValue = new Object();
        NodePointer existingPointer = pointerFor("already a pointer");

        Expression.PointerIterator pointers =
                new Expression.PointerIterator(
                        Arrays.asList(new Object[] { plainValue, existingPointer }).iterator(),
                        new QName(null, "value"),
                        Locale.US);

        assertTrue(pointers.hasNext());
        Object wrapped = pointers.next();
        assertTrue(wrapped instanceof Pointer);
        assertSame(plainValue, ((Pointer) wrapped).getValue());

        assertTrue(pointers.hasNext());
        assertSame(existingPointer, pointers.next());
        assertFalse(pointers.hasNext());

        try {
            pointers.remove();
            fail("PointerIterator.remove() must not be supported");
        }
        catch (UnsupportedOperationException expected) {
            // Expected.
        }
    }

    public void testIteratePointersReturnsEmptyIteratorForNullResult() {
        TestExpression expression = new TestExpression(null, false);

        Iterator pointers = expression.iteratePointers(null);

        assertNotNull(pointers);
        assertFalse(pointers.hasNext());
    }

    public void testValueIteratorDereferencesPointersAndKeepsOrdinaryValues() {
        NodePointer pointer = pointerFor("pointer value");
        Expression.ValueIterator values = new Expression.ValueIterator(
                Arrays.asList(new Object[] { pointer, "ordinary value" }).iterator());

        assertTrue(values.hasNext());
        assertEquals("pointer value", values.next());
        assertTrue(values.hasNext());
        assertEquals("ordinary value", values.next());
        assertFalse(values.hasNext());

        try {
            values.remove();
            fail("ValueIterator.remove() must not be supported");
        }
        catch (UnsupportedOperationException expected) {
            // Expected.
        }
    }

    private static NodePointer pointerFor(Object value) {
        return NodePointer.newNodePointer(
                new QName(null, "value"), value, Locale.US);
    }

    private static final class TestExpression extends Expression {
        private final Object result;
        private final boolean contextDependentResult;
        private int contextDependencyComputations;

        private TestExpression(Object result, boolean contextDependentResult) {
            this.result = result;
            this.contextDependentResult = contextDependentResult;
        }

        public boolean computeContextDependent() {
            contextDependencyComputations++;
            return contextDependentResult;
        }

        public Object computeValue(EvalContext context) {
            return result;
        }

        public Object compute(EvalContext context) {
            return result;
        }

        private int getContextDependencyComputations() {
            return contextDependencyComputations;
        }
    }

    /**
     * Controlled EvalContext used as an expression result.
     */
    private static final class ListEvalContext extends EvalContext {
        private final NodePointer currentNodePointer;
        private final Object[] elements;
        private int index;

        private ListEvalContext(NodePointer currentNodePointer, Object[] elements) {
            super(null);
            this.currentNodePointer = currentNodePointer;
            this.elements = elements;
        }

        public NodePointer getCurrentNodePointer() {
            return currentNodePointer;
        }

        public boolean setPosition(int position) {
            return false;
        }

        public int getDocumentOrder() {
            return 0;
        }

        public boolean nextNode() {
            return false;
        }

        public boolean hasNext() {
            return index < elements.length;
        }

        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return elements[index++];
        }

        private Object getExpectedElement(int elementIndex) {
            return elements[elementIndex];
        }
    }
}

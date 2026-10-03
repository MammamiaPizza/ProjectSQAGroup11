package org.apache.commons.jxpath.ri.compiler;

import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class ExpressionNodeSetIterationTest extends TestCase {

    public void testIterateReturnsValuesForNodeSetPointers() {
        Expression expression = new NodeSetExpression(new NodePointer[] {
            pointerTo("Nested: Name 1"),
            pointerTo("Nested: Name 2")
        });

        Iterator iterator = expression.iterate(null);

        assertTrue(iterator.hasNext());
        assertEquals("Nested: Name 1", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("Nested: Name 2", iterator.next());
        assertFalse(iterator.hasNext());
    }

    public void testIteratePointersPreservesNodeSetPointersAndOrder() {
        Expression expression = new NodeSetExpression(new NodePointer[] {
            pointerTo("Nested: Name 1"),
            pointerTo("Nested: Name 2")
        });

        Iterator iterator = expression.iteratePointers(null);

        assertTrue(iterator.hasNext());
        Object first = iterator.next();
        assertTrue(first instanceof Pointer);
        assertEquals("Nested: Name 1", ((Pointer) first).getValue());

        assertTrue(iterator.hasNext());
        Object second = iterator.next();
        assertTrue(second instanceof Pointer);
        assertEquals("Nested: Name 2", ((Pointer) second).getValue());

        assertFalse(iterator.hasNext());
    }

    public void testValueIteratorUnwrapsPointersButLeavesOrdinaryValuesUntouched() {
        Iterator source = new Iterator() {
            private final Object[] values = new Object[] {
                pointerTo("Nested: Name 1"), "plain value"
            };
            private int index;

            public boolean hasNext() {
                return index < values.length;
            }

            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return values[index++];
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        Expression.ValueIterator iterator = new Expression.ValueIterator(source);

        assertEquals("Nested: Name 1", iterator.next());
        assertEquals("plain value", iterator.next());
        assertFalse(iterator.hasNext());

        try {
            iterator.remove();
            fail("ValueIterator must not support remove");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true);
        }
    }

    private static NodePointer pointerTo(String value) {
        return NodePointer.newNodePointer(
                new QName(null, "value"), value, Locale.US);
    }

    private static class NodeSetExpression extends Expression {
        private final NodePointer[] pointers;

        NodeSetExpression(NodePointer[] pointers) {
            this.pointers = pointers;
        }

        public boolean computeContextDependent() {
            return false;
        }

        public Object computeValue(EvalContext context) {
            return pointers.length == 0 ? null : pointers[0].getValue();
        }

        public Object compute(EvalContext context) {
            return new PointerEvalContext(pointers);
        }
    }

    private static class PointerEvalContext extends EvalContext {
        private final NodePointer[] pointers;
        private int index;

        PointerEvalContext(NodePointer[] pointers) {
            super(null);
            this.pointers = pointers;
        }

        public boolean hasNext() {
            return index < pointers.length;
        }

        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return pointers[index++];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public NodePointer getCurrentNodePointer() {
            return index == 0 ? null : pointers[index - 1];
        }

        public boolean nextNode() {
            if (!hasNext()) {
                return false;
            }
            index++;
            return true;
        }

        public boolean nextSet() {
            return hasNext();
        }
    }
}

package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.EvalContext;

/**
 * Tests comparison semantics implemented by CoreOperationCompare.
 */
public class CoreOperationCompareTest extends TestCase {

    public CoreOperationCompareTest(String name) {
        super(name);
    }

    public void testEqualObjectsHandlesNullBooleanNumberStringAndOrdinaryValues() {
        TestCompare compare = new TestCompare();

        assertTrue(compare.compareObjects(null, null));
        assertFalse(compare.compareObjects(null, "value"));

        assertTrue(compare.compareObjects(Boolean.TRUE, "non-empty"));
        assertTrue(compare.compareObjects(Boolean.FALSE, ""));
        assertFalse(compare.compareObjects(Boolean.FALSE, "true"));

        assertTrue(compare.compareObjects(new Integer(3), new Double(3.0)));
        assertFalse(compare.compareObjects(new Integer(3), new Double(4.0)));

        assertTrue(compare.compareObjects("3", new Integer(3)));
        assertFalse(compare.compareObjects("3", new Integer(4)));

        assertTrue(compare.compareObjects(
                new Character('x'), new Character('x')));
        assertFalse(compare.compareObjects(
                new Character('x'), new Character('y')));
    }

    public void testContainsUsesXPathValueComparisonForIteratorElements() {
        TestCompare compare = new TestCompare();

        assertTrue(compare.containsValue(
                Arrays.asList(new Object[] { "first", new Integer(3) }).iterator(),
                "3"));

        assertFalse(compare.containsValue(
                Arrays.asList(new Object[] { "first", new Integer(3) }).iterator(),
                "missing"));
    }

    public void testExpressionComparisonMatchesIteratorAgainstScalarInEitherDirection() {
        TestCompare compare = new TestCompare();

        assertTrue(compare.compareExpressions(
                new ValueExpression(Arrays.asList(
                        new Object[] { "a", "b" }).iterator()),
                new ValueExpression("a")));

        assertTrue(compare.compareExpressions(
                new ValueExpression("b"),
                new ValueExpression(Arrays.asList(
                        new Object[] { "a", "b" }).iterator())));

        assertFalse(compare.compareExpressions(
                new ValueExpression(Arrays.asList(
                        new Object[] { "a", "b" }).iterator()),
                new ValueExpression("missing")));
    }

    public void testExpressionComparisonFindsMatchBetweenTwoIterators() {
        TestCompare compare = new TestCompare();

        assertTrue(compare.compareExpressions(
                new ValueExpression(Arrays.asList(
                        new Object[] { "unmatched", new Integer(1) }).iterator()),
                new ValueExpression(Arrays.asList(
                        new Object[] { "other", "1" }).iterator())));

        assertFalse(compare.compareExpressions(
                new ValueExpression(Arrays.asList(
                        new Object[] { "a", "b" }).iterator()),
                new ValueExpression(Arrays.asList(
                        new Object[] { "c", "d" }).iterator())));
    }

    /**
     * Regression test for JXPATH-94: a variable whose value is an iterator
     * must compare equal to one of the values returned by that iterator.
     */
    public void testIteratorVariableEqualsContainedStringValue() {
        JXPathContext context = JXPathContext.newContext(null);
        Iterator values = Arrays.asList(new Object[] { "a" }).iterator();

        context.getVariables().declareVariable("d", values);

        assertEquals(Boolean.TRUE, context.getValue("$d = 'a'"));
    }

    private static class TestCompare extends CoreOperationCompare {

        TestCompare() {
            super(null, null);
        }

        public Object compute(EvalContext context) {
            return null;
        }

        public String toString() {
            return "test-compare";
        }

        boolean compareObjects(Object left, Object right) {
            return equal(left, right);
        }

        boolean containsValue(Iterator values, Object value) {
            return contains(values, value);
        }

        boolean compareExpressions(Expression left, Expression right) {
            return equal(null, left, right);
        }
    }

    private static class ValueExpression extends Expression {

        private final Object value;

        ValueExpression(Object value) {
            this.value = value;
        }

        public Object compute(EvalContext context) {
            return value;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }
}

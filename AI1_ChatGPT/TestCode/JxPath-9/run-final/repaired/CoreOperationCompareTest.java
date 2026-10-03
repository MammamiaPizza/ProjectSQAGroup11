package org.apache.commons.jxpath.ri.compiler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests comparison semantics for XPath "=" and "!=" expressions.
 */
public class CoreOperationCompareTest extends TestCase {

    private static class LiteralExpression extends Expression {
        private final Object value;

        LiteralExpression(Object value) {
            this.value = value;
        }

        public Object compute(org.apache.commons.jxpath.ri.EvalContext context) {
            return value;
        }

        public Object computeValue(org.apache.commons.jxpath.ri.EvalContext context) {
            return value;
        }

        public String toString() {
            return String.valueOf(value);
        }
    }

    private static class ExposedEqualOperation extends CoreOperationEqual {
        ExposedEqualOperation(Expression left, Expression right) {
            super(left, right);
        }

        int precedence() {
            return getPrecedence();
        }

        boolean symmetric() {
            return isSymmetric();
        }
    }

    private static class Value {
        private final int id;

        Value(int id) {
            this.id = id;
        }

        public boolean equals(Object object) {
            return object instanceof Value && ((Value) object).id == id;
        }

        public int hashCode() {
            return id;
        }
    }

    private Expression literal(Object value) {
        return new LiteralExpression(value);
    }

    private List list(Object first, Object second) {
        List values = new ArrayList();
        values.add(first);
        values.add(second);
        return values;
    }

    public void testNaNIsNeverEqualEvenWhenBothExpressionsReturnSameObject() {
        Double nan = new Double(Double.NaN);

        CoreOperationEqual equal =
                new CoreOperationEqual(literal(nan), literal(nan));
        CoreOperationNotEqual notEqual =
                new CoreOperationNotEqual(literal(nan), literal(nan));

        assertEquals("XPath NaN must not compare equal to itself",
                Boolean.FALSE, equal.computeValue(null));
        assertEquals("XPath NaN must compare not-equal to itself",
                Boolean.TRUE, notEqual.computeValue(null));
    }

    public void testScalarComparisonConversionsAndNullValues() {
        CoreOperationEqual numericEqual =
                new CoreOperationEqual(
                        literal(new Integer(4)),
                        literal(new Double(4.0)));
        CoreOperationEqual stringEqual =
                new CoreOperationEqual(
                        literal(new String("value")),
                        literal(new String("value")));
        CoreOperationEqual booleanDifferent =
                new CoreOperationEqual(
                        literal(Boolean.TRUE),
                        literal(Boolean.FALSE));
        CoreOperationEqual nullEqual =
                new CoreOperationEqual(literal(null), literal(null));
        CoreOperationEqual nullDifferent =
                new CoreOperationEqual(literal(null), literal(new Value(1)));
        CoreOperationEqual objectEqual =
                new CoreOperationEqual(
                        literal(new Value(7)),
                        literal(new Value(7)));
        CoreOperationNotEqual objectNotEqual =
                new CoreOperationNotEqual(
                        literal(new Value(7)),
                        literal(new Value(8)));

        assertEquals(Boolean.TRUE, numericEqual.computeValue(null));
        assertEquals(Boolean.TRUE, stringEqual.computeValue(null));
        assertEquals(Boolean.FALSE, booleanDifferent.computeValue(null));
        assertEquals(Boolean.TRUE, nullEqual.computeValue(null));
        assertEquals(Boolean.FALSE, nullDifferent.computeValue(null));
        assertEquals(Boolean.TRUE, objectEqual.computeValue(null));
        assertEquals(Boolean.TRUE, objectNotEqual.computeValue(null));
    }

    public void testCollectionAndIteratorComparisonsFindAnyMatchingValue() {
        List leftValues = list("first", "match");
        List rightValues = list("other", "match");

        CoreOperationEqual collectionToScalar =
                new CoreOperationEqual(literal(leftValues), literal("match"));
        CoreOperationEqual scalarToIterator =
                new CoreOperationEqual(
                        literal("match"),
                        literal(rightValues.iterator()));
        CoreOperationEqual collectionToCollection =
                new CoreOperationEqual(literal(leftValues), literal(rightValues));
        CoreOperationNotEqual collectionToCollectionNotEqual =
                new CoreOperationNotEqual(literal(leftValues), literal(rightValues));

        assertEquals(Boolean.TRUE, collectionToScalar.computeValue(null));
        assertEquals(Boolean.TRUE, scalarToIterator.computeValue(null));
        assertEquals(Boolean.TRUE, collectionToCollection.computeValue(null));
        assertEquals(Boolean.FALSE,
                collectionToCollectionNotEqual.computeValue(null));
    }

    public void testCollectionsContainingNaNDoNotProduceAMatch() {
        Double nan = new Double(Double.NaN);
        List leftValues = new ArrayList();
        List rightValues = new ArrayList();
        leftValues.add(nan);
        rightValues.add(nan);

        CoreOperationEqual equal =
                new CoreOperationEqual(literal(leftValues), literal(rightValues));
        CoreOperationNotEqual notEqual =
                new CoreOperationNotEqual(literal(leftValues), literal(rightValues));

        assertEquals("A collection match must use XPath numeric NaN semantics",
                Boolean.FALSE, equal.computeValue(null));
        assertEquals(Boolean.TRUE, notEqual.computeValue(null));
    }

    public void testEmptyAndNonMatchingIteratorsDoNotCompareEqual() {
        List empty = new ArrayList();
        List values = list("left", "right");

        CoreOperationEqual emptyToValue =
                new CoreOperationEqual(literal(empty), literal("left"));
        CoreOperationEqual noMatch =
                new CoreOperationEqual(
                        literal(values.iterator()),
                        literal("missing"));
        CoreOperationNotEqual noMatchNotEqual =
                new CoreOperationNotEqual(
                        literal(values.iterator()),
                        literal("missing"));

        assertEquals(Boolean.FALSE, emptyToValue.computeValue(null));
        assertEquals(Boolean.FALSE, noMatch.computeValue(null));
        assertEquals(Boolean.TRUE, noMatchNotEqual.computeValue(null));
    }

    public void testOperationSymbolsPrecedenceAndSymmetry() {
        ExposedEqualOperation equal =
                new ExposedEqualOperation(literal("left"), literal("right"));
        CoreOperationNotEqual notEqual =
                new CoreOperationNotEqual(literal("left"), literal("right"));

        assertEquals("=", equal.getSymbol());
        assertEquals("!=", notEqual.getSymbol());
        assertEquals(2, equal.precedence());
        assertTrue(equal.symmetric());
    }
}

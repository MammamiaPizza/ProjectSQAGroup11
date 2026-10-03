package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.EvalContext;

/**
 * Tests relational-expression evaluation, including XPath node-set behavior.
 */
public class CoreOperationRelationalExpressionTest extends TestCase {

    private static final int GREATER_THAN = 1;
    private static final int GREATER_THAN_OR_EQUAL = 2;
    private static final int LESS_THAN = 3;
    private static final int LESS_THAN_OR_EQUAL = 4;

    public CoreOperationRelationalExpressionTest(String name) {
        super(name);
    }

    public void testScalarComparisonsAndExpressionProperties() {
        TestRelationalExpression greater =
                operation(GREATER_THAN, new Integer(5), new Integer(4));
        TestRelationalExpression greaterOrEqual =
                operation(GREATER_THAN_OR_EQUAL, new Integer(4), new Integer(4));
        TestRelationalExpression less =
                operation(LESS_THAN, new Integer(3), new Integer(4));
        TestRelationalExpression lessOrEqual =
                operation(LESS_THAN_OR_EQUAL, new Integer(4), new Integer(4));

        assertEquals(Boolean.TRUE, greater.computeValue(null));
        assertEquals(Boolean.TRUE, greaterOrEqual.computeValue(null));
        assertEquals(Boolean.TRUE, less.computeValue(null));
        assertEquals(Boolean.TRUE, lessOrEqual.computeValue(null));

        assertEquals(3, greater.precedence());
        assertFalse(greater.symmetric());
    }

    public void testNaNOperandsNeverMatch() {
        assertEquals(Boolean.FALSE,
                operation(GREATER_THAN, Double.NaN, new Integer(0))
                        .computeValue(null));
        assertEquals(Boolean.FALSE,
                operation(LESS_THAN, new Integer(0), Double.NaN)
                        .computeValue(null));
    }

    public void testCollectionAndEmptyCollectionComparisons() {
        assertEquals(Boolean.TRUE,
                operation(GREATER_THAN_OR_EQUAL,
                        Arrays.asList(new Integer(1), new Integer(5)),
                        new Integer(4)).computeValue(null));

        assertEquals(Boolean.TRUE,
                operation(GREATER_THAN_OR_EQUAL,
                        new Integer(4),
                        Collections.singletonList(new Integer(4)))
                        .computeValue(null));

        assertEquals(Boolean.FALSE,
                operation(GREATER_THAN_OR_EQUAL,
                        Collections.EMPTY_LIST,
                        new Integer(0)).computeValue(null));
    }

    public void testIteratorToIteratorComparisonFindsOnlyMatchingPairs() {
        assertEquals(Boolean.TRUE,
                operation(GREATER_THAN_OR_EQUAL,
                        Arrays.asList(new Integer(2)).iterator(),
                        Arrays.asList(new Integer(2)).iterator())
                        .computeValue(null));

        assertEquals(Boolean.FALSE,
                operation(GREATER_THAN_OR_EQUAL,
                        Arrays.asList(new Integer(1)).iterator(),
                        Arrays.asList(new Integer(2)).iterator())
                        .computeValue(null));
    }

    public void testMissingAbsolutePathIsNotRelationallyEqualToZero() {
        JXPathContext context = JXPathContext.newContext(new Object());

        assertEquals(Boolean.FALSE, context.getValue("/idonotexist >= 0"));
    }

    private TestRelationalExpression operation(int operation,
            Object left, Object right) {
        return new TestRelationalExpression(
                new Expression[] {
                    new LiteralExpression(left),
                    new LiteralExpression(right)
                },
                operation);
    }

    /**
     * Test expression used for values that cannot be represented by
     * Constant, such as Collections and Iterators.
     */
    private static class LiteralExpression extends Expression {

        private final Object value;

        LiteralExpression(Object value) {
            this.value = value;
        }

        public Object computeValue(EvalContext context) {
            return value;
        }

        protected int getPrecedence() {
            return 0;
        }

        protected boolean isSymmetric() {
            return false;
        }
    }

    private static class TestRelationalExpression
            extends CoreOperationRelationalExpression {

        private final int operation;

        TestRelationalExpression(Expression[] args, int operation) {
            super(args);
            this.operation = operation;
        }

        protected boolean evaluateCompare(int compare) {
            switch (operation) {
            case GREATER_THAN:
                return compare > 0;
            case GREATER_THAN_OR_EQUAL:
                return compare >= 0;
            case LESS_THAN:
                return compare < 0;
            case LESS_THAN_OR_EQUAL:
                return compare <= 0;
            default:
                throw new IllegalStateException("Unknown operation");
            }
        }

        int precedence() {
            return getPrecedence();
        }

        boolean symmetric() {
            return isSymmetric();
        }
    }
}

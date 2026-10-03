```java
package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.EvalContext;

/**
 * Tests XPath relational expressions, including the node-set/array behavior
 * addressed by JXPATH-93.
 */
public class CoreOperationRelationalExpressionTest extends TestCase {

    public void testConcreteOperationsWithScalarOperandsAndMetadata() {
        CoreOperationGreaterThan greaterThan =
                new CoreOperationGreaterThan(expression(2), expression(1));
        CoreOperationGreaterThanOrEqual greaterThanOrEqual =
                new CoreOperationGreaterThanOrEqual(expression(1), expression(1));
        CoreOperationLessThan lessThan =
                new CoreOperationLessThan(expression(1), expression(2));
        CoreOperationLessThanOrEqual lessThanOrEqual =
                new CoreOperationLessThanOrEqual(expression(1), expression(1));

        assertEquals(Boolean.TRUE, greaterThan.computeValue(null));
        assertEquals(Boolean.TRUE, greaterThanOrEqual.computeValue(null));
        assertEquals(Boolean.TRUE, lessThan.computeValue(null));
        assertEquals(Boolean.TRUE, lessThanOrEqual.computeValue(null));

        assertEquals(">", greaterThan.getSymbol());
        assertEquals(">=", greaterThanOrEqual.getSymbol());
        assertEquals("<", lessThan.getSymbol());
        assertEquals("<=", lessThanOrEqual.getSymbol());

        assertEquals(3, greaterThan.getPrecedence());
        assertEquals(3, greaterThanOrEqual.getPrecedence());
        assertEquals(3, lessThan.getPrecedence());
        assertEquals(3, lessThanOrEqual.getPrecedence());

        assertFalse(greaterThan.isSymmetric());
        assertFalse(greaterThanOrEqual.isSymmetric());
        assertFalse(lessThan.isSymmetric());
        assertFalse(lessThanOrEqual.isSymmetric());
    }

    public void testNodeSetRelationalOperationsMatchAnyEligibleArrayElement() {
        int[] values = new int[] { -2, 0, 3 };

        assertEquals(Boolean.TRUE, evaluate(values, ">"));
        assertEquals(Boolean.TRUE, evaluate(values, ">="));
        assertEquals(Boolean.TRUE, evaluate(values, "<"));
        assertEquals(Boolean.TRUE, evaluate(values, "<="));
    }

    public void testNodeSetRelationalOperationsReturnFalseWhenNoElementMatches() {
        assertEquals(Boolean.FALSE, evaluate(new int[] { -3, 0 }, ">"));
        assertEquals(Boolean.FALSE, evaluate(new int[] { -3, -1 }, ">="));
        assertEquals(Boolean.FALSE, evaluate(new int[] { 0, 3 }, "<"));
        assertEquals(Boolean.FALSE, evaluate(new int[] { 1, 3 }, "<="));
    }

    public void testEmptyNodeSetDoesNotSatisfyAnyRelationalOperation() {
        int[] emptyValues = new int[0];

        assertEquals(Boolean.FALSE, evaluate(emptyValues, ">"));
        assertEquals(Boolean.FALSE, evaluate(emptyValues, ">="));
        assertEquals(Boolean.FALSE, evaluate(emptyValues, "<"));
        assertEquals(Boolean.FALSE, evaluate(emptyValues, "<="));
    }

    private Boolean evaluate(Object values, String operator) {
        JXPathContext context = JXPathContext.newContext(new Object());
        context.getVariables().declareVariable("array", values);
        return (Boolean) context.getValue("$array " + operator + " 0");
    }

    private Expression expression(final Object value) {
        return new Expression() {
            public Object computeValue(EvalContext context) {
                return value;
            }

            public String getExpression() {
                return String.valueOf(value);
            }
        };
    }
}
```

Test coverage summary:

- `testConcreteOperationsWithScalarOperandsAndMetadata`
  - Exercises each concrete relational operation with normal scalar numeric operands.
  - Verifies the true result branches of `>`, `>=`, `<`, and `<=`.
  - Verifies each operator symbol.
  - Verifies the shared relational-expression precedence and non-symmetric behavior.

- `testNodeSetRelationalOperationsMatchAnyEligibleArrayElement`
  - Targets JXPATH-93 directly.
  - Verifies that an array/node-set comparison succeeds when at least one array element satisfies the comparison, including the reported `$array > 0` behavior.

- `testNodeSetRelationalOperationsReturnFalseWhenNoElementMatches`
  - Exercises false branches for all four operators when no element in the array satisfies the comparison.

- `testEmptyNodeSetDoesNotSatisfyAnyRelationalOperation`
  - Covers the empty-array/node-set boundary case.
  - Verifies that no relational comparison succeeds when there are no candidate elements.
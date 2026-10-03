package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;
import org.apache.commons.jxpath.JXPathContext;

public class CoreOperationRelationalExpressionArrayTest extends TestCase {

    public void testRelationalOperatorsUseAnyMatchingArrayElement() {
        JXPathContext context = contextWithArray(new Integer[] {
            new Integer(-3), new Integer(2)
        });

        assertEquals(Boolean.TRUE, context.getValue("$array > 0"));
        assertEquals(Boolean.TRUE, context.getValue("$array >= 2"));
        assertEquals(Boolean.TRUE, context.getValue("$array < 0"));
        assertEquals(Boolean.TRUE, context.getValue("$array <= -3"));
    }

    public void testRelationalOperatorsAreFalseWhenNoArrayElementMatches() {
        JXPathContext greaterContext = contextWithArray(new Integer[] {
            new Integer(-3), new Integer(0)
        });
        assertEquals(Boolean.FALSE, greaterContext.getValue("$array > 0"));

        JXPathContext greaterEqualContext = contextWithArray(new Integer[] {
            new Integer(-3), new Integer(-1)
        });
        assertEquals(Boolean.FALSE, greaterEqualContext.getValue("$array >= 0"));

        JXPathContext lessContext = contextWithArray(new Integer[] {
            new Integer(0), new Integer(3)
        });
        assertEquals(Boolean.FALSE, lessContext.getValue("$array < 0"));

        JXPathContext lessEqualContext = contextWithArray(new Integer[] {
            new Integer(1), new Integer(3)
        });
        assertEquals(Boolean.FALSE, lessEqualContext.getValue("$array <= 0"));
    }

    public void testInclusiveOperatorsMatchEqualArrayElement() {
        JXPathContext context = contextWithArray(new Integer[] {
            new Integer(0)
        });

        assertEquals(Boolean.FALSE, context.getValue("$array > 0"));
        assertEquals(Boolean.TRUE, context.getValue("$array >= 0"));
        assertEquals(Boolean.FALSE, context.getValue("$array < 0"));
        assertEquals(Boolean.TRUE, context.getValue("$array <= 0"));
    }

    public void testRelationalOperatorsSupportArrayOnRightHandSide() {
        JXPathContext context = contextWithArray(new Integer[] {
            new Integer(-2), new Integer(3)
        });

        assertEquals(Boolean.TRUE, context.getValue("0 < $array"));
        assertEquals(Boolean.TRUE, context.getValue("0 <= $array"));
        assertEquals(Boolean.TRUE, context.getValue("0 > $array"));
        assertEquals(Boolean.TRUE, context.getValue("0 >= $array"));
    }

    public void testEmptyArrayDoesNotSatisfyRelationalComparison() {
        JXPathContext context = contextWithArray(new Integer[0]);

        assertEquals(Boolean.FALSE, context.getValue("$array > 0"));
        assertEquals(Boolean.FALSE, context.getValue("$array >= 0"));
        assertEquals(Boolean.FALSE, context.getValue("$array < 0"));
        assertEquals(Boolean.FALSE, context.getValue("$array <= 0"));
    }

    public void testScalarNumericRelationalComparisonsRemainCorrect() {
        JXPathContext context = JXPathContext.newContext(new Object());

        assertEquals(Boolean.TRUE, context.getValue("3 > 2"));
        assertEquals(Boolean.TRUE, context.getValue("3 >= 3"));
        assertEquals(Boolean.TRUE, context.getValue("2 < 3"));
        assertEquals(Boolean.TRUE, context.getValue("2 <= 2"));
    }

    private JXPathContext contextWithArray(Integer[] values) {
        JXPathContext context = JXPathContext.newContext(new Object());
        context.getVariables().declareVariable("array", values);
        return context;
    }
}

package org.apache.commons.jxpath.ri.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

public class CoreOperationRelationalExpressionTest extends TestCase {

    public void testVariableArithmeticLessThanOrEqualAtEqualityBoundary() {
        JXPathContext context = contextWithNumbers(2, 3, 5);

        assertEquals(Boolean.TRUE, context.getValue("$a + $b <= $c"));
    }

    public void testVariableArithmeticLessThanOrEqualWhenSumIsBelowRightOperand() {
        JXPathContext context = contextWithNumbers(2, 3, 6);

        assertEquals(Boolean.TRUE, context.getValue("$a + $b <= $c"));
    }

    public void testVariableArithmeticLessThanOrEqualWhenSumIsAboveRightOperand() {
        JXPathContext context = contextWithNumbers(2, 4, 5);

        assertEquals(Boolean.FALSE, context.getValue("$a + $b <= $c"));
    }

    public void testStrictAndNonStrictRelationalOperatorsAtEquality() {
        JXPathContext context = contextWithNumbers(4, 1, 5);

        assertEquals(Boolean.TRUE, context.getValue("$a + $b >= $c"));
        assertEquals(Boolean.FALSE, context.getValue("$a + $b < $c"));
        assertEquals(Boolean.FALSE, context.getValue("$a + $b > $c"));
    }

    public void testCollectionOperandMatchesWhenAnyElementSatisfiesRelation() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("values",
                list(new Integer(7), new Integer(12)));
        context.getVariables().declareVariable("limit", new Integer(10));

        assertEquals(Boolean.TRUE, context.getValue("$values <= $limit"));
    }

    public void testCollectionOperandDoesNotMatchWhenNoElementSatisfiesRelation() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("values",
                list(new Integer(11), new Integer(12)));
        context.getVariables().declareVariable("limit", new Integer(10));

        assertEquals(Boolean.FALSE, context.getValue("$values <= $limit"));
    }

    public void testIteratorOperandMatchesScalarRelation() {
        JXPathContext context = JXPathContext.newContext(null);
        Iterator values = list(new Integer(3), new Integer(9)).iterator();
        context.getVariables().declareVariable("values", values);
        context.getVariables().declareVariable("limit", new Integer(5));

        assertEquals(Boolean.TRUE, context.getValue("$values <= $limit"));
    }

    public void testTwoCollectionsMatchWhenAComparablePairSatisfiesRelation() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("left",
                list(new Integer(11), new Integer(15)));
        context.getVariables().declareVariable("right",
                list(new Integer(10), new Integer(12)));

        assertEquals(Boolean.TRUE, context.getValue("$left <= $right"));
    }

    public void testNonNumericOperandDoesNotSatisfyRelation() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("value", "not-a-number");
        context.getVariables().declareVariable("limit", new Integer(10));

        assertEquals(Boolean.FALSE, context.getValue("$value <= $limit"));
    }

    private JXPathContext contextWithNumbers(int a, int b, int c) {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("a", new Integer(a));
        context.getVariables().declareVariable("b", new Integer(b));
        context.getVariables().declareVariable("c", new Integer(c));
        return context;
    }

    private List list(Object first, Object second) {
        List values = new ArrayList();
        values.add(first);
        values.add(second);
        return values;
    }
}

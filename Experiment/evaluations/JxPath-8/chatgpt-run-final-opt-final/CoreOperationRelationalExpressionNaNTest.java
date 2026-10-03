package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

public class CoreOperationRelationalExpressionNaNTest extends TestCase {

    public void testNaNComparedWithItselfIsFalseForAllRelationalOperators() {
        JXPathContext context = contextWithNaN();

        assertEquals(Boolean.FALSE, context.getValue("$nan > $nan"));
        assertEquals(Boolean.FALSE, context.getValue("$nan >= $nan"));
        assertEquals(Boolean.FALSE, context.getValue("$nan < $nan"));
        assertEquals(Boolean.FALSE, context.getValue("$nan <= $nan"));
    }

    public void testNaNAndFiniteNumberDoNotMatchInEitherDirection() {
        JXPathContext context = contextWithNaN();
        context.getVariables().declareVariable("number", new Double(2.0));

        assertEquals(Boolean.FALSE, context.getValue("$nan > $number"));
        assertEquals(Boolean.FALSE, context.getValue("$number > $nan"));
        assertEquals(Boolean.FALSE, context.getValue("$nan < $number"));
        assertEquals(Boolean.FALSE, context.getValue("$number < $nan"));
    }

    public void testNaNIteratorHasNoMatchAgainstScalar() {
        JXPathContext context = contextWithNaN();
        context.getVariables().declareVariable(
                "nanValues",
                Arrays.asList(new Object[] { new Double(Double.NaN) }));
        context.getVariables().declareVariable("number", new Double(2.0));

        assertEquals(Boolean.FALSE, context.getValue("$nanValues > $number"));
        assertEquals(Boolean.FALSE, context.getValue("$number > $nanValues"));
    }

    public void testTwoNaNIteratorsHaveNoRelationalMatch() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable(
                "left",
                Arrays.asList(new Object[] { new Double(Double.NaN) }));
        context.getVariables().declareVariable(
                "right",
                Arrays.asList(new Object[] { new Double(Double.NaN) }));

        assertEquals(Boolean.FALSE, context.getValue("$left > $right"));
    }

    public void testOrdinaryRelationalComparisonsStillEvaluateNormally() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("two", new Double(2.0));
        context.getVariables().declareVariable("three", new Double(3.0));

        assertEquals(Boolean.TRUE, context.getValue("$two < $three"));
        assertEquals(Boolean.TRUE, context.getValue("$three >= $three"));
        assertEquals(Boolean.FALSE, context.getValue("$three < $two"));
    }

    private JXPathContext contextWithNaN() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("nan", new Double(Double.NaN));
        return context;
    }
}

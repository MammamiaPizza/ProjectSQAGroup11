package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

public class CoreOperationNaNTest extends TestCase {

    public void testNaNEqualsItselfIsFalseInExpression() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("nan", new Double(Double.NaN));

        assertEquals(Boolean.FALSE, context.getValue("$nan = $nan"));
    }

    public void testNaNNotEqualsItselfIsTrueInExpression() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable("nan", new Double(Double.NaN));

        assertEquals(Boolean.FALSE, context.getValue("$nan != $nan"));
    }

    public void testEqualDoesNotConsiderSameNaNReferenceEqual() {
        CoreOperationEqual operation = new CoreOperationEqual(null, null);
        Double nan = new Double(Double.NaN);

        assertFalse(operation.equal(nan, nan));
    }

    public void testEqualDoesNotConsiderDistinctNaNValuesEqual() {
        CoreOperationEqual operation = new CoreOperationEqual(null, null);

        assertFalse(operation.equal(
                new Double(Double.NaN), new Double(Double.NaN)));
    }

    public void testContainsDoesNotMatchNaN() {
        CoreOperationEqual operation = new CoreOperationEqual(null, null);
        Double nan = new Double(Double.NaN);

        assertFalse(operation.contains(
                Collections.singletonList(nan).iterator(), nan));
    }

    public void testFindMatchDoesNotMatchNaNAcrossIterators() {
        CoreOperationEqual operation = new CoreOperationEqual(null, null);
        Double nan = new Double(Double.NaN);

        assertFalse(operation.findMatch(
                Arrays.asList(new Object[] { nan }).iterator(),
                Arrays.asList(new Object[] { nan }).iterator()));
    }

    public void testNaNDoesNotEqualOrdinaryNumber() {
        CoreOperationEqual operation = new CoreOperationEqual(null, null);

        assertFalse(operation.equal(new Double(Double.NaN), new Double(1.0)));
    }

    public void testOrdinaryEqualNumbersStillMatch() {
        CoreOperationEqual operation = new CoreOperationEqual(null, null);

        assertTrue(operation.equal(new Double(2.0), new Double(2.0)));
    }
}
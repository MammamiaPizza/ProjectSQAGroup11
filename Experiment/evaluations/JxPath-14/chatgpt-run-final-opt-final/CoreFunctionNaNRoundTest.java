package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;
import org.apache.commons.jxpath.JXPathContext;

public class CoreFunctionNaNRoundTest extends TestCase {

    public void testRoundOfNaNReturnsNaN() {
        JXPathContext context = JXPathContext.newContext(new Object());

        Object result = context.getValue("round('NaN')");

        assertTrue(result instanceof Number);
        assertTrue(Double.isNaN(((Number) result).doubleValue()));
    }
}

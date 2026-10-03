package org.apache.commons.jxpath.ri.compiler;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;

public class CoreOperationRelationalExpressionTest extends TestCase {

    public void testEmptyNodeSetIsNotGreaterThanOrEqualToNumber() {
        assertEquals(Boolean.FALSE, context().getValue("/idonotexist >= 0"));
    }

    public void testEmptyNodeSetIsNotLessThanOrEqualToNumber() {
        assertEquals(Boolean.FALSE, context().getValue("/idonotexist <= 0"));
    }

    public void testEmptyNodeSetIsNotGreaterThanAnyNumber() {
        assertEquals(Boolean.FALSE, context().getValue("/idonotexist > -1"));
    }

    public void testEmptyNodeSetIsNotLessThanAnyNumber() {
        assertEquals(Boolean.FALSE, context().getValue("/idonotexist < 1"));
    }

    public void testNonEmptyNodeSetUsesRelationalComparisonAtBoundary() {
        JXPathContext context = context();

        assertEquals(Boolean.TRUE, context.getValue("/number >= 2"));
        assertEquals(Boolean.TRUE, context.getValue("/number <= 2"));
        assertEquals(Boolean.FALSE, context.getValue("/number > 2"));
        assertEquals(Boolean.FALSE, context.getValue("/number < 2"));
    }

    public void testScalarRelationalComparisonsUseNumericOrdering() {
        JXPathContext context = context();

        assertEquals(Boolean.TRUE, context.getValue("2 < 3"));
        assertEquals(Boolean.TRUE, context.getValue("3 > 2"));
        assertEquals(Boolean.TRUE, context.getValue("2 <= 2"));
        assertEquals(Boolean.TRUE, context.getValue("2 >= 2"));
    }

    private JXPathContext context() {
        return JXPathContext.newContext(new TestBean());
    }

    public static class TestBean {
        public int getNumber() {
            return 2;
        }
    }
}
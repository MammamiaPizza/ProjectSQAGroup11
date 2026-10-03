package org.apache.commons.jxpath.ri.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.EvalContext;

public class CoreOperationCompareTest extends TestCase {

    private static class Comparison extends CoreOperationCompare {
        Comparison() {
            super(null, null);
        }

        public Object compute(EvalContext context) {
            return null;
        }

        boolean containsValue(Iterator iterator, Object value) {
            return contains(iterator, value);
        }

        boolean hasMatch(Iterator left, Iterator right) {
            return findMatch(left, right);
        }
    }

    public void testVariableIteratorEqualsMatchingScalar() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable(
                "d", Arrays.asList(new String[] { "a" }).iterator());

        assertEquals(Boolean.TRUE, context.getValue("$d = 'a'"));
    }

    public void testVariableIteratorEqualsScalarWhenMatchOccursLater() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable(
                "d", Arrays.asList(new String[] { "x", "a", "z" }).iterator());

        assertEquals(Boolean.TRUE, context.getValue("$d = 'a'"));
    }

    public void testVariableIteratorEqualsScalarWhenNoValueMatches() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable(
                "d", Arrays.asList(new String[] { "x", "y" }).iterator());

        assertEquals(Boolean.FALSE, context.getValue("$d = 'a'"));
    }

    public void testVariableEmptyIteratorDoesNotEqualScalar() {
        JXPathContext context = JXPathContext.newContext(null);
        context.getVariables().declareVariable(
                "d", Collections.EMPTY_LIST.iterator());

        assertEquals(Boolean.FALSE, context.getValue("$d = 'a'"));
    }

    public void testContainsFindsFirstAndLaterIteratorElements() {
        Comparison comparison = new Comparison();

        assertTrue(comparison.containsValue(
                Arrays.asList(new String[] { "a", "x" }).iterator(), "a"));
        assertTrue(comparison.containsValue(
                Arrays.asList(new String[] { "x", "a" }).iterator(), "a"));
        assertFalse(comparison.containsValue(
                Arrays.asList(new String[] { "x", "y" }).iterator(), "a"));
    }

    public void testFindMatchReturnsTrueForIteratorsWithSharedValue() {
        Comparison comparison = new Comparison();

        assertTrue(comparison.hasMatch(
                Arrays.asList(new String[] { "x", "a" }).iterator(),
                Arrays.asList(new String[] { "z", "a" }).iterator()));
    }

    public void testFindMatchReturnsFalseForIteratorsWithoutSharedValue() {
        Comparison comparison = new Comparison();

        assertFalse(comparison.hasMatch(
                Arrays.asList(new String[] { "x", "y" }).iterator(),
                Arrays.asList(new String[] { "a", "b" }).iterator()));
    }
}

public void testComparisonHandlesBooleanStringAndObjectValues() {
    ExposedEqualOperation operation = new ExposedEqualOperation();

    assertTrue(operation.valuesEqual(Boolean.TRUE, Boolean.TRUE));
    assertFalse(operation.valuesEqual(Boolean.TRUE, Boolean.FALSE));
    assertTrue(operation.valuesEqual(new String("value"), new String("value")));
    assertFalse(operation.valuesEqual("value", "other"));
    assertTrue(operation.valuesEqual(new java.util.Date(0), new java.util.Date(0)));
    assertFalse(operation.valuesEqual(null, null));
}

public void testContainsFindsOnlyMatchingIteratorValues() {
    ExposedEqualOperation operation = new ExposedEqualOperation();

    assertTrue(operation.valuesContain(
            java.util.Arrays.asList(new Object[] {
                new Integer(1), new Integer(2), new Integer(3)
            }).iterator(),
            new Integer(2)));
    assertFalse(operation.valuesContain(
            java.util.Arrays.asList(new Object[] {
                new Integer(1), new Integer(2)
            }).iterator(),
            new Integer(3)));
}

public void testFindMatchDetectsMatchingAndDisjointIterators() {
    ExposedEqualOperation operation = new ExposedEqualOperation();

    assertTrue(operation.valuesMatch(
            java.util.Arrays.asList(new Object[] {
                new Integer(1), new Integer(2)
            }).iterator(),
            java.util.Arrays.asList(new Object[] {
                new Integer(3), new Integer(2)
            }).iterator()));
    assertFalse(operation.valuesMatch(
            java.util.Arrays.asList(new Object[] {
                new Integer(1), new Integer(2)
            }).iterator(),
            java.util.Arrays.asList(new Object[] {
                new Integer(3), new Integer(4)
            }).iterator()));
}

private static class ExposedEqualOperation
        extends org.apache.commons.jxpath.ri.compiler.CoreOperationEqual {
    ExposedEqualOperation() {
        super(null, null);
    }

    boolean valuesEqual(Object left, Object right) {
        return equal(left, right);
    }

    boolean valuesContain(java.util.Iterator values, Object value) {
        return contains(values, value);
    }

    boolean valuesMatch(java.util.Iterator left, java.util.Iterator right) {
        return findMatch(left, right);
    }
}
public void testIsContextDependentCachesComputedValue() {
    final int[] calls = new int[1];
    org.apache.commons.jxpath.ri.compiler.Expression expression =
        new org.apache.commons.jxpath.ri.compiler.Expression() {
            public boolean computeContextDependent() {
                calls[0]++;
                return true;
            }

            public Object computeValue(
                    org.apache.commons.jxpath.ri.EvalContext context) {
                return null;
            }

            public Object compute(org.apache.commons.jxpath.ri.EvalContext context) {
                return null;
            }
        };

    assertTrue(expression.isContextDependent());
    assertTrue(expression.isContextDependent());
    assertEquals(1, calls[0]);
}

public void testIterateReturnsValuesFromOrdinaryIteratorResult() {
    final java.util.List values = new java.util.ArrayList();
    values.add("first");
    values.add("second");

    org.apache.commons.jxpath.ri.compiler.Expression expression =
        new org.apache.commons.jxpath.ri.compiler.Expression() {
            public boolean computeContextDependent() {
                return false;
            }

            public Object computeValue(
                    org.apache.commons.jxpath.ri.EvalContext context) {
                return null;
            }

            public Object compute(org.apache.commons.jxpath.ri.EvalContext context) {
                return values.iterator();
            }
        };

    java.util.Iterator iterator = expression.iterate(null);
    assertTrue(iterator.hasNext());
    assertEquals("first", iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals("second", iterator.next());
    assertFalse(iterator.hasNext());
}

public void testIteratePointersReturnsEmptyIteratorForNullResult() {
    org.apache.commons.jxpath.ri.compiler.Expression expression =
        new org.apache.commons.jxpath.ri.compiler.Expression() {
            public boolean computeContextDependent() {
                return false;
            }

            public Object computeValue(
                    org.apache.commons.jxpath.ri.EvalContext context) {
                return null;
            }

            public Object compute(org.apache.commons.jxpath.ri.EvalContext context) {
                return null;
            }
        };

    assertFalse(expression.iteratePointers(null).hasNext());
}
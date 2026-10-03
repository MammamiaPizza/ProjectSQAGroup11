public void testRelationalNodeSetToNodeSetComparisons() {
    assertEquals(Boolean.TRUE, context().getValue("/number >= /number"));
    assertEquals(Boolean.FALSE, context().getValue("/number >= /idonotexist"));
    assertEquals(Boolean.FALSE, context().getValue("/idonotexist >= /number"));
}

public void testRelationalScalarToNodeSetComparison() {
    assertEquals(Boolean.TRUE, context().getValue("2 <= /number"));
}

public void testRelationalNonNumericValueIsFalse() {
    assertEquals(Boolean.FALSE, context().getValue("'not-a-number' >= 0"));
}
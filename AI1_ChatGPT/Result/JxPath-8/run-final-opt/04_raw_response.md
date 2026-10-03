public void testRelationalComparisonWithLeftIterator() {
    context.getVariables().declareVariable(
            "relationalLeftIterator",
            java.util.Collections.singletonList(new java.lang.Double(3.0)).iterator());

    assertEquals(java.lang.Boolean.TRUE,
            context.getValue("$relationalLeftIterator > 2"));
}

public void testRelationalComparisonWithRightIterator() {
    context.getVariables().declareVariable(
            "relationalRightIterator",
            java.util.Collections.singletonList(new java.lang.Double(3.0)).iterator());

    assertEquals(java.lang.Boolean.TRUE,
            context.getValue("3 >= $relationalRightIterator"));
}

public void testRelationalComparisonBetweenIteratorsAndNaNIterators() {
    context.getVariables().declareVariable(
            "relationalIteratorLeft",
            java.util.Collections.singletonList(new java.lang.Double(3.0)).iterator());
    context.getVariables().declareVariable(
            "relationalIteratorRight",
            java.util.Collections.singletonList(new java.lang.Double(3.0)).iterator());

    assertEquals(java.lang.Boolean.TRUE,
            context.getValue("$relationalIteratorLeft >= $relationalIteratorRight"));

    context.getVariables().declareVariable(
            "relationalNaNLeft",
            java.util.Collections.singletonList(
                    new java.lang.Double(java.lang.Double.NaN)).iterator());
    context.getVariables().declareVariable(
            "relationalNaNRight",
            java.util.Collections.singletonList(
                    new java.lang.Double(java.lang.Double.NaN)).iterator());

    assertEquals(java.lang.Boolean.FALSE,
            context.getValue("$relationalNaNLeft > $relationalNaNRight"));
}

public void testRelationalExpressionMetadataAndCollectionOperand() {
    org.apache.commons.jxpath.ri.compiler.CoreOperationRelationalExpression operation =
            new org.apache.commons.jxpath.ri.compiler.CoreOperationRelationalExpression(
                    new org.apache.commons.jxpath.ri.compiler.Expression[0]) {
                protected boolean evaluateCompare(int compare) {
                    return false;
                }
            };

    assertEquals(3, operation.getPrecedence());
    assertFalse(operation.isSymmetric());

    context.getVariables().declareVariable(
            "relationalCollection",
            java.util.Collections.singletonList(new java.lang.Double(3.0)));

    assertEquals(java.lang.Boolean.TRUE,
            context.getValue("$relationalCollection > 2"));
}
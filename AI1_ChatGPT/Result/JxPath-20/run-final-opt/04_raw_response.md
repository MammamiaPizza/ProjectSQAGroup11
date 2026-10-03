public void testRelationalExpressionIsNotSymmetric() {
    assertFalse(new RelationalExpressionForTest().isSymmetricForTest());
}

private static final class RelationalExpressionForTest
        extends org.apache.commons.jxpath.ri.compiler.CoreOperationRelationalExpression {
    private RelationalExpressionForTest() {
        super(new org.apache.commons.jxpath.ri.compiler.Expression[0]);
    }

    protected boolean evaluateCompare(int compare) {
        return compare == 0;
    }

    private boolean isSymmetricForTest() {
        return isSymmetric();
    }
}
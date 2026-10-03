@org.junit.Test
public void testBigMatrixVectorConstructorCreatesColumnMatrix() {
    org.apache.commons.math.linear.BigMatrixImpl matrix =
        new org.apache.commons.math.linear.BigMatrixImpl(
            new java.math.BigDecimal[] {
                new java.math.BigDecimal("1.25"),
                new java.math.BigDecimal("-2.5")
            });

    org.junit.Assert.assertEquals(2, matrix.getRowDimension());
    org.junit.Assert.assertEquals(1, matrix.getColumnDimension());
    org.junit.Assert.assertEquals(0,
        new java.math.BigDecimal("1.25").compareTo(matrix.getEntry(0, 0)));
    org.junit.Assert.assertEquals(0,
        new java.math.BigDecimal("-2.5").compareTo(matrix.getEntry(1, 0)));
}

@org.junit.Test
public void testBigMatrixDimensionConstructorRejectsNonPositiveDimensions() {
    try {
        new org.apache.commons.math.linear.BigMatrixImpl(0, 1);
        org.junit.Assert.fail("zero row dimension should be rejected");
    } catch (IllegalArgumentException expected) {
        // expected
    }

    try {
        new org.apache.commons.math.linear.BigMatrixImpl(1, 0);
        org.junit.Assert.fail("zero column dimension should be rejected");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void testRealMatrixDimensionConstructorRejectsNonPositiveDimensions() {
    try {
        new org.apache.commons.math.linear.RealMatrixImpl(-1, 1);
        org.junit.Assert.fail("negative row dimension should be rejected");
    } catch (IllegalArgumentException expected) {
        // expected
    }

    try {
        new org.apache.commons.math.linear.RealMatrixImpl(1, -1);
        org.junit.Assert.fail("negative column dimension should be rejected");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void testRealMatrixCopyDoesNotShareData() {
    org.apache.commons.math.linear.RealMatrixImpl matrix =
        new org.apache.commons.math.linear.RealMatrixImpl(2, 2);
    matrix.setEntry(0, 1, 1.5);

    org.apache.commons.math.linear.RealMatrix copy = matrix.copy();
    matrix.setEntry(0, 1, -4.0);

    org.junit.Assert.assertEquals(1.5, copy.getEntry(0, 1), 0.0);
    org.junit.Assert.assertEquals(-4.0, matrix.getEntry(0, 1), 0.0);
}
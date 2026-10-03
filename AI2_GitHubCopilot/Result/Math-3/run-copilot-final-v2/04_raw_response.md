@Test
public void testConvolve() {
    double[] x = {1.0, 2.0};
    double[] h = {3.0, 4.0};
    double[] expected = {3.0, 10.0, 8.0};
    double[] result = MathArrays.convolve(x, h);
    Assert.assertArrayEquals(expected, result, 1e-15);
}

@Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
public void testCheckNonNegativeLong1DException() {
    MathArrays.checkNonNegative(new long[] {1L, -2L});
}

@Test
public void testBuildArray2D() {
    org.apache.commons.math3.complex.ComplexField field =
org.apache.commons.math3.complex.ComplexField.getInstance();
    org.apache.commons.math3.complex.Complex[][] array = MathArrays.buildArray(field, 2, 3);
    Assert.assertEquals(2, array.length);
    Assert.assertEquals(3, array[0].length);
    Assert.assertEquals(3, array[1].length);
    Assert.assertEquals(org.apache.commons.math3.complex.Complex.ZERO, array[0][0]);
    Assert.assertEquals(org.apache.commons.math3.complex.Complex.ZERO, array[1][2]);
}

@Test(expected = org.apache.commons.math3.exception.NonMonotonicSequenceException.class)
public void testCheckOrderDecreasing() {
    MathArrays.checkOrder(new double[] {3.0, 2.0, 1.0});
}
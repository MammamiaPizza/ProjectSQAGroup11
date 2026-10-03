@org.junit.Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
public void testCheckNonNegativeDetectsNegativeValuesInOneAndTwoDimensions() {
    org.apache.commons.math3.util.MathArrays.checkNonNegative(new long[] {0L, 2L, 5L});
    org.apache.commons.math3.util.MathArrays.checkNonNegative(
        new long[][] {{0L, 1L}, {2L, 3L}});
    org.apache.commons.math3.util.MathArrays.checkNonNegative(
        new long[][] {{0L, 1L}, {2L, -3L}});
}

@org.junit.Test(expected = org.apache.commons.math3.exception.NonMonotonicSequenceException.class)
public void testCheckOrderDefaultRequiresStrictlyIncreasingValues() {
    org.apache.commons.math3.util.MathArrays.checkOrder(new double[] {-1.0, 0.0, 2.0});
    org.apache.commons.math3.util.MathArrays.checkOrder(new double[] {1.0, 1.0});
}

@org.junit.Test(expected = org.apache.commons.math3.exception.NonMonotonicSequenceException.class)
public void testCheckOrderWithDirectionAndNonStrictSetting() {
    org.apache.commons.math3.util.MathArrays.checkOrder(
        new double[] {3.0, 3.0, 1.0},
        org.apache.commons.math3.util.MathArrays.OrderDirection.DECREASING,
        false);
    org.apache.commons.math3.util.MathArrays.checkOrder(
        new double[] {3.0, 4.0},
        org.apache.commons.math3.util.MathArrays.OrderDirection.DECREASING,
        false);
}
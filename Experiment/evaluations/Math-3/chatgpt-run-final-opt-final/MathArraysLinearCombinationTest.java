package org.apache.commons.math3.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathArraysLinearCombinationTest {

    @Test
    public void testLinearCombinationSinglePositiveElement() {
        assertEquals(6.0,
                     MathArrays.linearCombination(new double[] { 2.0 },
                                                  new double[] { 3.0 }),
                     0.0);
    }

    @Test
    public void testLinearCombinationSingleZeroElement() {
        assertEquals(0.0,
                     MathArrays.linearCombination(new double[] { 0.0 },
                                                  new double[] { -7.5 }),
                     0.0);
    }

    @Test
    public void testLinearCombinationSingleNegativeFractionalElement() {
        assertEquals(-0.75,
                     MathArrays.linearCombination(new double[] { -1.5 },
                                                  new double[] { 0.5 }),
                     0.0);
    }

    @Test
    public void testLinearCombinationMultipleElementsComputesDotProduct() {
        assertEquals(12.0,
                     MathArrays.linearCombination(new double[] { 1.0, -2.0, 3.0 },
                                                  new double[] { 4.0, 5.0, 6.0 }),
                     0.0);
    }

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
}

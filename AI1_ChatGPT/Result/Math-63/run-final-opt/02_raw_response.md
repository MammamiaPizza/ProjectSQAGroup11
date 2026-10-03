package org.apache.commons.math.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MathUtilsArrayEqualsRegressionTest {

    @Test
    public void testEqualsReturnsTrueForEqualFiniteArrays() {
        double[] first = { 1.0, -2.5, 3.75 };
        double[] second = { 1.0, -2.5, 3.75 };

        assertTrue(MathUtils.equals(first, second));
        assertTrue(MathUtils.equalsIncludingNaN(first, second));
    }

    @Test
    public void testEqualsReturnsFalseWhenOneElementDiffers() {
        double[] first = { 1.0, 2.0, 3.0 };
        double[] second = { 1.0, 2.0, 4.0 };

        assertFalse(MathUtils.equals(first, second));
        assertFalse(MathUtils.equalsIncludingNaN(first, second));
    }

    @Test
    public void testEqualsReturnsFalseForDifferentLengths() {
        double[] shorter = { 1.0, 2.0 };
        double[] longer = { 1.0, 2.0, 3.0 };

        assertFalse(MathUtils.equals(shorter, longer));
        assertFalse(MathUtils.equalsIncludingNaN(shorter, longer));
    }

    @Test
    public void testEqualsTreatsEmptyArraysAsEqual() {
        double[] first = {};
        double[] second = {};

        assertTrue(MathUtils.equals(first, second));
        assertTrue(MathUtils.equalsIncludingNaN(first, second));
    }

    @Test
    public void testMatchingNaNsAreOnlyEqualWithNaNInclusiveMethod() {
        double[] first = { 1.0, Double.NaN, -3.0 };
        double[] second = { 1.0, Double.NaN, -3.0 };

        assertFalse(MathUtils.equals(first, second));
        assertTrue(MathUtils.equalsIncludingNaN(first, second));
    }

    @Test
    public void testEqualsHandlesSignedZeroAndMatchingInfinities() {
        double[] first = { -0.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
        double[] second = { 0.0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };

        assertTrue(MathUtils.equals(first, second));
        assertTrue(MathUtils.equalsIncludingNaN(first, second));
    }
}
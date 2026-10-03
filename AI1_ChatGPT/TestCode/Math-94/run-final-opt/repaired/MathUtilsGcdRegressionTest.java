package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathUtilsGcdRegressionTest {

    @Test
    public void testGcdRegressionForOverflowingProduct() {
        assertEquals(98304, MathUtils.gcd(393216, 3047424));
    }

    @Test
    public void testGcdOfTypicalPositiveNumbers() {
        assertEquals(6, MathUtils.gcd(54, 24));
    }

    @Test
    public void testGcdWithZero() {
        assertEquals(27, MathUtils.gcd(0, 27));
        assertEquals(27, MathUtils.gcd(27, 0));
        assertEquals(0, MathUtils.gcd(0, 0));
    }

    @Test
    public void testGcdWithNegativeOperands() {
        assertEquals(6, MathUtils.gcd(-54, 24));
        assertEquals(6, MathUtils.gcd(54, -24));
        assertEquals(6, MathUtils.gcd(-54, -24));
    }

    @Test
    public void testGcdOfEqualOperands() {
        assertEquals(17, MathUtils.gcd(17, 17));
        assertEquals(17, MathUtils.gcd(-17, -17));
    }

    @Test
    public void testGcdNearIntegerBoundary() {
        assertEquals(1, MathUtils.gcd(Integer.MAX_VALUE, Integer.MAX_VALUE - 1));
        assertEquals(2, MathUtils.gcd(Integer.MIN_VALUE, 2));
    }

    @Test
    public void testGcdOfMinimumIntegerAndZero() {
        assertEquals(Integer.MIN_VALUE, MathUtils.gcd(Integer.MIN_VALUE, 0));
    }
}

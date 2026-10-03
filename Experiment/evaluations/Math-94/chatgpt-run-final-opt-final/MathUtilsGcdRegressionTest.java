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

@org.junit.Test
public void testAddAndCheckIntBoundariesAndOverflow() {
    org.junit.Assert.assertEquals(Integer.MAX_VALUE,
            MathUtils.addAndCheck(Integer.MAX_VALUE, 0));
    org.junit.Assert.assertEquals(Integer.MIN_VALUE,
            MathUtils.addAndCheck(Integer.MIN_VALUE, 0));

    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }

    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }
}

@org.junit.Test
public void testAddAndCheckLongSignsAndOverflow() {
    org.junit.Assert.assertEquals(7L, MathUtils.addAndCheck(5L, 2L));
    org.junit.Assert.assertEquals(-7L, MathUtils.addAndCheck(-5L, -2L));
    org.junit.Assert.assertEquals(-3L, MathUtils.addAndCheck(-5L, 2L));

    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }

    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }
}

@org.junit.Test
public void testBinomialCoefficientBoundaryAndInvalidArguments() {
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(8, 8));
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(8, 0));
    org.junit.Assert.assertEquals(10L, MathUtils.binomialCoefficient(5, 2));

    try {
        MathUtils.binomialCoefficient(2, 3);
        org.junit.Assert.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }

    try {
        MathUtils.binomialCoefficient(-1, -2);
        org.junit.Assert.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}
}

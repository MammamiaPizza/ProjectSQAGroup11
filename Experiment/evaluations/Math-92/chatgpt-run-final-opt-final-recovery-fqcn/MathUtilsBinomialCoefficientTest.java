package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathUtilsBinomialCoefficientTest {

    @Test
    public void testBinomialCoefficientLargeTriggerValue() {
        assertEquals(27385657281648L, MathUtils.binomialCoefficient(48, 22));
    }

    @Test
    public void testBinomialCoefficientLargeSymmetricValue() {
        assertEquals(27385657281648L, MathUtils.binomialCoefficient(48, 26));
    }

    @Test
    public void testBinomialCoefficientLargestCentralValueThatFitsInLong() {
        assertEquals(7219428434016265740L, MathUtils.binomialCoefficient(66, 33));
    }

    @Test
    public void testBinomialCoefficientKnownModerateValue() {
        assertEquals(2598960L, MathUtils.binomialCoefficient(52, 5));
    }

    @Test
    public void testBinomialCoefficientBoundaryCases() {
        assertEquals(1L, MathUtils.binomialCoefficient(48, 0));
        assertEquals(1L, MathUtils.binomialCoefficient(48, 48));
        assertEquals(48L, MathUtils.binomialCoefficient(48, 1));
        assertEquals(48L, MathUtils.binomialCoefficient(48, 47));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBinomialCoefficientRejectsKGreaterThanN() {
        MathUtils.binomialCoefficient(4, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBinomialCoefficientRejectsNegativeN() {
        MathUtils.binomialCoefficient(-1, 0);
    }

@org.junit.Test
public void testAddAndCheckIntHandlesBoundsAndOverflow() {
    org.junit.Assert.assertEquals(0, MathUtils.addAndCheck(-1, 1));
    org.junit.Assert.assertEquals(Integer.MAX_VALUE, MathUtils.addAndCheck(Integer.MAX_VALUE - 1, 1));
    org.junit.Assert.assertEquals(Integer.MIN_VALUE, MathUtils.addAndCheck(Integer.MIN_VALUE + 1, -1));

    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("expected positive integer overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }

    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("expected negative integer overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }
}

@org.junit.Test
public void testAddAndCheckLongHandlesSignsOrderAndOverflow() {
    org.junit.Assert.assertEquals(12L, MathUtils.addAndCheck(7L, 5L));
    org.junit.Assert.assertEquals(-2L, MathUtils.addAndCheck(5L, -7L));
    org.junit.Assert.assertEquals(-12L, MathUtils.addAndCheck(-5L, -7L));
    org.junit.Assert.assertEquals(-1L, MathUtils.addAndCheck(Long.MIN_VALUE, Long.MAX_VALUE));

    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("expected positive long overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }

    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("expected negative long overflow");
    } catch (ArithmeticException expected) {
        org.junit.Assert.assertEquals("overflow: add", expected.getMessage());
    }
}
}

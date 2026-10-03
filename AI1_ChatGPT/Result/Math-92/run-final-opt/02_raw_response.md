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
}
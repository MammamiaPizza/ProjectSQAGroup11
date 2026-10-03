package org.apache.commons.math.fraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FractionCompareToTest {

    @Test
    public void testCompareToOrdersVeryCloseFractionsNearOne() {
        Fraction larger = new Fraction(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
        Fraction smaller = new Fraction(Integer.MAX_VALUE - 2, Integer.MAX_VALUE - 1);

        assertEquals(1, larger.compareTo(smaller));
        assertEquals(-1, smaller.compareTo(larger));
    }

    @Test
    public void testCompareToOrdersFractionsWithLargeDenominators() {
        Fraction smaller = new Fraction(1, Integer.MAX_VALUE);
        Fraction larger = new Fraction(1, Integer.MAX_VALUE - 1);

        assertEquals(-1, smaller.compareTo(larger));
        assertEquals(1, larger.compareTo(smaller));
    }

    @Test
    public void testCompareToReturnsZeroForEquivalentReducedFractions() {
        Fraction reduced = new Fraction(1, 2);
        Fraction equivalent = new Fraction(2, 4);

        assertEquals(0, reduced.compareTo(equivalent));
        assertEquals(0, equivalent.compareTo(reduced));
    }

    @Test
    public void testCompareToOrdersNegativeZeroAndPositiveFractions() {
        Fraction negative = new Fraction(-1, 3);
        Fraction zero = Fraction.ZERO;
        Fraction positive = new Fraction(1, 3);

        assertTrue(negative.compareTo(zero) < 0);
        assertTrue(zero.compareTo(positive) < 0);
        assertTrue(positive.compareTo(negative) > 0);
    }
}
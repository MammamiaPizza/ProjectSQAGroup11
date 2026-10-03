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

@Test
public void testDoubleConstructorsConvertExactValues() throws Exception {
    Fraction defaultConversion = new Fraction(0.5d);
    Fraction limitedIterationConversion = new Fraction(0.5d, 1.0e-12d, 5);

    org.junit.Assert.assertEquals(1, defaultConversion.getNumerator());
    org.junit.Assert.assertEquals(2, defaultConversion.getDenominator());
    org.junit.Assert.assertEquals(1, limitedIterationConversion.getNumerator());
    org.junit.Assert.assertEquals(2, limitedIterationConversion.getDenominator());
}

@Test
public void testArithmeticOperationsProduceReducedFractions() {
    Fraction left = new Fraction(1, 6);
    Fraction right = new Fraction(1, 4);

    org.junit.Assert.assertEquals(new Fraction(5, 12), left.add(right));
    org.junit.Assert.assertEquals(new Fraction(-1, 12), left.subtract(right));
    org.junit.Assert.assertEquals(new Fraction(1, 24), left.multiply(right));
    org.junit.Assert.assertEquals(new Fraction(2, 3), left.divide(right));
}

@Test
public void testNormalizationAndUnaryOperations() {
    Fraction normalized = new Fraction(2, -4);

    org.junit.Assert.assertEquals(-1, normalized.getNumerator());
    org.junit.Assert.assertEquals(2, normalized.getDenominator());
    org.junit.Assert.assertEquals(new Fraction(1, 2), normalized.abs());
    org.junit.Assert.assertEquals(new Fraction(1, 2), normalized.negate());
    org.junit.Assert.assertEquals(new Fraction(-2, 1), normalized.reciprocal());

    Fraction reducedZero = Fraction.getReducedFraction(0, -7);
    org.junit.Assert.assertEquals(0, reducedZero.getNumerator());
    org.junit.Assert.assertEquals(1, reducedZero.getDenominator());
}

@Test(expected = ArithmeticException.class)
public void testConstructorRejectsZeroDenominator() {
    new Fraction(1, 0);
}
}

package org.apache.commons.math.fraction;

import java.math.BigInteger;

import org.apache.commons.math.exception.ZeroException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BigFractionLargeValueConversionTest {

    @Test
    public void testDoubleValueForLargeNumeratorAndDenominatorNearFive() {
        BigInteger denominator = BigInteger.TEN.pow(400);
        BigInteger numerator = denominator.multiply(BigInteger.valueOf(5)).add(BigInteger.ONE);

        BigFraction fraction = new BigFraction(numerator, denominator);

        assertEquals(Double.NaN, fraction.doubleValue(), 0.0d);
    }

    @Test
    public void testFloatValueForLargeNumeratorAndDenominatorNearFive() {
        BigInteger denominator = BigInteger.TEN.pow(400);
        BigInteger numerator = denominator.multiply(BigInteger.valueOf(5)).add(BigInteger.ONE);

        BigFraction fraction = new BigFraction(numerator, denominator);

        assertEquals(Float.NaN, fraction.floatValue(), 0.0f);
    }

    @Test
    public void testLargeNumeratorAndDenominatorCanProduceFractionBelowOne() {
        BigInteger numerator = BigInteger.TEN.pow(400).add(BigInteger.ONE);
        BigInteger denominator = BigInteger.TEN.pow(400).multiply(BigInteger.valueOf(5));

        BigFraction fraction = new BigFraction(numerator, denominator);

        assertEquals(Double.NaN, fraction.doubleValue(), 0.0d);
        assertEquals(Float.NaN, fraction.floatValue(), 0.0f);
    }

    @Test
    public void testLargeNegativeNumeratorAndDenominatorConversion() {
        BigInteger denominator = BigInteger.TEN.pow(400);
        BigInteger numerator = denominator.multiply(BigInteger.valueOf(5)).add(BigInteger.ONE).negate();

        BigFraction fraction = new BigFraction(numerator, denominator);

        assertEquals(Double.NaN, fraction.doubleValue(), 0.0d);
        assertEquals(Float.NaN, fraction.floatValue(), 0.0f);
    }

    @Test
    public void testFiniteFractionConversion() {
        BigFraction fraction = new BigFraction(7, 2);

        assertEquals(3.5d, fraction.doubleValue(), 0.0d);
        assertEquals(3.5f, fraction.floatValue(), 0.0f);
    }

    @Test(expected = ZeroException.class)
    public void testZeroDenominatorIsRejected() {
        new BigFraction(BigInteger.ONE, BigInteger.ZERO);
    }
}

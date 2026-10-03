package org.apache.commons.math3.fraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.junit.Test;

public class FractionMATH835Test {

    @Test
    public void testLargeCoprimeDenominatorAdditionCancelsOverflowingIntermediates() {
        int numerator = (Integer.MAX_VALUE / 3) - 1;
        Fraction left = new Fraction(numerator, 2);
        Fraction right = new Fraction(-numerator, 3);

        Fraction result = left.add(right);

        assertEquals(numerator, result.getNumerator());
        assertEquals(6, result.getDenominator());
        assertEquals((double) numerator / 6.0, result.doubleValue(), 0.0);
    }

    @Test
    public void testLargeCoprimeDenominatorSubtractionCancelsOverflowingIntermediates() {
        int numerator = (Integer.MAX_VALUE / 3) - 1;
        Fraction left = new Fraction(numerator, 2);
        Fraction right = new Fraction(numerator, 3);

        Fraction result = left.subtract(right);

        assertEquals(numerator, result.getNumerator());
        assertEquals(6, result.getDenominator());
        assertEquals((double) numerator / 6.0, result.doubleValue(), 0.0);
    }

    @Test
    public void testLargeSharedDenominatorAdditionReducesBeforeResultConstruction() {
        Fraction value = new Fraction(Integer.MAX_VALUE, 2);

        Fraction result = value.add(value);

        assertEquals(Integer.MAX_VALUE, result.getNumerator());
        assertEquals(1, result.getDenominator());
        assertEquals((double) Integer.MAX_VALUE, result.doubleValue(), 0.0);
    }

    @Test
    public void testAdditionWithSharedFactorsProducesReducedFraction() {
        Fraction result = new Fraction(1, 6).add(new Fraction(1, 15));

        assertEquals(7, result.getNumerator());
        assertEquals(30, result.getDenominator());
    }

    @Test
    public void testSubtractionOfEqualFractionsReturnsNormalizedZero() {
        Fraction result = new Fraction(17, 31).subtract(new Fraction(17, 31));

        assertEquals(0, result.getNumerator());
        assertEquals(1, result.getDenominator());
        assertEquals(Fraction.ZERO, result);
    }

    @Test
    public void testAdditionRejectsUnrepresentableResult() {
        try {
            new Fraction(Integer.MAX_VALUE, 1).add(Fraction.ONE);
            fail("An unrepresentable fraction result must throw an arithmetic exception");
        } catch (MathArithmeticException expected) {
            assertEquals(MathArithmeticException.class, expected.getClass());
        }
    }

    @Test
    public void testAdditionRejectsNullFraction() {
        try {
            Fraction.ONE.add(null);
            fail("Adding a null fraction must throw a NullArgumentException");
        } catch (NullArgumentException expected) {
            assertEquals(NullArgumentException.class, expected.getClass());
        }
    }
}
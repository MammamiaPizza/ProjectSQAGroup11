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

@org.junit.Test
public void testDoubleConstructorsHandleIntegerAndNegativeContinuedFractionValues() {
    org.apache.commons.math3.fraction.Fraction integer =
        new org.apache.commons.math3.fraction.Fraction(2.0);
    org.junit.Assert.assertEquals(2, integer.getNumerator());
    org.junit.Assert.assertEquals(1, integer.getDenominator());

    org.apache.commons.math3.fraction.Fraction half =
        new org.apache.commons.math3.fraction.Fraction(-0.5, 1.0e-12, 10);
    org.junit.Assert.assertEquals(-1, half.getNumerator());
    org.junit.Assert.assertEquals(2, half.getDenominator());
}

@org.junit.Test(expected = org.apache.commons.math3.fraction.FractionConversionException.class)
public void testDoubleConstructorRejectsValuesAboveIntegerNumeratorRange() {
    new org.apache.commons.math3.fraction.Fraction((double) Integer.MAX_VALUE + 1.0);
}

@org.junit.Test
public void testIntegerArithmeticOverloadsPreserveReducedResults() {
    org.apache.commons.math3.fraction.Fraction value =
        new org.apache.commons.math3.fraction.Fraction(3, 4);

    org.apache.commons.math3.fraction.Fraction added = value.add(2);
    org.junit.Assert.assertEquals(11, added.getNumerator());
    org.junit.Assert.assertEquals(4, added.getDenominator());

    org.apache.commons.math3.fraction.Fraction subtracted = value.subtract(2);
    org.junit.Assert.assertEquals(-5, subtracted.getNumerator());
    org.junit.Assert.assertEquals(4, subtracted.getDenominator());

    org.apache.commons.math3.fraction.Fraction multiplied = value.multiply(4);
    org.junit.Assert.assertEquals(3, multiplied.getNumerator());
    org.junit.Assert.assertEquals(1, multiplied.getDenominator());

    org.apache.commons.math3.fraction.Fraction divided = value.divide(2);
    org.junit.Assert.assertEquals(3, divided.getNumerator());
    org.junit.Assert.assertEquals(8, divided.getDenominator());
}

@org.junit.Test
public void testUnaryAndFractionArithmeticWithNegativeValues() {
    org.apache.commons.math3.fraction.Fraction value =
        new org.apache.commons.math3.fraction.Fraction(-2, 3);

    org.apache.commons.math3.fraction.Fraction absolute = value.abs();
    org.junit.Assert.assertEquals(2, absolute.getNumerator());
    org.junit.Assert.assertEquals(3, absolute.getDenominator());

    org.apache.commons.math3.fraction.Fraction reciprocal = value.reciprocal();
    org.junit.Assert.assertEquals(-3, reciprocal.getNumerator());
    org.junit.Assert.assertEquals(2, reciprocal.getDenominator());

    org.apache.commons.math3.fraction.Fraction product =
        value.multiply(new org.apache.commons.math3.fraction.Fraction(-3, 4));
    org.junit.Assert.assertEquals(1, product.getNumerator());
    org.junit.Assert.assertEquals(2, product.getDenominator());

    org.apache.commons.math3.fraction.Fraction quotient =
        new org.apache.commons.math3.fraction.Fraction(2, 3)
            .divide(new org.apache.commons.math3.fraction.Fraction(-4, 5));
    org.junit.Assert.assertEquals(-5, quotient.getNumerator());
    org.junit.Assert.assertEquals(6, quotient.getDenominator());
}
}

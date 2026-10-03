package org.apache.commons.math3.fraction;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.junit.Test;

public class FractionIntegerOverflowTest {

    @Test(expected = MathArithmeticException.class)
    public void addIntMustRejectNumeratorOverflow() {
        new Fraction(Integer.MAX_VALUE, 1).add(1);
    }

    @Test(expected = MathArithmeticException.class)
    public void subtractIntMustRejectNumeratorOverflow() {
        new Fraction(Integer.MIN_VALUE, 1).subtract(1);
    }

    @Test(expected = MathArithmeticException.class)
    public void multiplyIntMustRejectNumeratorOverflow() {
        new Fraction(Integer.MAX_VALUE, 1).multiply(2);
    }

    @Test(expected = MathArithmeticException.class)
    public void divideIntMustRejectDenominatorOverflow() {
        new Fraction(1, Integer.MAX_VALUE).divide(2);
    }

    @Test(expected = MathArithmeticException.class)
    public void fractionAdditionMustRejectOverflowingResult() {
        new Fraction(Integer.MAX_VALUE, 1).add(Fraction.ONE);
    }

    @Test(expected = MathArithmeticException.class)
    public void divisionByZeroIntMustBeRejected() {
        new Fraction(1, 2).divide(0);
    }

    @Test
    public void representableBoundaryArithmeticProducesExactFraction() {
        Fraction result = new Fraction(Integer.MAX_VALUE, Integer.MAX_VALUE - 1).subtract(1);

        assertEquals(1, result.getNumerator());
        assertEquals(Integer.MAX_VALUE - 1, result.getDenominator());
    }

    @Test
    public void representableOperationNearMinimumValueIsPreserved() {
        Fraction result = new Fraction(Integer.MIN_VALUE, 1).add(1);

        assertEquals(Integer.MIN_VALUE + 1, result.getNumerator());
        assertEquals(1, result.getDenominator());
    }
}
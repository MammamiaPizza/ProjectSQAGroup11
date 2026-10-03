package org.apache.commons.lang3.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class FractionLang22Test {

    @Test
    public void getReducedFractionReducesMinimumIntegerNumeratorByTwo() {
        Fraction fraction = Fraction.getReducedFraction(Integer.MIN_VALUE, 2);

        assertEquals(-1073741824, fraction.getNumerator());
        assertEquals(1, fraction.getDenominator());
    }

    @Test
    public void reduceReducesMinimumIntegerNumeratorByTwo() {
        Fraction fraction = Fraction.getFraction(Integer.MIN_VALUE, 2).reduce();

        assertEquals(-1073741824, fraction.getNumerator());
        assertEquals(1, fraction.getDenominator());
    }

    @Test
    public void getReducedFractionReducesOrdinaryFraction() {
        Fraction fraction = Fraction.getReducedFraction(6, 8);

        assertEquals(3, fraction.getNumerator());
        assertEquals(4, fraction.getDenominator());
    }

    @Test
    public void getReducedFractionNormalizesZeroToZeroConstant() {
        assertSame(Fraction.ZERO, Fraction.getReducedFraction(0, 7));
    }

    @Test(expected = ArithmeticException.class)
    public void getReducedFractionRejectsZeroDenominator() {
        Fraction.getReducedFraction(1, 0);
    }
}

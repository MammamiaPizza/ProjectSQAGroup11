package org.apache.commons.lang.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FractionLang49Test {

    @Test
    public void reduceNormalizesZeroNumeratorToUnitDenominator() {
        Fraction reduced = Fraction.getFraction(0, 100).reduce();

        assertEquals(0, reduced.getNumerator());
        assertEquals(1, reduced.getDenominator());
        assertEquals(Fraction.ZERO, reduced);
    }

    @Test
    public void reduceNormalizesZeroNumeratorAfterNegativeDenominatorIsNormalized() {
        Fraction reduced = Fraction.getFraction(0, -25).reduce();

        assertEquals(0, reduced.getNumerator());
        assertEquals(1, reduced.getDenominator());
    }

    @Test
    public void reduceDividesPositiveFractionByCommonFactor() {
        Fraction reduced = Fraction.getFraction(100, 400).reduce();

        assertEquals(1, reduced.getNumerator());
        assertEquals(4, reduced.getDenominator());
    }

    @Test
    public void reducePreservesSignWhileDividingByCommonFactor() {
        Fraction reduced = Fraction.getFraction(-150, 225).reduce();

        assertEquals(-2, reduced.getNumerator());
        assertEquals(3, reduced.getDenominator());
    }

    @Test
    public void reduceLeavesAlreadyReducedFractionInLowestTerms() {
        Fraction reduced = Fraction.getFraction(7, 13).reduce();

        assertEquals(7, reduced.getNumerator());
        assertEquals(13, reduced.getDenominator());
    }

    @Test(expected = ArithmeticException.class)
    public void getFractionRejectsZeroDenominator() {
        Fraction.getFraction(1, 0);
    }

@Test
public void reduceReducesEqualNonUnitTerms() {
    Fraction reduced = Fraction.getFraction(100, 100).reduce();

    assertEquals(1, reduced.getNumerator());
    assertEquals(1, reduced.getDenominator());
}

@Test
public void absReturnsPositiveFractionForNegativeValue() {
    Fraction absolute = Fraction.getFraction(-3, 7).abs();

    assertEquals(3, absolute.getNumerator());
    assertEquals(7, absolute.getDenominator());
}

@Test
public void addAndSubtractHandleZeroAndCommonDenominatorFactors() {
    Fraction fraction = Fraction.getFraction(3, 7);

    assertEquals(fraction, Fraction.ZERO.add(fraction));
    assertEquals(Fraction.getFraction(-3, 7), Fraction.ZERO.subtract(fraction));
    assertEquals(fraction, fraction.add(Fraction.ZERO));
    assertEquals(fraction, fraction.subtract(Fraction.ZERO));

    Fraction sum = Fraction.getFraction(1, 6).add(Fraction.getFraction(1, 15));
    assertEquals(7, sum.getNumerator());
    assertEquals(30, sum.getDenominator());

    Fraction difference = Fraction.getFraction(1, 6).subtract(Fraction.getFraction(1, 15));
    assertEquals(1, difference.getNumerator());
    assertEquals(10, difference.getDenominator());
}

@Test(expected = ArithmeticException.class)
public void addRejectsIntegerOverflow() {
    Fraction.getFraction(Integer.MAX_VALUE, 1).add(Fraction.ONE);
}
}

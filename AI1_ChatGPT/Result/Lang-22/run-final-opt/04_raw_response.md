@org.junit.Test
public void absReturnsSamePositiveFractionAndNegatesNegativeFraction() {
    org.apache.commons.lang3.math.Fraction positive =
            org.apache.commons.lang3.math.Fraction.getFraction(3, 4);
    org.apache.commons.lang3.math.Fraction negative =
            org.apache.commons.lang3.math.Fraction.getFraction(-3, 4);

    org.junit.Assert.assertSame(positive, positive.abs());
    org.junit.Assert.assertEquals(3, negative.abs().getNumerator());
    org.junit.Assert.assertEquals(4, negative.abs().getDenominator());
}

@org.junit.Test
public void addAndSubtractWithZeroUseIdentityValues() {
    org.apache.commons.lang3.math.Fraction fraction =
            org.apache.commons.lang3.math.Fraction.getFraction(2, 5);

    org.junit.Assert.assertSame(fraction,
            fraction.add(org.apache.commons.lang3.math.Fraction.ZERO));
    org.junit.Assert.assertSame(fraction,
            fraction.subtract(org.apache.commons.lang3.math.Fraction.ZERO));
    org.junit.Assert.assertSame(fraction,
            org.apache.commons.lang3.math.Fraction.ZERO.add(fraction));

    org.apache.commons.lang3.math.Fraction result =
            org.apache.commons.lang3.math.Fraction.ZERO.subtract(fraction);
    org.junit.Assert.assertEquals(-2, result.getNumerator());
    org.junit.Assert.assertEquals(5, result.getDenominator());
}

@org.junit.Test
public void addAndSubtractHandleCoprimeAndSharedDenominators() {
    org.apache.commons.lang3.math.Fraction coprimeSum =
            org.apache.commons.lang3.math.Fraction.getFraction(1, 2)
                    .add(org.apache.commons.lang3.math.Fraction.getFraction(1, 3));
    org.junit.Assert.assertEquals(5, coprimeSum.getNumerator());
    org.junit.Assert.assertEquals(6, coprimeSum.getDenominator());

    org.apache.commons.lang3.math.Fraction sharedDifference =
            org.apache.commons.lang3.math.Fraction.getFraction(1, 6)
                    .subtract(org.apache.commons.lang3.math.Fraction.getFraction(1, 15));
    org.junit.Assert.assertEquals(1, sharedDifference.getNumerator());
    org.junit.Assert.assertEquals(10, sharedDifference.getDenominator());
}
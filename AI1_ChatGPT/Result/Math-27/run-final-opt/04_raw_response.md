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
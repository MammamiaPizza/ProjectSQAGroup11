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
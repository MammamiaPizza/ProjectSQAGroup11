@Test
public void testAbsNegativeFractionReturnsNewInstance() {
    Fraction negative = new Fraction(-5, 12);
    Fraction absResult = negative.abs();
    assertEquals(5, absResult.getNumerator());
    assertEquals(12, absResult.getDenominator());
}

@Test
public void testIntConstructorWithNegativeDenominatorMovesSignToNumerator() {
    Fraction f = new Fraction(4, -9);
    assertEquals(-4, f.getNumerator());
    assertEquals(9, f.getDenominator());
}

@Test
public void testDoubleConstructorWithMaxDenominator() throws FractionConversionException {
    Fraction f = new Fraction(0.75, 16);
    assertEquals(3, f.getNumerator());
    assertEquals(4, f.getDenominator());
}

@Test
public void testDoubleConstructorEpsilonExactIntegerConvergence() {
    Fraction f = new Fraction(7.0, 1.0e-10, 100);
    assertEquals(7, f.getNumerator());
    assertEquals(1, f.getDenominator());
}
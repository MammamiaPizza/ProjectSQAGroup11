@org.junit.Test
public void createBigIntegerParsesSignedHexAlternativeAndOctalPrefixes() {
    org.junit.Assert.assertEquals(new java.math.BigInteger("-16"),
            NumberUtils.createBigInteger("-0x10"));
    org.junit.Assert.assertEquals(new java.math.BigInteger("16"),
            NumberUtils.createBigInteger("#10"));
    org.junit.Assert.assertEquals(new java.math.BigInteger("8"),
            NumberUtils.createBigInteger("010"));
}

@org.junit.Test
public void createBigDecimalPreservesTrailingZeroScale() {
    final java.math.BigDecimal value = new java.math.BigDecimal("123.4500");

    org.junit.Assert.assertEquals(value, NumberUtils.createBigDecimal("123.4500"));
}

@org.junit.Test(expected = java.lang.NumberFormatException.class)
public void createBigDecimalRejectsBlankInput() {
    NumberUtils.createBigDecimal("   ");
}

@org.junit.Test
public void createBigDecimalReturnsNullForDoubleMinusPrefix() {
    org.junit.Assert.assertNull(NumberUtils.createBigDecimal("--1.0"));
}
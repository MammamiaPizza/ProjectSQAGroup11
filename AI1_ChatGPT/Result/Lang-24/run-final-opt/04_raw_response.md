@Test
public void createNumericFactoriesParseValuesAndReturnNullForNullInput() {
    assertNull(NumberUtils.createFloat(null));
    assertNull(NumberUtils.createDouble(null));
    assertNull(NumberUtils.createInteger(null));
    assertNull(NumberUtils.createLong(null));
    assertNull(NumberUtils.createBigInteger(null));
    assertNull(NumberUtils.createBigDecimal(null));

    assertEquals(1.25f, NumberUtils.createFloat("1.25").floatValue(), 0.0f);
    assertEquals(2.5d, NumberUtils.createDouble("2.5").doubleValue(), 0.0d);
    assertEquals(42, NumberUtils.createInteger("0x2A").intValue());
    assertEquals(1234567890123L, NumberUtils.createLong("1234567890123").longValue());
    assertEquals(new java.math.BigInteger("12345678901234567890"),
            NumberUtils.createBigInteger("12345678901234567890"));
    assertEquals(new java.math.BigDecimal("12.50"), NumberUtils.createBigDecimal("12.50"));
}

@Test(expected = NumberFormatException.class)
public void createBigDecimalRejectsBlankInput() {
    NumberUtils.createBigDecimal("   ");
}

@Test
public void isNumberHandlesDecimalExponentAndSuffixForms() {
    assertTrue(NumberUtils.isNumber("0x1f"));
    assertTrue(NumberUtils.isNumber(".5"));
    assertTrue(NumberUtils.isNumber("5."));
    assertTrue(NumberUtils.isNumber("1e2"));
    assertTrue(NumberUtils.isNumber("1.0e-2F"));
    assertTrue(NumberUtils.isNumber("42L"));

    assertFalse(NumberUtils.isNumber("."));
    assertFalse(NumberUtils.isNumber("1e"));
    assertFalse(NumberUtils.isNumber("1e+"));
    assertFalse(NumberUtils.isNumber("1.2.3"));
}
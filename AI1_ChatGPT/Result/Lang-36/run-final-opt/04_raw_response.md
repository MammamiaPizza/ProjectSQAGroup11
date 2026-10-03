@Test
public void testCreateNumberHandlesNullAndDoubleMinusPrefix() {
    assertTrue(NumberUtils.createNumber(null) == null);
    assertTrue(NumberUtils.createNumber("--2") == null);
}

@Test
public void testNumericFactoryMethodsReturnNullForNullInput() {
    assertTrue(NumberUtils.createFloat(null) == null);
    assertTrue(NumberUtils.createDouble(null) == null);
    assertTrue(NumberUtils.createInteger(null) == null);
    assertTrue(NumberUtils.createLong(null) == null);
    assertTrue(NumberUtils.createBigInteger(null) == null);
    assertTrue(NumberUtils.createBigDecimal(null) == null);
}

@Test
public void testCreateNumberAndBigDecimalRejectBlankStrings() {
    boolean createNumberRejected = false;
    try {
        NumberUtils.createNumber(" ");
    } catch (NumberFormatException expected) {
        createNumberRejected = true;
    }
    assertTrue(createNumberRejected);

    boolean createBigDecimalRejected = false;
    try {
        NumberUtils.createBigDecimal("");
    } catch (NumberFormatException expected) {
        createBigDecimalRejected = true;
    }
    assertTrue(createBigDecimalRejected);
}

@Test
public void testCreateNumberCreatesIntegersForHexadecimalValues() {
    assertEquals(16, NumberUtils.createNumber("0x10").intValue());
    assertEquals(-16, NumberUtils.createNumber("-0x10").intValue());
}
@org.junit.Test
public void createNumberAcceptsNegativeUppercaseHexPrefix() {
    final Number number = org.apache.commons.lang3.math.NumberUtils.createNumber("-0Xfade");

    org.junit.Assert.assertNotNull(number);
    org.junit.Assert.assertEquals(-0xfadeL, number.longValue());
}

@org.junit.Test
public void numericFactoryMethodsReturnNullForNullInput() {
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.createFloat(null));
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.createDouble(null));
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.createInteger(null));
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.createLong(null));
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.createBigInteger(null));
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.createBigDecimal(null));
}

@org.junit.Test
public void createBigDecimalRejectsBlankInputAndParsesValidValue() {
    try {
        org.apache.commons.lang3.math.NumberUtils.createBigDecimal(" ");
        org.junit.Assert.fail("Expected NumberFormatException for blank input");
    } catch (final NumberFormatException expected) {
        // expected
    }

    org.junit.Assert.assertEquals(
            new java.math.BigDecimal("123.45"),
            org.apache.commons.lang3.math.NumberUtils.createBigDecimal("123.45"));
}
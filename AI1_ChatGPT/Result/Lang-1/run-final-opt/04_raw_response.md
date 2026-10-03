@org.junit.Test
public void createBigIntegerHandlesSignedHexOctalAndDecimalValues() {
    org.junit.Assert.assertEquals(new java.math.BigInteger("-16"),
            org.apache.commons.lang3.math.NumberUtils.createBigInteger("-0x10"));
    org.junit.Assert.assertEquals(new java.math.BigInteger("63"),
            org.apache.commons.lang3.math.NumberUtils.createBigInteger("077"));
    org.junit.Assert.assertEquals(new java.math.BigInteger("42"),
            org.apache.commons.lang3.math.NumberUtils.createBigInteger("42"));
}

@org.junit.Test
public void toBigDecimalCreatesValueAndReturnsNullForNullInput() {
    org.junit.Assert.assertEquals(new java.math.BigDecimal("12.50"),
            org.apache.commons.lang3.math.NumberUtils.toBigDecimal("12.50"));
    org.junit.Assert.assertNull(org.apache.commons.lang3.math.NumberUtils.toBigDecimal(null));
}

@org.junit.Test(expected = NumberFormatException.class)
public void toBigDecimalRejectsDoubleNegativePrefix() {
    org.apache.commons.lang3.math.NumberUtils.toBigDecimal("--1");
}

@org.junit.Test
public void primitiveConversionsUseDefaultsForInvalidAndNullInput() {
    org.junit.Assert.assertEquals(7,
            org.apache.commons.lang3.math.NumberUtils.toInt("not-a-number", 7));
    org.junit.Assert.assertEquals(9L,
            org.apache.commons.lang3.math.NumberUtils.toLong(null, 9L));
}
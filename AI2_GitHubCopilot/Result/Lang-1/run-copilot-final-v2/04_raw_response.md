@Test
    public void testCreateBigDecimal_ValidNumber() {
        java.math.BigDecimal result = NumberUtils.createBigDecimal("42.5");
        assertNotNull(result);
        assertEquals(0, result.compareTo(new java.math.BigDecimal("42.5")));
    }

 @Test(expected = NumberFormatException.class)
 public void testCreateBigDecimal_DoubleNegativePrefixThrows() {
     NumberUtils.createBigDecimal("--1");
 }

 @Test
 public void testCreateNumber_Octal_07() {
     Number result = NumberUtils.createNumber("07");
     assertNotNull(result);
     assertTrue(result instanceof Integer);
     assertEquals(7, ((Integer) result).intValue());
 }

 @Test(expected = NumberFormatException.class)
 public void testCreateNumber_HexUpperCasePrefix_0X1A_Throws() {
     NumberUtils.createNumber("0X1A");
 }
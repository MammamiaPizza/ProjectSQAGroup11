@Test
    public void testConstructor() {
        new NumberUtils();
    }

 @Test
 public void testCreateBigDecimalNullAndBlank() {
     assertNull(NumberUtils.createBigDecimal(null));
     try {
         NumberUtils.createBigDecimal("");
         fail("Expected NumberFormatException for blank string");
     } catch (NumberFormatException e) {
     }
     try {
         NumberUtils.createBigDecimal("   ");
         fail("Expected NumberFormatException for blank string");
     } catch (NumberFormatException e) {
     }
 }

 @Test
 public void testIsNumberInvalidHexEdgeCases() {
     assertFalse(NumberUtils.isNumber("0x"));
     assertFalse(NumberUtils.isNumber("0x "));
     assertFalse(NumberUtils.isNumber("0xG"));
     assertFalse(NumberUtils.isNumber("0x1G"));
     assertFalse(NumberUtils.isNumber("-0x"));
     assertFalse(NumberUtils.isNumber("0xABCG"));
 }

 @Test
 public void testIsNumberScientificEdgeCases() {
     assertTrue(NumberUtils.isNumber("-0.5E10"));
     assertTrue(NumberUtils.isNumber("+5.0E-10"));
     assertFalse(NumberUtils.isNumber("1.5E"));
     assertFalse(NumberUtils.isNumber("1.5e"));
     assertFalse(NumberUtils.isNumber("E10"));
     assertFalse(NumberUtils.isNumber(".E10"));
     assertFalse(NumberUtils.isNumber("5.E"));
     assertTrue(NumberUtils.isNumber("5.E10"));
 }
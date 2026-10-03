@Test
    public void testEqualsWithSelf() {
        CSVFormat format = CSVFormat.DEFAULT.withIgnoreEmptyLines(true);
        assertTrue(format.equals(format));
    }

 @Test
 public void testEqualsWithDifferentQuoteChar() {
     CSVFormat format1 = CSVFormat.DEFAULT.withQuoteChar('"');
     CSVFormat format2 = CSVFormat.DEFAULT.withQuoteChar('\'');
     assertFalse(format1.equals(format2));
 }

 @Test
 public void testEqualsWithDifferentCommentStart() {
     CSVFormat format1 = CSVFormat.DEFAULT.withCommentStart('#');
     CSVFormat format2 = CSVFormat.DEFAULT;
     assertFalse(format1.equals(format2));
 }

 @Test(expected = IllegalArgumentException.class)
 public void testNewFormatWithLineBreakDelimiter() {
     CSVFormat.newFormat('\n');
 }
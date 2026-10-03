@Test
public void testEqualsSameObject() {
    assertTrue(CSVFormat.RFC4180.equals(CSVFormat.RFC4180));
}

@Test
public void testEqualsNull() {
    assertFalse(CSVFormat.RFC4180.equals(null));
}

@Test
public void testEqualsDifferentClass() {
    assertFalse(CSVFormat.RFC4180.equals("NotACSVFormat"));
}

@Test
public void testEqualsFieldDifferences() {
    CSVFormat base = CSVFormat.RFC4180;
    CSVFormat diffDelim = base.withDelimiter(';');
    assertFalse("Delimiter differ", base.equals(diffDelim));
    CSVFormat diffMode = base.withQuoteMode(QuoteMode.ALL);
    assertFalse("QuoteMode differ", base.equals(diffMode));
    CSVFormat noQuote = CSVFormat.newFormat(base.getDelimiter());
    assertFalse("Quote null vs non-null", base.equals(noQuote));
    CSVFormat diffQuote = base.withQuote(''');
    assertFalse("Quote value differ", base.equals(diffQuote));
    CSVFormat withComment = base.withCommentMarker('#');
    assertFalse("Comment differ", base.equals(withComment));
    CSVFormat withEscape = base.withEscape('\');
    assertFalse("Escape differ", base.equals(withEscape));
    CSVFormat copy = base.withDelimiter(base.getDelimiter());
    assertTrue("Equal after copy", base.equals(copy));
}
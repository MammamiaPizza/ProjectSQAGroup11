@Test
public void parsePositionConsumesNumericDayAndRequiredQuotedLiteral() {
    final FastDateParser parser = new FastDateParser("d'd'",
            java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    final java.text.ParsePosition position = new java.text.ParsePosition(0);

    assertEquals(date(java.util.Calendar.JANUARY, 3), parser.parse("3d", position));
    assertEquals(2, position.getIndex());
}

@Test
public void parsePositionRejectsMissingRequiredQuotedLiteral() {
    final FastDateParser parser = new FastDateParser("d'd'",
            java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    final java.text.ParsePosition position = new java.text.ParsePosition(0);

    assertNull(parser.parse("3", position));
    assertEquals(0, position.getIndex());
    assertEquals(0, position.getErrorIndex());
}

@Test
public void parsesLongMonthNamesUsingLocaleSymbols() throws java.text.ParseException {
    final FastDateParser parser = new FastDateParser("MMMM d",
            java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    final java.util.Calendar calendar = java.util.Calendar.getInstance(
            java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);

    calendar.setTime(parser.parse("January 2"));

    assertEquals(java.util.Calendar.JANUARY, calendar.get(java.util.Calendar.MONTH));
    assertEquals(2, calendar.get(java.util.Calendar.DAY_OF_MONTH));
}

@Test
public void equalsAndHashCodeDependOnPatternTimeZoneAndLocale() {
    final java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
    final FastDateParser parser = new FastDateParser("d", utc, java.util.Locale.US);
    final FastDateParser equalParser = new FastDateParser("d", utc, java.util.Locale.US);

    assertEquals(parser, equalParser);
    assertEquals(parser.hashCode(), equalParser.hashCode());
    org.junit.Assert.assertFalse(parser.equals(new FastDateParser("M", utc, java.util.Locale.US)));
    org.junit.Assert.assertFalse(parser.equals(new FastDateParser("d",
            java.util.TimeZone.getTimeZone("GMT+01:00"), java.util.Locale.US)));
    org.junit.Assert.assertFalse(parser.equals(new FastDateParser("d", utc, java.util.Locale.FRANCE)));
}
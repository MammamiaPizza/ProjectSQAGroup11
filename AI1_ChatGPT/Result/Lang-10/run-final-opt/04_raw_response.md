@Test(expected = java.text.ParseException.class)
public void rejectsMonthAndWeekdayWithoutDayOfMonth() throws java.text.ParseException {
    new org.apache.commons.lang3.time.FastDateParser(
            "M E",
            java.util.TimeZone.getTimeZone("America/Los_Angeles"),
            java.util.Locale.US).parse("3 Tue");
}

@Test
public void parsePositionRejectsMonthAndWeekdayWithoutDayOfMonth() {
    org.apache.commons.lang3.time.FastDateParser parser =
            new org.apache.commons.lang3.time.FastDateParser(
                    "M E",
                    java.util.TimeZone.getTimeZone("America/Los_Angeles"),
                    java.util.Locale.US);

    java.text.ParsePosition position = new java.text.ParsePosition(0);
    org.junit.Assert.assertNull(parser.parse("3 Tue", position));
}

@Test
public void equalityDependsOnPatternTimeZoneAndLocale() {
    org.apache.commons.lang3.time.FastDateParser parser =
            new org.apache.commons.lang3.time.FastDateParser(
                    "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    org.apache.commons.lang3.time.FastDateParser equalParser =
            new org.apache.commons.lang3.time.FastDateParser(
                    "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    org.apache.commons.lang3.time.FastDateParser differentTimeZone =
            new org.apache.commons.lang3.time.FastDateParser(
                    "yyyy-MM-dd", java.util.TimeZone.getTimeZone("GMT+01:00"), java.util.Locale.US);
    org.apache.commons.lang3.time.FastDateParser differentLocale =
            new org.apache.commons.lang3.time.FastDateParser(
                    "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.UK);

    org.junit.Assert.assertEquals(parser, equalParser);
    org.junit.Assert.assertEquals(parser.hashCode(), equalParser.hashCode());
    org.junit.Assert.assertFalse(parser.equals("yyyy-MM-dd"));
    org.junit.Assert.assertFalse(parser.equals(differentTimeZone));
    org.junit.Assert.assertFalse(parser.equals(differentLocale));
}
@org.junit.Test
public void parsesNumericTimestampsIncludingNegativeValues() throws Exception {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat();

    org.junit.Assert.assertEquals(0L, format.parse("0").getTime());
    org.junit.Assert.assertEquals(-1L, format.parse("-1").getTime());
}

@org.junit.Test
public void parsesIsoFractionsShorterThanMilliseconds() throws Exception {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat();

    org.junit.Assert.assertEquals(100L,
            format.parse("1970-01-01T00:00:00.1Z").getTime());
    org.junit.Assert.assertEquals(120L,
            format.parse("1970-01-01T00:00:00.12Z").getTime());
}

@org.junit.Test
public void strictLeniencyRejectsInvalidIsoCalendarDates() throws Exception {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat();
    format.setLenient(false);

    org.junit.Assert.assertFalse(format.isLenient());
    try {
        format.parse("2017-02-30T00:00:00.000Z");
        org.junit.Assert.fail("Expected ParseException for invalid calendar date");
    } catch (java.text.ParseException e) {
        // expected
    }
}

@org.junit.Test
public void iso8601FactoryUsesRequestedTimezoneAndLocale() {
    java.text.DateFormat format =
            com.fasterxml.jackson.databind.util.StdDateFormat.getISO8601Format(
                    java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);

    org.junit.Assert.assertEquals("1970-01-01T00:00:00.000+0000",
            format.format(new java.util.Date(0L)));
}
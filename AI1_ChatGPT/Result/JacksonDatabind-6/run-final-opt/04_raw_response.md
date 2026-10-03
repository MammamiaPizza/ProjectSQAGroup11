@Test
public void parsesIso8601WithoutSecondsUsingZuluTimezone() throws Exception {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat();
    java.util.Date result = format.parse("1997-07-16T19:20Z");

    assertEquals(utcMillis(1997, 7, 16, 19, 20, 0, 0), result.getTime());
}

@Test
public void parsesIso8601WithoutSecondsUsingNegativeColonTimezone() throws Exception {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat();
    java.util.Date result = format.parse("1997-07-16T19:20-05:00");

    assertEquals(utcMillis(1997, 7, 17, 0, 20, 0, 0), result.getTime());
}

@Test
public void cloneKeepsTimezoneAndCanBeChangedIndependently() {
    com.fasterxml.jackson.databind.util.StdDateFormat original =
            new com.fasterxml.jackson.databind.util.StdDateFormat(
                    java.util.TimeZone.getTimeZone("GMT+02:00"));
    com.fasterxml.jackson.databind.util.StdDateFormat copy = original.clone();
    copy.setTimeZone(java.util.TimeZone.getTimeZone("GMT-03:00"));

    assertEquals("1970-01-01T02:00:00.000+0200", original.format(new java.util.Date(0L)));
    assertEquals("1969-12-31T21:00:00.000-0300", copy.format(new java.util.Date(0L)));
}

@Test
public void staticFormatFactoriesHonorTimezoneAndLocale() {
    java.util.TimeZone gmt = java.util.TimeZone.getTimeZone("GMT");

    assertEquals("1970-01-01T00:00:00.000+0000",
            com.fasterxml.jackson.databind.util.StdDateFormat
                    .getISO8601Format(gmt, java.util.Locale.US)
                    .format(new java.util.Date(0L)));
    assertEquals("Thu, 01 Jan 1970 00:00:00 GMT",
            com.fasterxml.jackson.databind.util.StdDateFormat
                    .getRFC1123Format(gmt, java.util.Locale.US)
                    .format(new java.util.Date(0L)));
}
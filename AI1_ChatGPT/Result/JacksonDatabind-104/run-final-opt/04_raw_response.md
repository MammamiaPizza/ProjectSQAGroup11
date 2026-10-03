@org.junit.Test
public void formatsTenThousandYearWithMandatoryPlusSign() {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat()
                    .withTimeZone(java.util.TimeZone.getTimeZone("UTC"));

    java.util.Date value = stdDateFormatCoverageUtcDate(10000, java.util.Calendar.JANUARY, 1, 0, 0, 0, 0);

    org.junit.Assert.assertEquals("+10000-01-01T00:00:00.000+0000", format.format(value));
}

@org.junit.Test
public void formatsAndParsesColonSeparatedTimeZoneOffsets() throws java.text.ParseException {
    java.util.Date value = stdDateFormatCoverageUtcDate(2018, java.util.Calendar.JANUARY, 1, 0, 0, 0, 0);
    com.fasterxml.jackson.databind.util.StdDateFormat offsetFormat =
            new com.fasterxml.jackson.databind.util.StdDateFormat()
                    .withTimeZone(java.util.TimeZone.getTimeZone("GMT+05:30"))
                    .withColonInTimeZone(true);

    String text = "2018-01-01T05:30:00.000+05:30";
    org.junit.Assert.assertEquals(text, offsetFormat.format(value));
    org.junit.Assert.assertEquals(value, offsetFormat.parse(text));

    com.fasterxml.jackson.databind.util.StdDateFormat utcFormat =
            new com.fasterxml.jackson.databind.util.StdDateFormat()
                    .withTimeZone(java.util.TimeZone.getTimeZone("UTC"))
                    .withColonInTimeZone(true);
    org.junit.Assert.assertEquals("2018-01-01T00:00:00.000+00:00", utcFormat.format(value));
}

private static java.util.Date stdDateFormatCoverageUtcDate(int year, int month, int day,
        int hour, int minute, int second, int millisecond) {
    java.util.GregorianCalendar calendar =
            new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
    calendar.clear();
    calendar.set(java.util.Calendar.ERA, java.util.GregorianCalendar.AD);
    calendar.set(year, month, day, hour, minute, second);
    calendar.set(java.util.Calendar.MILLISECOND, millisecond);
    return calendar.getTime();
}
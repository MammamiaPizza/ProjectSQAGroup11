@org.junit.Test
public void getInstanceRejectsNullPattern() {
    try {
        org.apache.commons.lang3.time.FastDateFormat.getInstance((String) null);
        org.junit.Assert.fail("Expected IllegalArgumentException for a null pattern");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void formatObjectLongAppendsUsingFormatterTimezone() {
    org.apache.commons.lang3.time.FastDateFormat format =
            org.apache.commons.lang3.time.FastDateFormat.getInstance(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    java.util.TimeZone.getTimeZone("UTC"));
    StringBuffer buffer = new StringBuffer("prefix:");
    StringBuffer returned = format.format(
            (Object) Long.valueOf(0L), buffer, new java.text.FieldPosition(0));

    org.junit.Assert.assertSame(buffer, returned);
    org.junit.Assert.assertEquals("prefix:1970-01-01T00:00:00.000Z", buffer.toString());
}

@org.junit.Test
public void dateInstanceUsesRequestedLocaleAndTimezone() {
    java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
    java.util.Locale locale = java.util.Locale.US;
    org.apache.commons.lang3.time.FastDateFormat format =
            org.apache.commons.lang3.time.FastDateFormat.getDateInstance(
                    org.apache.commons.lang3.time.FastDateFormat.SHORT, utc, locale);
    java.text.DateFormat expected = java.text.DateFormat.getDateInstance(
            java.text.DateFormat.SHORT, locale);
    expected.setTimeZone(utc);

    org.junit.Assert.assertEquals(expected.format(new java.util.Date(0L)),
            format.format(new java.util.Date(0L)));
}
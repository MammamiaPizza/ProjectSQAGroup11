@org.junit.Test
public void testNullPatternIsRejected() {
    try {
        org.apache.commons.lang.time.FastDateFormat.getInstance((String) null);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
        org.junit.Assert.assertEquals("The pattern must not be null", ex.getMessage());
    }
}

@org.junit.Test
public void testDefaultArgumentsUseCurrentDefaultsAndRemainDistinctFromForcedDefaults() {
    java.util.TimeZone defaultTimeZone = java.util.TimeZone.getDefault();
    java.util.Locale defaultLocale = java.util.Locale.getDefault();
    org.apache.commons.lang.time.FastDateFormat implicit =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "MMMM yyyy", (java.util.TimeZone) null, (java.util.Locale) null);
    org.apache.commons.lang.time.FastDateFormat explicit =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "MMMM yyyy", defaultTimeZone, defaultLocale);

    org.junit.Assert.assertEquals(defaultTimeZone, implicit.getTimeZone());
    org.junit.Assert.assertEquals(
        implicit.format(new java.util.Date(0L)),
        explicit.format(new java.util.Date(0L)));
    org.junit.Assert.assertTrue(implicit.equals(implicit));
    org.junit.Assert.assertFalse(implicit.equals(explicit));
    org.junit.Assert.assertFalse(implicit.equals("MMMM yyyy"));
}

@org.junit.Test
public void testFormatObjectSupportsLongAndRejectsUnknownObjects() {
    org.apache.commons.lang.time.FastDateFormat format =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    StringBuffer buffer = new StringBuffer();

    format.format((Object) Long.valueOf(0L), buffer, new java.text.FieldPosition(0));
    org.junit.Assert.assertEquals("1970-01-01", buffer.toString());

    try {
        format.format((Object) new Object(), new StringBuffer(), new java.text.FieldPosition(0));
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
        org.junit.Assert.assertEquals("Unknown class: java.lang.Object", ex.getMessage());
    }
}

@org.junit.Test
public void testFormattingCalendarWithForcedTimeZoneDoesNotMutateCalendar() {
    java.util.TimeZone calendarTimeZone = java.util.TimeZone.getTimeZone("GMT+05:30");
    java.util.Calendar calendar = java.util.Calendar.getInstance(calendarTimeZone, java.util.Locale.US);
    calendar.setTimeInMillis(0L);
    org.apache.commons.lang.time.FastDateFormat format =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "yyyy-MM-dd HH:mm Z", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);

    org.junit.Assert.assertEquals("1970-01-01 00:00 +0000", format.format(calendar));
    org.junit.Assert.assertEquals(calendarTimeZone, calendar.getTimeZone());
}
@Test(expected = IllegalArgumentException.class)
public void rejectsNullPattern() {
    org.apache.commons.lang3.time.FastDateFormat.getInstance((String) null);
}

@Test
public void formatsSupportedObjectTypesThroughFormatApi() {
    org.apache.commons.lang3.time.FastDateFormat format =
            org.apache.commons.lang3.time.FastDateFormat.getInstance(
                    "yyyy-MM-dd",
                    java.util.TimeZone.getTimeZone("UTC"),
                    java.util.Locale.US);

    assertEquals("x1970-01-01", format.format(
            new java.util.Date(0L),
            new java.lang.StringBuffer("x")).toString());

    java.util.Calendar calendar =
            new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    calendar.setTimeInMillis(0L);
    assertEquals("x1970-01-01", format.format(
            (Object) calendar,
            new java.lang.StringBuffer("x"),
            new java.text.FieldPosition(0)).toString());

    assertEquals("x1970-01-01", format.format(
            (Object) java.lang.Long.valueOf(0L),
            new java.lang.StringBuffer("x"),
            new java.text.FieldPosition(0)).toString());
}

@Test
public void calendarFormattingUsesForcedTimezoneButRetainsCalendarTimezoneWhenUnforced() {
    java.util.Calendar calendar =
            new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("GMT+02:00"), java.util.Locale.US);
    calendar.setTimeInMillis(0L);

    org.apache.commons.lang3.time.FastDateFormat forced =
            org.apache.commons.lang3.time.FastDateFormat.getInstance(
                    "HH",
                    java.util.TimeZone.getTimeZone("UTC"),
                    java.util.Locale.US);
    assertEquals("00", forced.format(calendar, new java.lang.StringBuffer()).toString());

    java.util.Calendar utcCalendar =
            new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    utcCalendar.setTimeInMillis(0L);
    org.apache.commons.lang3.time.FastDateFormat unforced =
            org.apache.commons.lang3.time.FastDateFormat.getInstance(
                    "HH",
                    (java.util.TimeZone) null,
                    java.util.Locale.US);
    assertEquals("00", unforced.format(utcCalendar, new java.lang.StringBuffer()).toString());
}

@Test
public void equalsRejectsDifferentFormatConfigurationAndOtherTypes() {
    java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
    java.util.Locale defaultLocale = java.util.Locale.getDefault();

    org.apache.commons.lang3.time.FastDateFormat base =
            org.apache.commons.lang3.time.FastDateFormat.getInstance("yyyy", utc, java.util.Locale.US);

    assertEquals(java.lang.Boolean.FALSE, java.lang.Boolean.valueOf(base.equals("yyyy")));
    assertEquals(java.lang.Boolean.FALSE, java.lang.Boolean.valueOf(base.equals(
            org.apache.commons.lang3.time.FastDateFormat.getInstance("MM", utc, java.util.Locale.US))));
    assertEquals(java.lang.Boolean.FALSE, java.lang.Boolean.valueOf(base.equals(
            org.apache.commons.lang3.time.FastDateFormat.getInstance(
                    "yyyy", java.util.TimeZone.getTimeZone("GMT+01:00"), java.util.Locale.US))));
    assertEquals(java.lang.Boolean.FALSE, java.lang.Boolean.valueOf(base.equals(
            org.apache.commons.lang3.time.FastDateFormat.getInstance("yyyy", utc, java.util.Locale.FRANCE))));
    assertEquals(java.lang.Boolean.FALSE, java.lang.Boolean.valueOf(
            org.apache.commons.lang3.time.FastDateFormat.getInstance("yyyy", utc).equals(
                    org.apache.commons.lang3.time.FastDateFormat.getInstance("yyyy", utc, defaultLocale))));
}
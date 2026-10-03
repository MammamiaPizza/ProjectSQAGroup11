@Test
public void timeInstanceUsesCurrentDefaultLocaleAfterDefaultChanges() {
    java.util.Locale originalLocale = java.util.Locale.getDefault();
    try {
        java.util.Locale.setDefault(java.util.Locale.US);
        FastDateFormat usFormat = FastDateFormat.getTimeInstance(FastDateFormat.FULL);
        Assert.assertSame(java.util.Locale.US, usFormat.getLocale());

        java.util.Locale.setDefault(java.util.Locale.GERMANY);
        FastDateFormat germanFormat = FastDateFormat.getTimeInstance(FastDateFormat.FULL);
        Assert.assertSame(java.util.Locale.GERMANY, germanFormat.getLocale());
    } finally {
        java.util.Locale.setDefault(originalLocale);
    }
}

@Test
public void formatObjectSupportsDateCalendarAndLong() {
    FastDateFormat format = FastDateFormat.getInstance(
            "yyyy-MM-dd HH:mm", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);

    StringBuffer dateBuffer = new StringBuffer();
    Assert.assertSame(dateBuffer, format.format(
            (Object) new java.util.Date(0L), dateBuffer, new java.text.FieldPosition(0)));
    Assert.assertEquals("1970-01-01 00:00", dateBuffer.toString());

    java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
    calendar.setTimeInMillis(0L);
    StringBuffer calendarBuffer = new StringBuffer();
    Assert.assertSame(calendarBuffer, format.format(
            (Object) calendar, calendarBuffer, new java.text.FieldPosition(0)));
    Assert.assertEquals("1970-01-01 00:00", calendarBuffer.toString());

    StringBuffer longBuffer = new StringBuffer();
    Assert.assertSame(longBuffer, format.format(
            (Object) Long.valueOf(0L), longBuffer, new java.text.FieldPosition(0)));
    Assert.assertEquals("1970-01-01 00:00", longBuffer.toString());
}

@Test
public void equalsDistinguishesNonFormatsAndDifferentLocales() {
    FastDateFormat usFormat = FastDateFormat.getInstance(
            "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    FastDateFormat sameFormat = FastDateFormat.getInstance(
            "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    FastDateFormat frenchFormat = FastDateFormat.getInstance(
            "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.FRANCE);

    Assert.assertTrue(usFormat.equals(sameFormat));
    Assert.assertFalse(usFormat.equals(frenchFormat));
    Assert.assertFalse(usFormat.equals("yyyy-MM-dd"));
}

@Test
public void nullTimeZoneUsesTheCurrentDefaultTimeZone() {
    java.util.TimeZone originalTimeZone = java.util.TimeZone.getDefault();
    try {
        java.util.TimeZone expectedTimeZone = java.util.TimeZone.getTimeZone("GMT+13:00");
        java.util.TimeZone.setDefault(expectedTimeZone);

        FastDateFormat format = FastDateFormat.getInstance(
                "yyyy-MM-dd 'null-time-zone'", null, java.util.Locale.US);

        Assert.assertEquals(expectedTimeZone, format.getTimeZone());
    } finally {
        java.util.TimeZone.setDefault(originalTimeZone);
    }
}
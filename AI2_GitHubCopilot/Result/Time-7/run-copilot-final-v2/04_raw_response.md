@Test
public void testGettersAndSetters() {
    DateTimeFormatter f = org.joda.time.format.ISODateTimeFormat.date()
            .withChronology(org.joda.time.chrono.ISOChronology.getInstanceUTC());
    assertNotNull(f.getPrinter());
    assertNotNull(f.getParser());
    assertTrue(f.isPrinter());
    assertTrue(f.isParser());
    DateTimeFormatter f2 = f.withLocale(java.util.Locale.FRENCH);
    assertEquals(java.util.Locale.FRENCH, f2.getLocale());
    DateTimeFormatter f3 = f2.withOffsetParsed();
    assertTrue(f3.isOffsetParsed());
    org.joda.time.DateTimeZone zone = org.joda.time.DateTimeZone.forID("Europe/London");
    DateTimeFormatter f4 = f3.withZone(zone);
    assertEquals(zone, f4.getZone());
    assertEquals(org.joda.time.chrono.ISOChronology.getInstanceUTC(), f.getChronology());
    DateTimeFormatter f5 = f4.withPivotYear(2000);
    assertEquals(Integer.valueOf(2000), f5.getPivotYear());
    DateTimeFormatter f6 = f5.withDefaultYear(2020);
    assertEquals(2020, f6.getDefaultYear());
}

@Test
public void testParseDateTimeLeapYear() {
    DateTimeFormatter parser = org.joda.time.format.ISODateTimeFormat.date()
            .withZone(org.joda.time.DateTimeZone.UTC);
    org.joda.time.DateTime result = parser.parseDateTime("2020-02-29");
    assertNotNull(result);
    assertEquals(2020, result.getYear());
    assertEquals(2, result.getMonthOfYear());
    assertEquals(29, result.getDayOfMonth());
    assertEquals(0, result.getMillisOfDay());
}

@Test
public void testParseMonthDayFeb29LeapYear() {
    DateTimeFormatter monthDayFmt = org.joda.time.format.ISODateTimeFormat.monthDay()
            .withZoneUTC();
    org.joda.time.MutableDateTime instant = new org.joda.time.MutableDateTime(2020, 1, 1, 0, 0, 0,
0);
    int pos = monthDayFmt.parseInto(instant, "--02-29", 0);
    assertTrue(pos >= 0);
    assertEquals(2, instant.getMonthOfYear());
    assertEquals(29, instant.getDayOfMonth());
}

@Test
public void testDeprecatedGetChronolgy() {
    DateTimeFormatter f = org.joda.time.format.ISODateTimeFormat.date()
            .withChronology(org.joda.time.chrono.ISOChronology.getInstanceUTC());
    assertNotNull(f.getChronolgy());
}
@Test
public void testDefaultLeniencyParsesRfc1123AndOffsetIso8601() throws Exception {
    StdDateFormat rfcFormat = new StdDateFormat();
    assertTrue(rfcFormat.isLenient());
    assertEquals(new java.util.Date(1420167845000L),
            rfcFormat.parse("Fri, 02 Jan 2015 03:04:05 GMT"));

    StdDateFormat isoFormat = new StdDateFormat();
    assertEquals(new java.util.Date(1420167845000L),
            isoFormat.parse("2015-01-02T05:04:05+0200"));
}

@Test
public void testSetTimeZoneClearsCachedPlainDateFormat() throws Exception {
    java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
    java.util.TimeZone plusTwo = java.util.TimeZone.getTimeZone("GMT+02:00");
    StdDateFormat format = new StdDateFormat().withTimeZone(utc);

    assertEquals(new java.util.Date(1420156800000L), format.parse("2015-01-02"));

    format.setTimeZone(plusTwo);
    assertEquals(plusTwo, format.getTimeZone());
    assertEquals(new java.util.Date(1420149600000L), format.parse("2015-01-02"));
}

@Test
public void testCloneRetainsExplicitTimeZoneAndLeniency() throws Exception {
    java.util.TimeZone plusTwo = java.util.TimeZone.getTimeZone("GMT+02:00");
    StdDateFormat format = new StdDateFormat().withTimeZone(plusTwo);
    format.setLenient(false);
    format.parse("2015-01-02");

    StdDateFormat clone = format.clone();

    assertNotSame(format, clone);
    assertEquals(plusTwo, clone.getTimeZone());
    assertFalse(clone.isLenient());
    assertEquals(new java.util.Date(1420149600000L), clone.parse("2015-01-02"));
}

@Test
public void testStaticFormatFactoriesUseRequestedTimeZoneAndLocale() throws Exception {
    java.util.TimeZone plusTwo = java.util.TimeZone.getTimeZone("GMT+02:00");
    java.util.Date value = new java.util.Date(1420167845000L);

    java.text.DateFormat isoFormat = StdDateFormat.getISO8601Format(plusTwo);
    assertEquals(plusTwo, isoFormat.getTimeZone());
    assertEquals(value, isoFormat.parse(isoFormat.format(value)));

    java.text.DateFormat rfcFormat = StdDateFormat.getRFC1123Format(plusTwo,
            java.util.Locale.FRANCE);
    assertEquals(plusTwo, rfcFormat.getTimeZone());
    assertEquals(value, rfcFormat.parse(rfcFormat.format(value)));
}
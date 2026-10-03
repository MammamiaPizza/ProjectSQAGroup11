public void testAddWeekyears_int_dstOverlapWinter_addZero() {
    org.joda.time.MutableDateTime dateTime = new org.joda.time.MutableDateTime(
            1319938200000L, org.joda.time.DateTimeZone.forID("Europe/Paris"));

    dateTime.addWeekyears(0);

    assertEquals("2011-10-30T02:30:00.000+01:00", dateTime.toString());
}

public void testAddLongUpdatesMillisAndDetectsOverflow() {
    org.joda.time.MutableDateTime dateTime = new org.joda.time.MutableDateTime(0L);

    dateTime.add(1234L);
    assertEquals(1234L, dateTime.getMillis());

    dateTime.setMillis(Long.MAX_VALUE);
    try {
        dateTime.add(1L);
        fail("Expected ArithmeticException");
    } catch (ArithmeticException ex) {
    }
}

public void testComponentConstructorWithChronologyUsesSuppliedChronology() {
    org.joda.time.MutableDateTime dateTime = new org.joda.time.MutableDateTime(
            2012, 2, 29, 23, 59, 58, 7,
            org.joda.time.chrono.ISOChronology.getInstanceUTC());

    assertEquals("2012-02-29T23:59:58.007Z", dateTime.toString());
}
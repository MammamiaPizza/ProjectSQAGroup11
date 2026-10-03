public void testAddLong_zero_dstOverlapWinter() {
    org.joda.time.DateTimeZone berlin = org.joda.time.DateTimeZone.forID("Europe/Berlin");
    long millisBefore = new org.joda.time.MutableDateTime(2021, 10, 31, 2, 30, 0, 0,
berlin).getMillis();
    org.joda.time.MutableDateTime mdt = new org.joda.time.MutableDateTime(millisBefore, berlin);
    long offsetBefore = berlin.getOffset(millisBefore);
    assertEquals("offset should be +01:00", 3600000, offsetBefore);
    mdt.add(0L);
    assertEquals("millis unchanged", millisBefore, mdt.getMillis());
    assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
}

public void testAddDurationFieldType_null() {
    org.joda.time.MutableDateTime mdt = new org.joda.time.MutableDateTime();
    try {
        mdt.add((org.joda.time.DurationFieldType) null, 0);
        fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
        // expected
    }
}

public void testConstructor_Chronology() {
    org.joda.time.Chronology chrono = org.joda.time.chrono.ISOChronology.getInstanceUTC();
    org.joda.time.MutableDateTime mdt = new org.joda.time.MutableDateTime(chrono);
    assertEquals(chrono, mdt.getChronology());
}

public void testConstructor_noArg() {
    org.joda.time.MutableDateTime mdt = new org.joda.time.MutableDateTime();
    assertTrue(mdt != null);
    assertEquals(org.joda.time.DateTimeZone.getDefault(), mdt.getZone());
}
@Test
public void testConvertLocalToUTC_Strict_Gap() {
    DateTimeZone zone = DateTimeZone.forID("America/New_York");
    // 2023-03-12 02:30:00 local time falls in the spring-forward gap
    long localMillis = new DateTime(2023, 3, 12, 2, 30, 0, 0, DateTimeZone.UTC).getMillis();
    try {
        zone.convertLocalToUTC(localMillis, true);
        fail("Expected IllegalArgumentException for local time in DST gap");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@Test
public void testConvertLocalToUTC_NonStrict_Gap() {
    DateTimeZone zone = DateTimeZone.forID("America/New_York");
    long localMillis = new DateTime(2023, 3, 12, 2, 30, 0, 0, DateTimeZone.UTC).getMillis();
    long utcMillis = zone.convertLocalToUTC(localMillis, false);
    assertNotNull(zone);
    // The returned UTC millis should not equal the local millis because an offset is applied
    assertNotEquals(localMillis, utcMillis);
}

@Test
public void testAdjustOffset_FixedZone() {
    long instant = 123456789L;
    assertEquals(instant, DateTimeZone.UTC.adjustOffset(instant, true));
    assertEquals(instant, DateTimeZone.UTC.adjustOffset(instant, false));
}

@Test
public void testForID_OldID_MET() {
    DateTimeZone zone = DateTimeZone.forID("MET");
    assertNotNull(zone);
    assertEquals("MET", zone.getID());
}
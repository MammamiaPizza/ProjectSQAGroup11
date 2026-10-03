@Test(expected = IllegalArgumentException.class)
public void convertLocalToUtcRejectsLondonSpringGapInStrictMode() {
    DateTimeZone.forID("Europe/London").convertLocalToUTC(1301189400000L, true);
}

@Test
public void adjustOffsetSelectsBothOccurrencesDuringLondonOverlap() {
    DateTimeZone zone = DateTimeZone.forID("Europe/London");
    long laterOccurrence = 1319938200000L;

    long earlier = zone.adjustOffset(laterOccurrence, false);
    long later = zone.adjustOffset(laterOccurrence, true);

    assertTrue(earlier < later);
    assertEquals(zone.convertUTCToLocal(earlier), zone.convertUTCToLocal(later));
}

@Test
public void adjustOffsetLeavesFixedZoneInstantUnchanged() {
    assertEquals(123456789L, DateTimeZone.UTC.adjustOffset(123456789L, false));
    assertEquals(123456789L, DateTimeZone.UTC.adjustOffset(123456789L, true));
}

@Test
public void forTimeZoneCreatesFixedZoneForGmtOffset() {
    DateTimeZone zone = DateTimeZone.forTimeZone(java.util.TimeZone.getTimeZone("GMT+02:00"));

    assertTrue(zone.isFixed());
    assertEquals(7200000L, zone.convertUTCToLocal(0L));
}
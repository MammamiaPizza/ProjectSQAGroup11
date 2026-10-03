public void testForOffsetHoursMinutes_zeroHoursNegativeMinutes() {
    DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, -30);
    assertEquals(-30 * 60 * 1000, zone.getOffset(0L));
}

public void testForOffsetHoursMinutes_negativeHoursPositiveMinutesRejected() {
    assertIllegalArgumentForOffset(-1, 1);
}
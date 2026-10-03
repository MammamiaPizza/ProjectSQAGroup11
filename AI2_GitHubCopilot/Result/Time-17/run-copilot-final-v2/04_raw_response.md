@Test(expected = IllegalArgumentException.class)
    public void testConvertLocalToUTCGapStrictThrows() {
        DateTimeZone zone = DateTimeZone.forID("America/New_York");
        long localGap = new DateTime(2012, 3, 11, 2, 30, 0, 0, DateTimeZone.UTC).getMillis();
        zone.convertLocalToUTC(localGap, true);
    }

 @Test
 public void testConvertLocalToUTCGapNonStrictReturnsLaterOffset() {
     DateTimeZone zone = DateTimeZone.forID("America/New_York");
     long localGap = new DateTime(2012, 3, 11, 2, 30, 0, 0, DateTimeZone.UTC).getMillis();
     long expectedUtc = localGap + 4 * MILLIS_PER_HOUR;
     assertEquals(expectedUtc, zone.convertLocalToUTC(localGap, false));
 }

 @Test(expected = ArithmeticException.class)
 public void testConvertLocalToUTCOverflow() {
     DateTimeZone zone = DateTimeZone.forOffsetHours(-14);
     long localInstant = Long.MAX_VALUE - 1;
     zone.convertLocalToUTC(localInstant, false);
 }

 @Test
 public void testConvertLocalToUTCOverlapWithOriginalUtc() {
     DateTimeZone zone = DateTimeZone.forID("America/New_York");
     long localOverlap = new DateTime(2012, 11, 4, 1, 30, 0, 0, DateTimeZone.UTC).getMillis();
     long refUtc1 = localOverlap + 4 * MILLIS_PER_HOUR - MILLIS_PER_HOUR;
     long expectedUtc1 = localOverlap + 4 * MILLIS_PER_HOUR;
     assertEquals(expectedUtc1, zone.convertLocalToUTC(localOverlap, false, refUtc1));
     long refUtc2 = localOverlap + 5 * MILLIS_PER_HOUR + MILLIS_PER_HOUR;
     long expectedUtc2 = localOverlap + 5 * MILLIS_PER_HOUR;
     assertEquals(expectedUtc2, zone.convertLocalToUTC(localOverlap, false, refUtc2));
 }
@org.junit.Test
    public void convertLocalToUTCStrictSpringGapThrowsIllegalArgumentException() {
        org.joda.time.DateTimeZone moscow = org.joda.time.DateTimeZone.forID("Europe/Moscow");
        long gapLocal = new org.joda.time.LocalDateTime(2007, 3, 25, 2, 30)
                .toDateTime(org.joda.time.DateTimeZone.UTC).getMillis();
        try {
            moscow.convertLocalToUTC(gapLocal, true);
            org.junit.Assert.fail("Expected IllegalArgumentException for strict conversion of a
spring DST gap");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

 @org.junit.Test
 public void convertLocalToUTCSpringGapNonStrictUsesLocalOffset() {
     org.joda.time.DateTimeZone moscow = org.joda.time.DateTimeZone.forID("Europe/Moscow");
     long gapLocal = new org.joda.time.LocalDateTime(2007, 3, 25, 2, 30)
             .toDateTime(org.joda.time.DateTimeZone.UTC).getMillis();
     long expected = gapLocal - moscow.getOffset(gapLocal);
     org.junit.Assert.assertEquals("Non-strict gap conversion should use the local offset",
             expected, moscow.convertLocalToUTC(gapLocal, false));
 }

 @org.junit.Test
 public void convertLocalToUTCOverloadUsesOriginalSummerOffsetForAutumnOverlap() {
     org.joda.time.DateTimeZone moscow = org.joda.time.DateTimeZone.forID("Europe/Moscow");
     long local = new org.joda.time.LocalDateTime(2007, 10, 28, 2, 30)
             .toDateTime(org.joda.time.DateTimeZone.UTC).getMillis();
     long originalUtc = new org.joda.time.LocalDateTime(2007, 10, 27, 12, 0)
             .toDateTime(org.joda.time.DateTimeZone.UTC).getMillis();
     long summerOffset = 4L * 60 * 60 * 1000;
     org.junit.Assert.assertEquals("Original summer offset should be preserved during autumn
overlap",
             local - summerOffset, moscow.convertLocalToUTC(local, false, originalUtc));
 }

 @org.junit.Test
 public void convertLocalToUTCOverloadUsesOriginalWinterOffsetForAutumnOverlap() {
     org.joda.time.DateTimeZone moscow = org.joda.time.DateTimeZone.forID("Europe/Moscow");
     long local = new org.joda.time.LocalDateTime(2007, 10, 28, 2, 30)
             .toDateTime(org.joda.time.DateTimeZone.UTC).getMillis();
     long originalUtc = new org.joda.time.LocalDateTime(2007, 10, 28, 12, 0)
             .toDateTime(org.joda.time.DateTimeZone.UTC).getMillis();
     long winterOffset = 3L * 60 * 60 * 1000;
     org.junit.Assert.assertEquals("Original winter offset should be preserved during autumn
overlap",
             local - winterOffset, moscow.convertLocalToUTC(local, false, originalUtc));
 }
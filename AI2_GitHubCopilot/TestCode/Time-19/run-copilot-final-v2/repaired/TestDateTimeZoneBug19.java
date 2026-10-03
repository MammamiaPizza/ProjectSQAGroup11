package org.joda.time;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class TestDateTimeZoneBug19 {

     private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
     private static final DateTimeZone UTC = DateTimeZone.UTC;

     /**
      * Returns the UTC millis for the given fields in the ISO chronology at UTC.
      */
     private static long utcMillis(int year, int month, int day, int hour, int min, int sec, int
millis) {
         return new DateTime(year, month, day, hour, min, sec, millis, UTC).getMillis();
     }

     /**
      * Constructs a local millis value from a UTC instant and an offset.
      */
     private static long localMillis(long utcInstant, int offsetMillis) {
         return utcInstant + offsetMillis;
     }

     /**
      * Simulates the original failing test: create a DateTime in a gap and
      * check its UTC instant and offset.
      */
     @Test
     public void testDateTimeCreation_LondonGap_1891() {
         long utcExpected = utcMillis(2010, 3, 28, 0, 15, 0, 0);
         DateTime dt = new DateTime(2010, 3, 28, 1, 15, 0, 0, LONDON);
         assertEquals("UTC millis should correspond to 00:15 UTC", utcExpected, dt.getMillis());
         assertEquals("Offset should be +01:00 (3600000 ms)", DateTimeConstants.MILLIS_PER_HOUR,
                 dt.getZone().getOffset(dt.getMillis()));
     }

     /** Gap middle: local 01:15 → offset +01:00 */
     @Test
     public void testGetOffsetFromLocal_GapMiddle_01_15() {
         long utc = utcMillis(2010, 3, 28, 0, 15, 0, 0);
         long local = localMillis(utc, DateTimeConstants.MILLIS_PER_HOUR);
         assertEquals(DateTimeConstants.MILLIS_PER_HOUR, LONDON.getOffsetFromLocal(local));
     }

     /** Gap start: local 01:00:00.000 → offset +01:00 */
     @Test
     public void testGetOffsetFromLocal_GapStart_01_00() {
         long utc = utcMillis(2010, 3, 28, 0, 0, 0, 0);
         long local = localMillis(utc, DateTimeConstants.MILLIS_PER_HOUR);
         assertEquals(DateTimeConstants.MILLIS_PER_HOUR, LONDON.getOffsetFromLocal(local));
     }

     /** Gap end: local 02:00:00.000 → offset +01:00 */
     @Test
     public void testGetOffsetFromLocal_GapEnd_02_00() {
         long utc = utcMillis(2010, 3, 28, 1, 0, 0, 0);
         long local = localMillis(utc, DateTimeConstants.MILLIS_PER_HOUR);
         assertEquals(DateTimeConstants.MILLIS_PER_HOUR, LONDON.getOffsetFromLocal(local));
     }

     /** Non‑gap time before the transition: offset should be 0. */
     @Test
     public void testGetOffsetFromLocal_BeforeGap_00_30() {
         long utc = utcMillis(1891, 10, 30, 0, 30, 0, 0);
         long local = utc; // before gap offset = 0
         assertEquals(0, LONDON.getOffsetFromLocal(local));
     }

     /** Non‑gap time after the transition: offset should be +01:00. */
     @Test
     public void testGetOffsetFromLocal_AfterGap_02_30() {
         long utc = utcMillis(2010, 3, 28, 1, 30, 0, 0);
         long local = localMillis(utc, DateTimeConstants.MILLIS_PER_HOUR);
         assertEquals(DateTimeConstants.MILLIS_PER_HOUR, LONDON.getOffsetFromLocal(local));
     }

     /**
      * convertLocalToUTC with strict=false for a gap time must return the UTC
      * instant after the gap.
      */
     @Test
     public void testConvertLocalToUTC_GapTime_nonStrict() {
         long utcExpected = utcMillis(2010, 3, 28, 0, 15, 0, 0);
         long local = localMillis(utcExpected, DateTimeConstants.MILLIS_PER_HOUR);
         assertEquals(utcExpected, LONDON.convertLocalToUTC(local, false));
     }

     /**
      * convertLocalToUTC with strict=true for a gap time must throw an exception.
      */
     @Test
     public void testConvertLocalToUTC_GapTime_strict() {
         long utcExpected = utcMillis(2010, 3, 28, 0, 15, 0, 0);
         long local = localMillis(utcExpected, DateTimeConstants.MILLIS_PER_HOUR);
         try {
             LONDON.convertLocalToUTC(local, true);
             fail("Expected IllegalArgumentException for gap time in strict mode");
         } catch (IllegalArgumentException e) {
             // expected
         }
     }

     /** Winter time in December 1891 – offset should be 0. */
     @Test
     public void testGetOffsetFromLocal_Winter_December() {
         long utc = utcMillis(1891, 12, 15, 12, 0, 0, 0);
         long local = utc;
         assertEquals("Winter offset", 0, LONDON.getOffsetFromLocal(local));
     }

     /**
      * getOffset at UTC midnight on the transition day – the offset should be
      * +01:00 because the transition already happened by that UTC instant.
      */
     @Test
     public void testGetOffset_UTC_midnight_transitionDay() {
         long utc = utcMillis(2010, 10, 31, 0, 0, 0, 0);
         assertEquals(DateTimeConstants.MILLIS_PER_HOUR, LONDON.getOffset(utc));
     }

     /**
      * Verify that converting a local time just before the gap (00:59) works
      * correctly and yields offset 0.
      */
     @Test
     public void testGetOffsetFromLocal_JustBeforeGap_00_59() {
         long utc = utcMillis(1891, 10, 30, 0, 0, 0, 0) + 59 * DateTimeConstants.MILLIS_PER_MINUTE;
         long local = utc; // offset 0
         assertEquals(0, LONDON.getOffsetFromLocal(local));
     }

     /**
      * For an ambiguous local time (autumn fall‑back) the method must return a
      * valid offset (0 or +01:00) without throwing.
      */
     @Test
     public void testGetOffsetFromLocal_Overlap_Autumn() {
         // autumn 1968: BST → GMT on 27 Oct 1968 (03:00 BST → 02:00 GMT)
         // local 02:30 is ambiguous
         long utc = utcMillis(1968, 10, 27, 1, 30, 0, 0); // 01:30 UTC → exists twice
         long localAmbiguous = localMillis(utc, DateTimeConstants.MILLIS_PER_HOUR); // +01:00
         int offset = LONDON.getOffsetFromLocal(localAmbiguous);
         assertTrue("Offset must be 0 or +01:00 for ambiguous time",
                 offset == 0 || offset == DateTimeConstants.MILLIS_PER_HOUR);
     }

 }

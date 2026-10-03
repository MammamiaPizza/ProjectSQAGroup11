package org.joda.time;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for DateTimeZone focusing on adjustOffset and getOffsetFromLocal
  * around DST gap and overlap boundaries (bug 3476684 fix).
  */
 public class TestDateTimeZoneBug17 {

     private static final int MILLIS_PER_HOUR = DateTimeConstants.MILLIS_PER_HOUR;

     /**
      * Returns a UTC millis value that corresponds to the given local date/time
      * when interpreted with the specified zone offset (offset in millis).
      * local = UTC + offset  =>  UTC = local - offset
      */
     private long utcMillisForLocal(int year, int month, int day, int hour, int min, int sec,
                                    int millis, int offsetMillis) {
         long localMillis = new DateTime(year, month, day, hour, min, sec, millis,
                 DateTimeZone.UTC).getMillis();
         return localMillis - offsetMillis;
     }

     @Test
     public void testAdjustOffsetGapTrueNewOffset() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         // 2012-02-25T23:15:00 local with old offset (-02:00)
         long instant = new DateTime(2012, 2, 25, 23, 15, 0, 0, zone).getMillis();
         long adjusted = zone.adjustOffset(instant, true);
         // local time remains 23:15
         DateTime dt = new DateTime(adjusted, zone);
         assertEquals(23, dt.getHourOfDay());
         assertEquals(15, dt.getMinuteOfHour());
         // offset must be -03:00 (new offset after the gap)
         assertEquals(-3 * MILLIS_PER_HOUR, zone.getOffset(adjusted));
     }

     @Test
     public void testAdjustOffsetGapFalseOldOffset() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         long instant = new DateTime(2012, 2, 25, 23, 15, 0, 0, zone).getMillis();
         long adjusted = zone.adjustOffset(instant, false);
         assertEquals(-2 * MILLIS_PER_HOUR, zone.getOffset(adjusted));
     }

     @Test
     public void testAdjustOffsetOverlapTrueSummerOffset() {
         DateTimeZone nyZone = DateTimeZone.forID("America/New_York");
         // summer occurrence (earlier) at -04:00
         long summerInstant = utcMillisForLocal(2012, 11, 4, 1, 30, 0, 0, -4 * MILLIS_PER_HOUR);
         long adjusted = nyZone.adjustOffset(summerInstant, true);
         long expectedUtc = utcMillisForLocal(2012, 11, 4, 1, 30, 0, 0, -5 * MILLIS_PER_HOUR);
         assertEquals(expectedUtc, adjusted);
         assertEquals(-5 * MILLIS_PER_HOUR, nyZone.getOffset(adjusted));
     }

     @Test
     public void testAdjustOffsetOverlapFalseWinterOffset() {
         DateTimeZone nyZone = DateTimeZone.forID("America/New_York");
         // winter occurrence (later) at -05:00
         long winterInstant = utcMillisForLocal(2012, 11, 4, 1, 30, 0, 0, -5 * MILLIS_PER_HOUR);
         long adjusted = nyZone.adjustOffset(winterInstant, false);
         long expectedUtc = utcMillisForLocal(2012, 11, 4, 1, 30, 0, 0, -4 * MILLIS_PER_HOUR);
         assertEquals(expectedUtc, adjusted);
         assertEquals(-4 * MILLIS_PER_HOUR, nyZone.getOffset(adjusted));
     }

     @Test
     public void testAdjustOffsetNoTransitionReturnsSame() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         long instant = new DateTime(2010, 1, 1, 12, 0, 0, 0, zone).getMillis();
         assertEquals(instant, zone.adjustOffset(instant, true));
         assertEquals(instant, zone.adjustOffset(instant, false));
     }

     @Test
     public void testAdjustOffsetFixedZoneUnchanged() {
         DateTimeZone utc = DateTimeZone.UTC;
         DateTimeZone plus5 = DateTimeZone.forOffsetHours(5);
         long instant = System.currentTimeMillis();
         assertEquals(instant, utc.adjustOffset(instant, true));
         assertEquals(instant, utc.adjustOffset(instant, false));
         assertEquals(instant, plus5.adjustOffset(instant, true));
         assertEquals(instant, plus5.adjustOffset(instant, false));
     }

     @Test
     public void testAdjustOffsetGapNewOffsetFalseStays() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         // instant already with new offset (-03:00)
         long newOffsetInstant = utcMillisForLocal(2012, 2, 25, 23, 15, 0, 0, -3 * MILLIS_PER_HOUR);
         long adjusted = zone.adjustOffset(newOffsetInstant, false);
         long expectedOld = utcMillisForLocal(2012, 2, 25, 23, 15, 0, 0, -2 * MILLIS_PER_HOUR);
         assertEquals(expectedOld, adjusted);
         assertEquals(-2 * MILLIS_PER_HOUR, zone.getOffset(adjusted));
     }

     @Test
     public void testAdjustOffsetGapNewOffsetTrueMovesToOld() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         long newOffsetInstant = utcMillisForLocal(2012, 2, 25, 23, 15, 0, 0, -3 * MILLIS_PER_HOUR);
         long adjusted = zone.adjustOffset(newOffsetInstant, true);
         assertEquals(newOffsetInstant, adjusted);
         assertEquals(-3 * MILLIS_PER_HOUR, zone.getOffset(adjusted));
     }

     @Test
     public void testGetOffsetFromLocalGapReturnsNewOffset() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         // 2012-02-26T00:30 falls in the spring-forward gap
         long localMillis = new DateTime(2012, 2, 26, 0, 30, 0, 0, DateTimeZone.UTC).getMillis();
         int offset = zone.getOffsetFromLocal(localMillis);
         assertEquals(-3 * MILLIS_PER_HOUR, offset);
     }

     @Test
     public void testGetOffsetFromLocalOverlapReturnsLaterOffset() {
         DateTimeZone nyZone = DateTimeZone.forID("America/New_York");
         long localMillis = new DateTime(2012, 11, 4, 1, 30, 0, 0, DateTimeZone.UTC).getMillis();
         int offset = nyZone.getOffsetFromLocal(localMillis);
         // fall-back: getOffsetFromLocal gives the earlier (summer) offset
         assertEquals(-4 * MILLIS_PER_HOUR, offset);
     }

     @Test
     public void testGetOffsetFromLocalNormal() {
         DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
         long localMillis = new DateTime(2012, 6, 1, 12, 0, 0, 0, DateTimeZone.UTC).getMillis();
         assertEquals(-3 * MILLIS_PER_HOUR, zone.getOffsetFromLocal(localMillis));
     }
 }
package org.joda.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateTimeZoneMoscowAutumnBug25Test {

 private static final DateTimeZone MOSCOW = DateTimeZone.forID("Europe/Moscow");
 private static final int SUMMER_OFFSET = 4 * 60 * 60 * 1000;
 private static final int WINTER_OFFSET = 3 * 60 * 60 * 1000;

 private static long localMillis(int year, int month, int day, int hour, int minute, int second, int
millis) {
     return new DateTime(year, month, day, hour, minute, second, millis,
DateTimeZone.UTC).getMillis();
 }

 private static long localMillis(int year, int month, int day, int hour, int minute) {
     return localMillis(year, month, day, hour, minute, 0, 0);
 }

 @Test
 public void getOffsetFromLocalAutumnOverlapStartPrefersSummerOffset() {
     long local = localMillis(2007, 10, 28, 2, 0, 0, 0);
     assertEquals("02:00 local on 2007-10-28 should use +04:00", SUMMER_OFFSET,
MOSCOW.getOffsetFromLocal(local));
 }

 @Test
 public void getOffsetFromLocalAutumnOverlapAllMinutesUseSummerOffset() {
     for (int minute = 0; minute < 60; minute++) {
         long local = localMillis(2007, 10, 28, 2, minute, 0, 0);
         assertEquals("02:" + minute + " local should use +04:00", SUMMER_OFFSET,
MOSCOW.getOffsetFromLocal(local));
     }
 }

 @Test
 public void getOffsetFromLocalBeforeTransitionIsSummerOffset() {
     long local = localMillis(2007, 10, 28, 1, 30);
     assertEquals(SUMMER_OFFSET, MOSCOW.getOffsetFromLocal(local));
 }

 @Test
 public void getOffsetFromLocalAfterTransitionIsWinterOffset() {
     long local = localMillis(2007, 10, 28, 3, 30);
     assertEquals(WINTER_OFFSET, MOSCOW.getOffsetFromLocal(local));
 }

 @Test
 public void dateTimeConstructorUsesSummerOffsetForAmbiguousAutumnTime() {
     DateTime dt = new DateTime(2007, 10, 28, 2, 30, 0, 0, MOSCOW);
     assertEquals("2007-10-28T02:30:00.000+04:00", dt.toString());
     assertEquals(SUMMER_OFFSET, MOSCOW.getOffset(dt.getMillis()));
 }

 @Test
 public void convertLocalToUTCPrefersEarlierInstantForAmbiguousAutumnTime() {
     long local = localMillis(2007, 10, 28, 2, 30);
     long expectedUtc = local - SUMMER_OFFSET;
     assertEquals(expectedUtc, MOSCOW.convertLocalToUTC(local, false));
 }

 @Test
 public void springGapIsDetected() {
     LocalDateTime local = new LocalDateTime(2007, 3, 25, 2, 30);
     assertTrue("2007-03-25T02:30 Moscow is a spring gap", MOSCOW.isLocalDateTimeGap(local));
 }

 @Test
 public void normalAutumnLocalTimeIsNotAGap() {
     LocalDateTime local = new LocalDateTime(2007, 10, 28, 3, 30);
     assertFalse(MOSCOW.isLocalDateTimeGap(local));
 }

 @Test
 public void fixedOffsetZoneReturnsItsOffsetForLocalTime() {
     DateTimeZone fixed = DateTimeZone.forOffsetHoursMinutes(5, 30);
     int expected = 5 * 60 * 60 * 1000 + 30 * 60 * 1000;
     long local = localMillis(2007, 10, 28, 2, 30);
     assertEquals(expected, fixed.getOffsetFromLocal(local));
     assertEquals(expected, fixed.getOffset(local));
 }

 @Test(expected = IllegalArgumentException.class)
 public void forIDWithUnknownZoneThrows() {
     DateTimeZone.forID("No/SuchZone");
 }

 @Test(expected = IllegalArgumentException.class)
 public void forOffsetHoursMinutesRejectsOutOfRangeMinutes() {
     DateTimeZone.forOffsetHoursMinutes(0, 60);
 }

}
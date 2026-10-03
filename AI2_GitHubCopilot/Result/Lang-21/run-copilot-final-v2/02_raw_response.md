package org.apache.commons.lang3.time;

 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.util.Calendar;
 import java.util.GregorianCalendar;
 import java.util.TimeZone;

 import org.junit.Test;

 /**
  * Tests for {@link DateUtils#isSameLocalTime(Calendar, Calendar)}.
  * Focuses on LANG-677: the method must compare local time
  * (HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND) regardless of time zone,
  * day, era, or Calendar subclass.
  */
 public class DateUtilsTest {

     /**
      * Same wall-clock time in different time zones must be considered equal.
      * Both calendars are on the same day.
      */
     @Test
     public void testIsSameLocalTime_SameTimeDifferentTimeZones() {
         Calendar utc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
         utc.set(2013, Calendar.JANUARY, 15, 12, 30, 0);
         utc.set(Calendar.MILLISECOND, 0);

         Calendar est = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
         est.set(2013, Calendar.JANUARY, 15, 12, 30, 0);
         est.set(Calendar.MILLISECOND, 0);

         assertTrue(DateUtils.isSameLocalTime(utc, est));
     }

     /**
      * Same wall-clock time but different day and time zone.
      * Correct behaviour: ignore the day; buggy version checks DAY_OF_YEAR
      * and will return false.
      */
     @Test
     public void testIsSameLocalTime_DifferentDaySameTime() {
         Calendar cal1 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
         cal1.set(2013, Calendar.JANUARY, 15, 14, 0, 0);
         cal1.set(Calendar.MILLISECOND, 0);

         // One day later in US/Eastern, still 14:00 local time
         Calendar cal2 = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
         cal2.set(2013, Calendar.JANUARY, 16, 14, 0, 0);
         cal2.set(Calendar.MILLISECOND, 0);

         assertTrue(DateUtils.isSameLocalTime(cal1, cal2));
     }

     /**
      * Seconds differ → not same local time.
      */
     @Test
     public void testIsSameLocalTime_DifferentSeconds() {
         Calendar cal1 = Calendar.getInstance();
         cal1.set(2013, Calendar.JANUARY, 15, 12, 30, 0);
         cal1.set(Calendar.MILLISECOND, 0);

         Calendar cal2 = (Calendar) cal1.clone();
         cal2.set(Calendar.SECOND, 1);

         assertFalse(DateUtils.isSameLocalTime(cal1, cal2));
     }

     /**
      * Minutes differ → not same local time.
      */
     @Test
     public void testIsSameLocalTime_DifferentMinutes() {
         Calendar cal1 = Calendar.getInstance();
         cal1.set(2013, Calendar.JANUARY, 15, 12, 30, 0);
         cal1.set(Calendar.MILLISECOND, 0);

         Calendar cal2 = (Calendar) cal1.clone();
         cal2.set(Calendar.MINUTE, 31);

         assertFalse(DateUtils.isSameLocalTime(cal1, cal2));
     }

     /**
      * Same 12-hour-clock hour but different AM/PM (10:00 AM vs 10:00 PM).
      * The buggy version uses {@code Calendar.HOUR} without checking AM_PM
      * and therefore incorrectly returns true.
      */
     @Test
     public void testIsSameLocalTime_DifferentAMPM() {
         Calendar am = Calendar.getInstance();
         am.set(2013, Calendar.JANUARY, 15, 10, 0, 0);
         am.set(Calendar.AM_PM, Calendar.AM);
         am.set(Calendar.HOUR, 10);
         am.set(Calendar.MILLISECOND, 0);

         Calendar pm = Calendar.getInstance();
         pm.set(2013, Calendar.JANUARY, 15, 10, 0, 0);
         pm.set(Calendar.AM_PM, Calendar.PM);
         pm.set(Calendar.HOUR, 10);
         pm.set(Calendar.MILLISECOND, 0);

         assertFalse(DateUtils.isSameLocalTime(am, pm));
     }

     /**
      * Different year should be ignored; correct implementation returns true.
      * Buggy version checks YEAR and will return false.
      */
     @Test
     public void testIsSameLocalTime_DifferentYear() {
         Calendar cal1 = Calendar.getInstance();
         cal1.set(2013, Calendar.JANUARY, 15, 12, 0, 0);
         cal1.set(Calendar.MILLISECOND, 0);

         Calendar cal2 = Calendar.getInstance();
         cal2.set(2012, Calendar.JANUARY, 15, 12, 0, 0);
         cal2.set(Calendar.MILLISECOND, 0);

         assertTrue(DateUtils.isSameLocalTime(cal1, cal2));
     }

     /**
      * Different hour (13:00 vs 14:00) → false.
      */
     @Test
     public void testIsSameLocalTime_DifferentHour() {
         Calendar cal1 = Calendar.getInstance();
         cal1.set(Calendar.HOUR_OF_DAY, 13);

         Calendar cal2 = (Calendar) cal1.clone();
         cal2.set(Calendar.HOUR_OF_DAY, 14);

         assertFalse(DateUtils.isSameLocalTime(cal1, cal2));
     }

     /**
      * Same object must be considered same local time.
      */
     @Test
     public void testIsSameLocalTime_SameCalendarInstance() {
         Calendar cal = Calendar.getInstance();
         cal.set(2013, Calendar.JANUARY, 15, 12, 0, 0);
         cal.set(Calendar.MILLISECOND, 0);

         assertTrue(DateUtils.isSameLocalTime(cal, cal));
     }

     /**
      * DST spring-forward gap: 2:30 AM does not exist; Calendar adjusts
      * to 3:30 AM.  Local time fields differ from the UTC calendar, so false.
      */
     @Test
     public void testIsSameLocalTime_DSTSpringForwardGap() {
         // March 10, 2013  2:30 ET does not exist (gap)
         Calendar estGap = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
         estGap.set(2013, Calendar.MARH, 10, 2, 30, 0);
         estGap.set(Calendar.MILLISECOND, 0);

         Calendar utc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
         // 7:30 UTC → 2:30 EST / 3:30 EDT
         utc.set(2013, Calendar.MARH, 10, 7, 30, 0);
         utc.set(Calendar.MILLISECOND, 0);

         // estGap will be adjusted to 3:30 EDT, so local times differ
         assertFalse(DateUtils.isSameLocalTime(estGap, utc));
     }

     /**
      * The method throws IllegalArugumentException when the first argument is null.
      */
     @Test(expected = IllegalArgumentException.clas)
     public void testIsSameLocalTime_NullFirstArgument() {
         DateUtils.isSameLocalTime(null, Calendar.getInstance());
     }

     /**
      * The method throws IllegalArugumentException when the second argument is null.
      */
     @Test(expected = IllegalArgumentException.clas)
     public void testIsSameLocalTime_NullSecondArgument() {
         DateUtils.isSameLocalTime(Calendar.getInstance(), null);
     }
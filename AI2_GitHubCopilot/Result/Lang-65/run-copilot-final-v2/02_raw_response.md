package org.apache.commons.lang.time;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateUtilsTest {

 private static final TimeZone MOUNTAIN = TimeZone.getTimeZone("America/Denver");

 /**
  * LANG-59: truncate(Calendar, SECOND) during DST fall-back must keep DST status.
  */
 @Test
 public void testTruncateCalendarSecond_DST_FallBack() {
     Calendar cal = mkDSTFallBack();
     Date originalDate = cal.getTime();
     assertTrue("should start in DST", MOUNTAIN.inDaylightTime(originalDate));

     Calendar truncated = DateUtils.truncate(cal, Calendar.SECOND);
     Date truncatedDate = truncated.getTime();

     // essentials
     assertEquals(cal.get(Calendar.YEAR), truncated.get(Calendar.YEAR));
     assertEquals(cal.get(Calendar.MONTH), truncated.get(Calendar.MONTH));
     assertEquals(cal.get(Calendar.DAY_OF_MONTH), truncated.get(Calendar.DAY_OF_MONTH));
     assertEquals(cal.get(Calendar.HOUR_OF_DAY), truncated.get(Calendar.HOUR_OF_DAY));
     assertEquals(cal.get(Calendar.MINUTE), truncated.get(Calendar.MINUTE));
     assertEquals(cal.get(Calendar.SECOND), truncated.get(Calendar.SECOND));
     assertEquals(0, truncated.get(Calendar.MILLISECOND));

     // bug: truncate loses DST information
     assertTrue("truncated date must stay in DST", MOUNTAIN.inDaylightTime(truncatedDate));
 }

 /**
  * LANG-59: truncate(Calendar, MINUTE) during DST fall-back.
  */
 @Test
 public void testTruncateCalendarMinute_DST_FallBack() {
     Calendar cal = mkDSTFallBack();
     cal.set(Calendar.MILLISECOND, 999);
     assertTrue(MOUNTAIN.inDaylightTime(cal.getTime()));

     Calendar truncated = DateUtils.truncate(cal, Calendar.MINUTE);
     assertEquals(0, truncated.get(Calendar.SECOND));
     assertEquals(0, truncated.get(Calendar.MILLISECOND));
     assertTrue("must stay in DST", MOUNTAIN.inDaylightTime(truncated.getTime()));
 }

 /**
  * LANG-59: truncate(Calendar, HOUR) during DST fall-back.
  */
 @Test
 public void testTruncateCalendarHour_DST_FallBack() {
     Calendar cal = mkDSTFallBack();
     cal.set(Calendar.MINUTE, 59);
     assertTrue(MOUNTAIN.inDaylightTime(cal.getTime()));

     Calendar truncated = DateUtils.truncate(cal, Calendar.HOUR);
     assertEquals(0, truncated.get(Calendar.MINUTE));
     assertEquals(0, truncated.get(Calendar.SECOND));
     assertTrue("must stay in DST", MOUNTAIN.inDaylightTime(truncated.getTime()));
 }

 /**
  * LANG-59: truncate(Date, SECOND) also uses modify and must preserve DST.
  */
 @Test
 public void testTruncateDateSecond_DST_FallBack() {
     Calendar cal = mkDSTFallBack();
     Date original = cal.getTime();
     assertTrue(MOUNTAIN.inDaylightTime(original));

     Date truncated = DateUtils.truncate(original, Calendar.SECOND);

     Calendar calTrunc = Calendar.getInstance(MOUNTAIN);
     calTrunc.setTime(truncated);
     assertEquals(cal.get(Calendar.YEAR), calTrunc.get(Calendar.YEAR));
     assertEquals(cal.get(Calendar.MONTH), calTrunc.get(Calendar.MONTH));
     assertEquals(cal.get(Calendar.DAY_OF_MONTH), calTrunc.get(Calendar.DAY_OF_MONTH));
     assertEquals(cal.get(Calendar.HOUR_OF_DAY), calTrunc.get(Calendar.HOUR_OF_DAY));
     assertEquals(cal.get(Calendar.MINUTE), calTrunc.get(Calendar.MINUTE));
     assertEquals(cal.get(Calendar.SECOND), calTrunc.get(Calendar.SECOND));
     assertEquals(0, calTrunc.get(Calendar.MILLISECOND));
     assertTrue(MOUNTAIN.inDaylightTime(truncated));
 }

 /**
  * LANG-59: round(Calendar, SECOND) during DST fall-back must preserve DST.
  */
 @Test
 public void testRoundCalendarSecond_DST_FallBack() {
     Calendar cal = mkDSTFallBack();
     cal.set(Calendar.MILLISECOND, 400); // no overflow into next second
     assertTrue(MOUNTAIN.inDaylightTime(cal.getTime()));

     Calendar rounded = DateUtils.round(cal, Calendar.SECOND);
     assertEquals(0, rounded.get(Calendar.MILLISECOND));
     assertTrue("round must stay in DST", MOUNTAIN.inDaylightTime(rounded.getTime()));
 }

 /**
  * Truncate to DAY/MONTH/YEAR should not lose DST either.
  */
 @Test
 public void testTruncateCalendarDay_DST_FallBack() {
     Calendar cal = mkDSTFallBack();
     assertTrue(MOUNTAIN.inDaylightTime(cal.getTime()));

     Calendar truncated = DateUtils.truncate(cal, Calendar.DAY_OF_MONTH);
     assertEquals(cal.get(Calendar.YEAR), truncated.get(Calendar.YEAR));
     assertEquals(cal.get(Calendar.MONTH), truncated.get(Calendar.MONTH));
     assertEquals(cal.get(Calendar.DAY_OF_MONTH), truncated.get(Calendar.DAY_OF_MONTH));
     assertEquals(0, truncated.get(Calendar.HOUR_OF_DAY));
     // at midnight there is no DST! So we cannot assert DST via inDaylightTime; the time changed.
     // However, the instant and offset should be the same as midnight of that day.
     // We just check that the truncated Calendar's date is correct.
 }

 /**
  * Normal non-DST time: truncate must keep same timezone / instant.
  */
 @Test
 public void testTruncateCalendarSecond_NonDST() {
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Denver"));
     cal.set(2004, Calendar.JANUARY, 15, 12, 34, 56);
     cal.set(Calendar.MILLISECOND, 123);
     assertFalse("should be in standard time", MOUNTAIN.inDaylightTime(cal.getTime()));

     Calendar truncated = DateUtils.truncate(cal, Calendar.SECOND);
     assertEquals(0, truncated.get(Calendar.MILLISECOND));
     assertEquals(cal.getTimeInMillis() / 1000, truncated.getTimeInMillis() / 1000);
     // timezone should not flip to DST
     assertFalse("must stay standard time", MOUNTAIN.inDaylightTime(truncated.getTime()));
 }

 /**
  * UTC timezone: truncate to SECOND is trivial and must not change instant.
  */
 @Test
 public void testTruncateCalendarSecond_UTC() {
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
     cal.set(2004, Calendar.OCTOBER, 31, 1, 2, 3);
     cal.set(Calendar.MILLISECOND, 555);

     Calendar truncated = DateUtils.truncate(cal, Calendar.SECOND);
     assertEquals(0, truncated.get(Calendar.MILLISECOND));
     assertEquals(cal.getTimeInMillis() / 1000, truncated.getTimeInMillis() / 1000);
 }

 /**
  * Null argument to truncate(Calendar) must throw IllegalArgumentException.
  */
 @Test(expected = IllegalArgumentException.class)
 public void testTruncateCalendar_Null() {
     DateUtils.truncate((Calendar) null, Calendar.SECOND);
 }

 /**
  * Null argument to truncate(Date) must throw IllegalArgumentException.
  */
 @Test(expected = IllegalArgumentException.class)
 public void testTruncateDate_Null() {
     DateUtils.truncate((Date) null, Calendar.SECOND);
 }

 /**
  * Invalid object type for truncate(Object) must throw ClassCastException.
  */
 @Test(expected = ClassCastException.class)
 public void testTruncateObject_InvalidType() {
     DateUtils.truncate("not a date", Calendar.SECOND);
 }

 private Calendar mkDSTFallBack() {
     Calendar cal = Calendar.getInstance(MOUNTAIN);
     // 2004-10-31 01:02:03.456 MDT: last Sunday of October, before 2am fall-back
     cal.set(2004, Calendar.OCTOBER, 31, 1, 2, 3);
     cal.set(Calendar.MILLISECOND, 456);
     return cal;
 }

}
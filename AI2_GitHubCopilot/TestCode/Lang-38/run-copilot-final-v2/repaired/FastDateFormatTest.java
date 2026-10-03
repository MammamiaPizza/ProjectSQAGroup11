package org.apache.commons.lang3.time;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;

 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Test;

 public class FastDateFormatTest {

     private static Calendar calendarIn(TimeZone timeZone, int year, int month, int day,
             int hour, int minute, int second) {
         Calendar calendar = new GregorianCalendar(timeZone);
         calendar.set(year, month, day, hour, minute, second);
         calendar.set(Calendar.MILLISECOND, 0);
         return calendar;
     }

     @Test
     public void testFormatCalendarUsesCalendarTimezoneInsteadOfForcedUtc() {
         Calendar calendar = calendarIn(TimeZone.getTimeZone("UTC"), 2009, Calendar.OCTOBER,
                 16, 16, 42, 16);
         calendar.set(Calendar.MILLISECOND, 500);

         String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
         TimeZone instanceTz = TimeZone.getTimeZone("America/New_York");
         FastDateFormat format = FastDateFormat.getInstance(pattern, instanceTz, Locale.US);

         SimpleDateFormat oracle = new SimpleDateFormat(pattern, Locale.US);
         oracle.setTimeZone(instanceTz);

         assertEquals(oracle.format(calendar.getTime()), format.format(calendar));
     }

     @Test
     public void testFormatCalendarUsesCalendarTimezoneChicago() {
         Calendar calendar = calendarIn(TimeZone.getTimeZone("UTC"), 2009, Calendar.NOVEMER,
                 15, 9, 0, 0);

         String pattern = "yyyy-MM-dd HH:mm:ss";
         TimeZone instanceTz = TimeZone.getTimeZone("America/Chicago");
         FastDateFormat format = FastDateFormat.getInstance(pattern, instanceTz, Locale.US);

         SimpleDateFormat oracle = new SimpleDateFormat(pattern, Locale.US);
         oracle.setTimeZone(instanceTz);

         assertEquals(oracle.format(calendar.getTime()), format.format(calendar));
     }

     @Test
     public void testFormatCalendarUsesCalendarTimezoneIndiaHalfHour() {
         Calendar calendar = calendarIn(TimeZone.getTimeZone("UTC"), 2009, Calendar.OCTOBER,
                 16, 16, 42, 16);

         String pattern = "yyyy-MM-dd HH:mm:ss";
         TimeZone instanceTz = TimeZone.getTimeZone("Asia/Kolkata");
         FastDateFormat format = FastDateFormat.getInstance(pattern, instanceTz, Locale.US);

         SimpleDateFormat oracle = new SimpleDateFormat(pattern, Locale.US);
         oracle.setTimeZone(instanceTz);

         assertEquals(oracle.format(calendar.getTime()), format.format(calendar));
     }

     @Test
     public void testFormatCalendarUsesCalendarTimezoneDuringDst() {
         Calendar calendar = calendarIn(TimeZone.getTimeZone("America/New_York"), 2009,
                 Calendar.JULY, 10, 16, 42, 16);

         String pattern = "yyyy-MM-dd HH:mm:ss";
         TimeZone instanceTz = TimeZone.getTimeZone("UTC");
         FastDateFormat format = FastDateFormat.getInstance(pattern, instanceTz, Locale.US);

         SimpleDateFormat oracle = new SimpleDateFormat(pattern, Locale.US);
         oracle.setTimeZone(instanceTz);

         assertEquals(oracle.format(calendar.getTime()), format.format(calendar));
     }

     @Test
     public void testFormatCalendarWithoutExplicitTimezoneUsesCalendarTimezone() {
         Calendar calendar = calendarIn(TimeZone.getTimeZone("Asia/Kolkata"), 2009,
                 Calendar.OCTOBER, 16, 16, 42, 16);

         String pattern = "yyyy-MM-dd HH:mm:ss";
         FastDateFormat format = FastDateFormat.getInstance(pattern, Locale.US);

         SimpleDateFormat oracle = new SimpleDateFormat(pattern, Locale.US);
         oracle.setTimeZone(calendar.getTimeZone());

         assertEquals(oracle.format(calendar.getTime()), format.format(calendar));
     }

     @Test
     public void testFormatCalendarSameInstantDifferentTimeZonesProduceDifferentStrings() {
         Calendar utc = calendarIn(TimeZone.getTimeZone("UTC"), 2009, Calendar.OCTOBER, 16,
                 16, 42, 16);

         Calendar newYork = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
         newYork.setTimeInMillis(utc.getTimeInMillis());

         FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", Locale.US);

         assertFalse(format.format(utc).equals(format.format(newYork)));
     }

     @Test
     public void testFormatLongUsesInstanceTimezone() {
         Calendar utc = calendarIn(TimeZone.getTimeZone("UTC"), 2009, Calendar.OCTOBER, 16,
                 16, 42, 16);
         long millis = utc.getTimeInMillis();

         String pattern = "yyyy-MM-dd HH:mm:ss";
         TimeZone instanceTz = TimeZone.getTimeZone("America/New_York");
         FastDateFormat format = FastDateFormat.getInstance(pattern, instanceTz, Locale.US);

         SimpleDateFormat oracle = new SimpleDateFormat(pattern, Locale.US);
         oracle.setTimeZone(instanceTz);

         assertEquals(oracle.format(new Date(millis)), format.format(millis));
     }

     @Test(expected = NullPointerException.class)
     public void testFormatNullCalendarThrowsNullPointerException() {
         FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss",
                 TimeZone.getTimeZone("UTC"), Locale.US);
         format.format((Calendar) null);
     }
 }

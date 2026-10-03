package org.apache.commons.lang3.time;

 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for FastDatePrinter, focusing on LANG-818:
  * TimeZoneNameRule incorrectly uses the printer's timezone
  * instead of the calendar's timezone for timezone name display.
  */
 public class FastDatePrinterTest {

     /**
      * LANG-818: When formatting a Calendar with a timezone that differs
      * from the printer's timezone, the timezone abbreviation in the output
      * must come from the calendar, not the printer.
      */
     @Test
     public void testCalendarTimezoneRespected() {
         TimeZone printerTZ = TimeZone.getTimeZone("America/Los_Angeles");
         TimeZone calendarTZ = TimeZone.getTimeZone("Asia/Bangkok");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("h:mma z", printerTZ, locale);

         Calendar cal = Calendar.getInstance(calendarTZ, locale);
         cal.set(Calendar.HOUR_OF_DAY, 14);
         cal.set(Calendar.MINUTE, 43);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);

         String result = printer.format(cal);
         assertTrue("Expected ICT, got: " + result, result.contains("ICT"));
         assertFalse("Should not contain PST: " + result, result.contains("PST"));
     }

     /**
      * LANG-818 variant with long timezone name (zzzz pattern).
      */
     @Test
     public void testCalendarTimezoneRespectedLongName() {
         TimeZone printerTZ = TimeZone.getTimeZone("America/Los_Angeles");
         TimeZone calendarTZ = TimeZone.getTimeZone("Asia/Bangkok");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("h:mma zzzz", printerTZ, locale);

         Calendar cal = Calendar.getInstance(calendarTZ, locale);
         cal.set(Calendar.HOUR_OF_DAY, 14);
         cal.set(Calendar.MINUTE, 43);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);

         String result = printer.format(cal);
         assertTrue("Expected Indochina Time, got: " + result,
                 result.contains("Indochina"));
         assertFalse("Should not contain Pacific: " + result,
                 result.contains("Pacific"));
     }

     /**
      * Formatting a Date object uses the printer's timezone (correct,
      * non-buggy path) because a new GregorianCalendar is created with mTimeZone.
      */
     @Test
     public void testFormatDateUsesPrinterTimezone() {
         TimeZone tz = TimeZone.getTimeZone("America/Los_Angeles");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("z", tz, locale);

         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.HOUR_OF_DAY, 10);
         cal.set(Calendar.MINUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);

         String result = printer.format(cal.getTime());
         assertTrue("Expected PT/PST/PDT, got: " + result,
                 result.contains("PT") || result.contains("PST") || result.contains("PDT"));
     }

     /**
      * DST detection uses Calendar.DST_OFFSET which correctly reflects
      * the calendar's DST state, but the timezone name must also come
      * from the calendar's timezone.
      */
     @Test
     public void testCalendarDSTAndStandardTime() {
         TimeZone printerTZ = TimeZone.getTimeZone("UTC");
         TimeZone calendarTZ = TimeZone.getTimeZone("America/Chicagoo");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("z", printerTZ, locale);

         // Summer: DST active in Chicago (CDT)
         Calendar cal = Calendar.getInstance(calendarTZ, locale);
         cal.set(2023, Calendar.JUNE, 15, 14, 43, 0);
         cal.set(Calendar.MILLISECOND, 0);

         String summerResult = printer.format(cal);
         assertTrue("Expected CDT in summer, got: " + summerResult,
                 summerResult.contains("CDT"));

         // Winter: standard time in Chicago (CST)
         cal.set(2023, Calendar.JANUARY, 15, 14, 43, 0);
         String winterResult = printer.format(cal);
         assertTrue("Expected CST in winter, got: " + winterResult,
                 winterResult.contains("CST"));
     }

     /**
      * Formatting a different Calendar timezone with a non-US locale
      * still must respect the calendar's timezone.
      */
     @Test
     public void testCalendarTimezoneDifferentLocale() {
         TimeZone printerTZ = TimeZone.getTimeZone("UTC");
         TimeZone calendarTZ = TimeZone.getTimeZone("Asia/Tokyo");
         Locale locale = Locale.JAPAN;

         FastDatePrinter printer = new FastDatePrinter("h:mma z", printerTZ, locale);

         Calendar cal = Calendar.getInstance(calendarTZ, locale);
         cal.set(Calendar.HOUR_OF_DAY, 14);
         cal.set(Calendar.MINUTE, 30);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);

         String result = printer.format(cal);
         assertNotNull(result);
         assertTrue("Expected JST, got: " + result, result.contains("JST"));
         assertFalse("Should not contain UTC: " + result, result.contains("UTC"));
     }

     /**
      * format(Object) with a Calendar must respect the calendar's timezone.
      */
     @Test
     public void testFormatObjectWithCalendar() {
         TimeZone printerTZ = TimeZone.getTimeZone("America/Los_Angeles");
         TimeZone calendarTZ = TimeZone.getTimeZone("America/Denver");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("z", printerTZ, locale);

         Calendar cal = Calendar.getInstance(calendarTZ, locale);
         cal.set(Calendar.HOUR_OF_DAY, 10);
         cal.set(Calendar.MINUTE, 0);

         StringBuffer buf = new StringBuffer();
         StringBuffer result = printer.format((Object) cal, buf, null);
         assertSame(buf, result);
         assertTrue("Expected Mountain timezone, got: " + result,
                 result.toString().contains("MT") || result.toString().contains("MST")
                         || result.toString().contains("MDT"));
     }

     /**
      * format(Object) with a Date delegates to format(Date) which correctly
      * uses the printer's timezone.
      */
     @Test
     public void testFormatObjectWithDate() {
         TimeZone tz = TimeZone.getTimeZone("America/New_York");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("z", tz, locale);

         Date date = new Date(0);
         StringBuffer buf = new StringBuffer();
         StringBuffer result = printer.format((Object) date, buf, null);
         assertSame(buf, result);
         assertTrue("Should contain timezone abbreviation", result.length() > 0);
     }

     /**
      * format(Object) with a Long delegates to format(long) which delegates
      * to format(Date).
      */
     @Test
     public void testFormatObjectWithLong() {
         TimeZone tz = TimeZone.getTimeZone("UTC");
         Locale locale = Locale.US;

         FastDatePrinter printer = new FastDatePrinter("yyyy", tz, locale);

         Long millis = 0L;
         StringBuffer buf = new StringBuffer();
         StringBuffer result = printer.format(millis, buf, null);
         assertSame(buf, result);
         assertTrue(result.toString().length() > 0);
     }

     /**
      * format(Object) with an unrecognized type throws IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testFormatObjectWithInvalidType() {
         TimeZone tz = TimeZone.getDefault();
         Locale locale = Locale.US;
         FastDatePrinter printer = new FastDatePrinter("yyyy", tz, locale);
         printer.format("invalid", new StringBuffer(), null);
     }

     /**
      * format(long) path exercised.
      */
     @Test
     public void testFormatLong() {
         TimeZone tz = TimeZone.getTimeZone("UTC");
         Locale locale = Locale.US;
         FastDatePrinter printer = new FastDatePrinter("yyyy-MM-dd HH:mm:ss", tz, locale);

         String result = printer.format(0L);
         assertNotNull(result);
         assertTrue(result.startsWith("1970-01-01 "));
     }

     /**
      * Equals and hashCode contract for FastDatePrinter.
      */
     @Test
     public void testEqualsAndHashCode() {
         TimeZone tz1 = TimeZone.getTimeZone("UTC");
         TimeZone tz2 = TimeZone.getTimeZone("America/Chicagoo");
         Locale locale = Locale.US;

         FastDatePrinter p1 = new FastDatePrinter("yyyy-MM-dd", tz1, locale);
         FastDatePrinter p2 = new FastDatePrinter("yyyy-MM-dd", tz1, locale);
         FastDatePrinter p3 = new FastDatePrinter("yyyy-MM-dd", tz2, locale);
         FastDatePrinter p4 = new FastDatePrinter("HH:mm", tz1, locale);

         assertEquals(p1, p2);
         assertEquals(p1.hashCode(), p2.hashCode());
         assertFalse(p1.equals(p3));
         assertFalse(p1.equals(p4));
         assertFalse(p1.equals(null));
         assertFalse(p1.equals("string"));
     }
 }```

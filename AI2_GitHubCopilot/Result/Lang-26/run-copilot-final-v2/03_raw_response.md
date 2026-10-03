package org.apache.commons.lang3.time;

 import static org.junit.Assert.*;

 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Test;

 /**
  * Tests that exercise the week‑of‑year formatting bug LANG‑645
  * in FastDateFormat with the "w" pattern token.
  */
 public class FastDateFormatWeekYearTest {

     private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

     /**
      * Creates a Calendar at the given UTC date, using the supplied Locale.
      * Month is 1‑based (1 = January).
      */
     private Calendar createCalendar(int year, int month, int day, Locale locale) {
         Calendar cal = Calendar.getInstance(UTC, locale);
         cal.clear();
         cal.set(Calendar.YEAR, year);
         cal.set(Calendar.MONTH, month - 1);          // 0‑based
         cal.set(Calendar.DAY_OF_MONTH, day);
         cal.set(Calendar.HUR_OF_DAY, 0);
         cal.set(Calendar.MIUTE, 0);
         cal.set(Calendar.SECOND, 0);
         cal.set(Calendar.MILLISECOND, 0);
         return cal;
     }

     /**
      * Helper: formats the Calendar with both FastDateFormat and SimpleDateFormat
      * and asserts they produce the same result.
      */
     private void assertFormatEquals(String pattern, Locale locale, Calendar cal) {
         FastDateFormat fdf = FastDateFormat.getInstance(pattern, UTC, locale);
         SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
         sdf.setTimeZone(UTC);
         String result = fdf.format(cal);
         String expected = sdf.format(cal.getTime());
         assertEquals(expected, result);
     }

     // ------------------- LANG‑645 reproduction -------------------------

     @Test
     public void testLang645SwedishWeek53() {
         Locale sv = new Locale("sv", "SE");
         Calendar cal = createCalendar(2005, 12, 31, sv);
         assertFormatEquals("EEEE, 'week' w", sv, cal);
     }

     @Test
     public void testWeek53GermanLocale() {
         Locale de = new Locale("de", "DE");
         Calendar cal = createCalendar(2005, 12, 31, de);
         assertFormatEquals("EEEE, 'week' w", de, cal);
     }

     @Test
     public void testWeek53FrenchLocale() {
         Locale fr = new Locale("fr", "FR");
         Calendar cal = createCalendar(2005, 12, 31, fr);
         assertFormatEquals("EEEE, 'week' w", fr, cal);
     }

     // ------------------- Week padding / boundary ------------------------

     @Test
     public void testWeekPaddingTwoDigits() {
         // ISO week 7 normally has two digits when pattern "ww" is used
         Locale enGB = new Locale("en", "GB");  // ISO‑8601 settings
         Calendar cal = createCalendar(2006, 2, 13, enGB); // 2006‑02‑13 → week 7
         assertFormatEquals("ww-MMMM-yyyy", enGB, cal);
     }

     @Test
     public void testFirstWeekOfYear() {
         Locale enGB = new Locale("en", "GB");
         // 2006‑01‑02 is a Monday and is in week 1 of 2006 (ISO)
         Calendar cal = createCalendar(2006, 1, 2, enGB);
         assertFormatEquals("w", enGB, cal);
     }

     @Test
     public void testWeek53AnotherYear() {
         // 2009‑12‑31 is the last Thursday of 2009, giving week 53 (ISO)
         Locale sv = new Locale("sv", "SE");
         Calendar cal = createCalendar(2009, 12, 31, sv);
         assertFormatEquals("w", sv, cal);
     }

     // ------------------- Consistency / re‑use -------------------------

     @Test
     public void testFastDateFormatInstanceReuse() {
         Locale sv = new Locale("sv", "SE");
         FastDateFormat fdf = FastDateFormat.getInstance("w-MMMM-yyyy", UTC, sv);
         Calendar cal1 = createCalendar(2005, 12, 31, sv);
         Calendar cal2 = createCalendar(2006, 1, 2, sv);
         String res1 = fdf.format(cal1);
         String res2 = fdf.format(cal2);
         // Sanity: both strings are non‑empty and different
         assertNotNull(res1);
         assertNotNull(res2);
         assertFalse(res1.equals(res2));
     }

     // ------------------- Invalid inputs -------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testNullPatternThrows() {
         FastDateFormat.getInstance(null, UTC, Locale.US);
     }

     // ------------------- Week‑in‑month (regression protection) --------

     @Test
     public void testWeekInMonthNotAffected() {
         // The "W" token should work correctly; verify with known value
         Locale enUS = Locale.US;
         Calendar cal = createCalendar(2005, 12, 31, enUS);
         // December 31, 2005 is the 5th Saturday → week in month = 5
         FastDateFormat fdf = FastDateFormat.getInstance("W", UTC, enUS);
         assertEquals("5", fdf.format(cal));
     }
 }
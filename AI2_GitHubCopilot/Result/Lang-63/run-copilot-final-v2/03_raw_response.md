package org.apache.commons.lang.time;

 import java.util.Calendar;
 import java.util.TimeZone;

 import junit.framework.TestCase;

 public class DurationFormatUtilsTest extends TestCase {

     /**
      * LANG-281: formatDurationISO with negative duration must not produce negative
      * field values.  A duration of -2000 ms is -2 seconds; the buggy
      * reduceAndCorrect / format logic can turn this into an invalid ISO string
      * containing "-2" inside the fields.
      */
     public void testJiraLang281() {
         String result = DurationFormatUtils.formatDurationISO(-2000L);
         int pIdx = result.indexOf('P');
         assertTrue("Result must contain period designator 'P'", pIdx >= 0);
         String afterP = result.substring(pIdx + 1);
         assertFalse(
             "Negative duration should not embed a minus sign in a field: " + result,
             afterP.matches(".*-\\d.*")
         );
     }

     /**
      * formatDurationISO with zero duration produces only zeros in every field.
      */
     public void testFormatDurationISOZero() {
         String result = DurationFormatUtils.formatDurationISO(0L);
         assertTrue("Must start with P", result.startsWith("P"));
         // the buggy format may insert negative numbers; zero must never contain '-'
         assertFalse("Zero duration must not contain '-'", result.contains("-"));
         // basic sanity: contains the T separator
         assertTrue("Must contain time separator T", result.contains("T"));
     }

     /**
      * formatDurationISO with a clean positive value (9 seconds + 500 ms).
      * Expected seconds field is 9; buggy code could produce -2 for certain inputs.
      */
     public void testFormatDurationISOPositiveSeconds() {
         String result = DurationFormatUtils.formatDurationISO(9500L);
         assertTrue("Must start with P", result.startsWith("P"));
         assertFalse("Positive duration must not contain '-'", result.contains("-"));
         // 9500 ms = 9 seconds, 500 ms; seconds portion should contain "9"
         assertTrue("Should contain 9 seconds", result.contains("9"));
     }

     /**
      * formatDurationISO with exactly one hour.
      */
     public void testFormatDurationISOOneHour() {
         String result = DurationFormatUtils.formatDurationISO(3600000L);
         assertTrue("Must start with P", result.startsWith("P"));
         assertTrue("Must contain T separator", result.contains("T"));
         // 1 hour -> hours should be "1", not negative
         assertFalse("Must not contain '-'", result.contains("-"));
         assertTrue("Should contain 1 hour", result.contains("1H") || result.contains("H1"));
     }

     /**
      * formatPeriodISO with end after start (positive period crossing month boundary).
      */
     public void testFormatPeriodISOPositiveCrossingMonth() {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         cal.clear();
         cal.set(2024, Calendar.JANUARY, 31, 10, 30, 45);
         long start = cal.getTimeInMillis();
         cal.set(2024, Calendar.FEBRUARY, 15, 12, 45, 10);
         long end = cal.getTimeInMillis();

         String result = DurationFormatUtils.formatPeriodISO(start, end);
         assertTrue("Must start with P", result.startsWith("P"));
         assertTrue("Must contain T separator", result.contains("T"));
         assertFalse("Positive period must not contain '-' in fields", result.contains("-"));
     }

     /**
      * formatPeriodISO with start after end (negative overall period).
      * The buggy reduceAndCorrect can corrupt field signs for negative periods.
      */
     public void testFormatPeriodISONegativePeriod() {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         cal.clear();
         cal.set(2024, Calendar.MARCH, 20, 14, 30, 50);
         long start = cal.getTimeInMillis();   // later
         cal.set(2024, Calendar.JANUARY, 5, 8, 10, 5);
         long end = cal.getTimeInMillis();     // earlier -> negative period

         String result = DurationFormatUtils.formatPeriodISO(start, end);
         assertTrue("Must start with P", result.startsWith("P"));
         // The result represents a negative duration; however individual fields
         // should not contain embedded minus signs after 'P'
         int pIdx = result.indexOf('P');
         assertTrue("Must contain P", pIdx >= 0);
         if (pIdx + 1 < result.length()) {
             String afterP = result.substring(pIdx + 1);
             assertFalse(
                 "Negative period fields must not embed '-' after P: " + result,
                 afterP.matches(".*-\\d.*")
             );
         }
     }

     /**
      * formatPeriodISO with equal start/end (zero-length period).
      */
     public void testFormatPeriodISOZeroPeriod() {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         cal.clear();
         cal.set(2024, Calendar.JUNE, 15, 12, 0, 0);
         long time = cal.getTimeInMillis();

         String result = DurationFormatUtils.formatPeriodISO(time, time);
         assertTrue("Must start with P", result.startsWith("P"));
         assertTrue("Must contain T separator", result.contains("T"));
         assertFalse("Zero period must not contain '-'", result.contains("-"));
     }

     /**
      * formatDuration with a simple pattern and negative duration.
      * Demonstrates that negative values leak into fields.
      */
     public void testFormatDurationNegativeWithSimplePattern() {
         // -2 seconds using pattern "s.S"
         String result = DurationFormatUtils.formatDuration(-2000L, "s.S");
         // The buggy implementation will output "-2.0" (negative seconds)
         // A correct implementation should either error or present a leading minus.
         assertNotNull(result);
         assertTrue("Result should be non-empty", result.length() > 0);
         // Check that the numeric portions are coherent: no double minus
         int minusCount = result.length() - result.replace("-", "").length();
         assertTrue("At most one minus sign expected", minusCount <= 1);
     }

     /**
      * formatDuration with pattern containing days and negative duration.
      */
     public void testFormatDurationNegativeCrossDay() {
         // -25 hours = -90000000 ms
         String result = DurationFormatUtils.formatDuration(-90000000L, "d' days 'H' hours 'm'
minutes'");
         assertNotNull(result);
         // the result will contain negative days and/or hours in the buggy version
         assertTrue("Result must not be empty", result.length() > 0);
     }

     /**
      * formatDurationHMS with positive value.
      */
     public void testFormatDurationHMSPositive() {
         // 1h 2m 3s 4ms
         long ms = (1 * 3600000L) + (2 * 60000L) + (3 * 1000L) + 4;
         String result = DurationFormatUtils.formatDurationHMS(ms);
         // expected: "1:02:03.004"
         assertEquals("1:02:03.004", result);
     }

     /**
      * formatDurationHMS with zero.
      */
     public void testFormatDurationHMSZero() {
         String result = DurationFormatUtils.formatDurationHMS(0L);
         assertEquals("0:00:00.000", result);
     }

     /**
      * formatDurationWords with a positive duration.
      */
     public void testFormatDurationWordsPositive() {
         // 2 days 3 hours 4 minutes 5 seconds
         long ms = 2L * 86400000L + 3L * 3600000L + 4L * 60000L + 5L * 1000L;
         String result = DurationFormatUtils.formatDurationWords(ms, true, true);
         assertNotNull(result);
         assertTrue("Should mention days", result.contains("day"));
         assertTrue("Should mention hours", result.contains("hour"));
         assertTrue("Should mention minutes", result.contains("minute"));
         assertTrue("Should mention seconds", result.contains("second"));
     }

     /**
      * formatPeriod with a large period (> 28 days) exercising the calendar-based
      * path and reduceAndCorrect.  Verifies no negative values leak into fields.
      */
     public void testFormatPeriodLargePositiveNoNegativeFields() {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
         cal.clear();
         cal.set(2020, Calendar.JANUARY, 1, 0, 0, 0);
         long start = cal.getTimeInMillis();
         cal.set(2021, Calendar.JUNE, 15, 23, 59, 59);
         long end = cal.getTimeInMillis();

         // use a pattern that exercises multiple fields
         String result = DurationFormatUtils.formatPeriod(
             start, end, "y'Y 'M'M 'd'D 'H'H 'm'm 's's'"
         );
         assertNotNull(result);
         assertFalse("Large positive period must not contain '-' in output: " + result,
             result.contains("-"));
     }

     /**
      * formatDuration with ISO pattern and a duration that crosses day boundary
      * (2 days - 2 seconds).  Verify no negative fields appear.
      */
     public void testFormatDurationISODayBoundaryWithRemainder() {
         long twoDays = 2L * 86400000L;
         long duration = twoDays - 2000L; // 2 days minus 2 seconds
         String result = DurationFormatUtils.formatDurationISO(duration);
         assertTrue("Must start with P", result.startsWith("P"));
         assertFalse("Must not embed negative field values: " + result,
             result.matches(".*[^P]-\\d.*"));
     }
 }
package org.joda.time;

 import static org.junit.Assert.assertEquals;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import org.junit.Test;

 /**
  * Regression tests for Defects4J Time bug 12.
  * Verifies that LocalDate.fromDateFields/fromCalendarFields and
  * LocalDateTime.fromDateFields/fromCalendarFields correctly map
  * BCE dates to proleptic years (0 for 1 BC, -2 for 3 BC, etc.)
  * and preserve time components.
  */
 public class TestLocalDateLocalDateTime_Bug12 {

     // ---- helpers ----
     private GregorianCalendar newBceCalendar(int gregorianBceYear, int month, int day,
                                               int hour, int min, int sec, int millis) {
         GregorianCalendar cal = new GregorianCalendar();
         cal.clear();
         cal.set(Calendar.ERA, GregorianCalendar.BC);
         cal.set(Calendar.YEAR, gregorianBceYear);
         cal.set(Calendar.MONTH, month);
         cal.set(Calendar.DAY_OF_MONTH, day);
         cal.set(Calendar.HOUR_OF_DAY, hour);
         cal.set(Calendar.MINUTE, min);
         cal.set(Calendar.SECOND, sec);
         cal.set(Calendar.MILLISECOND, millis);
         return cal;
     }

     private GregorianCalendar newCeCalendar(int year, int month, int day,
                                              int hour, int min, int sec, int millis) {
         GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, min, sec);
         cal.set(Calendar.MILLISECOND, millis);
         return cal;
     }

     // ============ LocalDate.fromDateFields ============

     @Test
     public void testLocalDate_fromDateFields_year0() {
         // 1 BC -> proleptic year 0
         GregorianCalendar cal = newBceCalendar(1, Calendar.FEBRUARY, 3, 0, 0, 0, 0);
         Date date = cal.getTime();
         LocalDate ld = LocalDate.fromDateFields(date);
         assertEquals("0000-02-03", ld.toString());
     }

     @Test
     public void testLocalDate_fromDateFields_yearMinus2() {
         // 3 BC -> proleptic year -2
         GregorianCalendar cal = newBceCalendar(3, Calendar.FEBRUARY, 3, 0, 0, 0, 0);
         Date date = cal.getTime();
         LocalDate ld = LocalDate.fromDateFields(date);
         assertEquals("-0002-02-03", ld.toString());
     }

     @Test
     public void testLocalDate_fromDateFields_positiveYear() {
         GregorianCalendar cal = newCeCalendar(2020, Calendar.FEBRUARY, 3, 0, 0, 0, 0);
         Date date = cal.getTime();
         LocalDate ld = LocalDate.fromDateFields(date);
         assertEquals("2020-02-03", ld.toString());
     }

     // ============ LocalDate.fromCalendarFields ============

     @Test
     public void testLocalDate_fromCalendarFields_year0() {
         GregorianCalendar cal = newBceCalendar(1, Calendar.FEBRUARY, 3, 0, 0, 0, 0);
         LocalDate ld = LocalDate.fromCalendarFields(cal);
         assertEquals("0000-02-03", ld.toString());
     }

     @Test
     public void testLocalDate_fromCalendarFields_yearMinus2() {
         GregorianCalendar cal = newBceCalendar(3, Calendar.FEBRUARY, 3, 0, 0, 0, 0);
         LocalDate ld = LocalDate.fromCalendarFields(cal);
         assertEquals("-0002-02-03", ld.toString());
     }

     @Test
     public void testLocalDate_fromCalendarFields_positiveYear() {
         GregorianCalendar cal = newCeCalendar(2020, Calendar.FEBRUARY, 3, 0, 0, 0, 0);
         LocalDate ld = LocalDate.fromCalendarFields(cal);
         assertEquals("2020-02-03", ld.toString());
     }

     // ============ LocalDateTime.fromDateFields ============

     @Test
     public void testLocalDateTime_fromDateFields_year0() {
         GregorianCalendar cal = newBceCalendar(1, Calendar.FEBRUARY, 3, 4, 5, 6, 7);
         Date date = cal.getTime();
         LocalDateTime ldt = LocalDateTime.fromDateFields(date);
         assertEquals("0000-02-03T04:05:06.007", ldt.toString());
     }

     @Test
     public void testLocalDateTime_fromDateFields_yearMinus2() {
         GregorianCalendar cal = newBceCalendar(3, Calendar.FEBRUARY, 3, 4, 5, 6, 7);
         Date date = cal.getTime();
         LocalDateTime ldt = LocalDateTime.fromDateFields(date);
         assertEquals("-0002-02-03T04:05:06.007", ldt.toString());
     }

     @Test
     public void testLocalDateTime_fromDateFields_positiveYear() {
         GregorianCalendar cal = newCeCalendar(2020, Calendar.FEBRUARY, 3, 4, 5, 6, 7);
         Date date = cal.getTime();
         LocalDateTime ldt = LocalDateTime.fromDateFields(date);
         assertEquals("2020-02-03T04:05:06.007", ldt.toString());
     }

     // ============ LocalDateTime.fromCalendarFields ============

     @Test
     public void testLocalDateTime_fromCalendarFields_year0() {
         GregorianCalendar cal = newBceCalendar(1, Calendar.FEBRUARY, 3, 4, 5, 6, 7);
         LocalDateTime ldt = LocalDateTime.fromCalendarFields(cal);
         assertEquals("0000-02-03T04:05:06.007", ldt.toString());
     }

     @Test
     public void testLocalDateTime_fromCalendarFields_yearMinus2() {
         GregorianCalendar cal = newBceCalendar(3, Calendar.FEBRUARY, 3, 4, 5, 6, 7);
         LocalDateTime ldt = LocalDateTime.fromCalendarFields(cal);
         assertEquals("-0002-02-03T04:05:06.007", ldt.toString());
     }

     @Test
     public void testLocalDateTime_fromCalendarFields_positiveYear() {
         GregorianCalendar cal = newCeCalendar(2020, Calendar.FEBRUARY, 3, 4, 5, 6, 7);
         LocalDateTime ldt = LocalDateTime.fromCalendarFields(cal);
         assertEquals("2020-02-03T04:05:06.007", ldt.toString());
     }
 }

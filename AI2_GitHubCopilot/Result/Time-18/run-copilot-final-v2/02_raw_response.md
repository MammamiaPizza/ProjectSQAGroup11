package org.joda.time.chrono;

 import org.joda.time.Chronology;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeField;
 import org.joda.time.DateTimeZone;
 import org.joda.time.IllegalFieldValueException;
 import org.joda.time.ReadablePartial;
 import org.joda.time.YearMonth;
 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 /**
  * Tests for bug 130 / Time-18b in {@link GJChronology}.
  *
  * The bug causes an IllegalFieldValueException when constructing a
  * GJChronology with a cutover after a leap year and then requesting
  * the maximum value of day-of-month for February of that leap year.
  * The underlying issue is that CutoverField.getMaximumValue(ReadablePartial)
  * does not correctly account for leap years when the cutover falls after
  * the leap day.
  */
 public class TestGJChronologyBug18 {

     private static final DateTimeZone UTC = DateTimeZone.UTC;
     private static final int MIN_DAYS = 4; // ISO standard

     // --- Helper ----------------------------------------------------------------

     private GJChronology chronologyWithCutover(DateTime cutover) {
         return GJChronology.getInstance(UTC, cutover, MIN_DAYS);
     }

     // --- Basic leap-year construction tests -------------------------------------

     @Test
     public void testFeb29LeapYearCutoverAfter() {
         // Cutover on 2001-01-01. Year 2000 is entirely before the cutover (Julian side),
         // but both Julian and Gregorian calendars treat 2000 as a leap year.
         // February 2000 must have a maximum day-of-month of 29.
         GJChronology chrono = chronologyWithCutover(new DateTime(2001, 1, 1, 0, 0, UTC));
         DateTimeField dayOfMonth = chrono.dayOfMonth();

         // getMaximumValue(long) with a representative instant in Feb 2000
         long feb2000Millis = chrono.getDateTimeMillis(2000, 2, 1, 0);
         assertEquals("Feb 2000 max (by instant)", 29, dayOfMonth.getMaximumValue(feb2000Millis));

         // getMaximumValue(ReadablePartial) with year and month
         ReadablePartial partial = new YearMonth(2000, 2);
         assertEquals("Feb 2000 max (by partial)", 29, dayOfMonth.getMaximumValue(partial));

         // Construction of Feb 29 itself must succeed
         long feb29Millis = chrono.getDateTimeMillis(2000, 2, 29, 0);
         assertEquals("Feb 29 was not built correctly", 29, chrono.dayOfMonth().get(feb29Millis));
     }

     @Test
     public void testFeb28NonLeapYearCutoverAfter() {
         // 2001 is not a leap year; maximal day-of-month for February is 28.
         GJChronology chrono = chronologyWithCutover(new DateTime(2002, 1, 1, 0, 0, UTC));
         DateTimeField dayOfMonth = chrono.dayOfMonth();

         ReadablePartial partial = new YearMonth(2001, 2);
         assertEquals(28, dayOfMonth.getMaximumValue(partial));
     }

     @Test
     public void testFeb28NonLeapYearGregorianSide() {
         // 1900 is a leap year in Julian but NOT in Gregorian.
         // With a cutover before 1900, the year is on the Gregorian side.
         GJChronology chrono = chronologyWithCutover(new DateTime(1800, 1, 1, 0, 0, UTC));
         DateTimeField dayOfMonth = chrono.dayOfMonth();

         ReadablePartial partial = new YearMonth(1900, 2);
         assertEquals("Gregorian 1900 is not a leap year", 28, dayOfMonth.getMaximumValue(partial));
     }

     @Test
     public void testFeb29LeapYearJulianSide() {
         // 1900 is a leap year in Julian. With a cutover after 1900, it is on the Julian side.
         GJChronology chrono = chronologyWithCutover(new DateTime(2000, 1, 1, 0, 0, UTC));
         DateTimeField dayOfMonth = chrono.dayOfMonth();

         ReadablePartial partial = new YearMonth(1900, 2);
         assertEquals("Julian 1900 is a leap year", 29, dayOfMonth.getMaximumValue(partial));
     }

     // --- Invalid values / exception branches -----------------------------------

     @Test(expected = IllegalFieldValueException.class)
     public void testDayOfMonthBeyondMax() {
         // February 2000 (leap) only allows 1..29
         GJChronology chrono = chronologyWithCutover(new DateTime(2001, 1, 1, 0, 0, UTC));
         // Should throw when trying to set day 30
         chrono.getDateTimeMillis(2000, 2, 30, 0);
     }

     @Test
     public void testInvalidDayOfMonthThrows() {
         GJChronology chrono = chronologyWithCutover(new DateTime(2001, 1, 1, 0, 0, UTC));
         DateTimeField dayOfMonth = chrono.dayOfMonth();
         long feb2000 = chrono.getDateTimeMillis(2000, 2, 1, 0);
         try {
             dayOfMonth.set(feb2000, 30);
             fail("Expected IllegalFieldValueException");
         } catch (IllegalFieldValueException expected) {
             assertTrue(expected.getMessage().contains("30"));
         }
     }

     // --- Cutover boundary tests ------------------------------------------------

     @Test
     public void testCutoverOnLeapDay() {
         // Cutover is exactly February 29, 2000.
         // The cutover instant is Gregorian, so Feb 29 exists on both sides.
         DateTime cutover = new DateTime(2000, 2, 29, 0, 0, UTC);
         GJChronology chrono = GJChronology.getInstance(UTC, cutover, MIN_DAYS);
         DateTimeField dayOfMonth = chrono.dayOfMonth();

         // The day-of-month field must report 29 as valid for Feb 2000.
         ReadablePartial partial = new YearMonth(2000, 2);
         assertEquals("Feb 2000 at cutover should allow 29", 29,
dayOfMonth.getMaximumValue(partial));

         // Building Feb 29, 2000 must not throw.
         long feb29 = chrono.getDateTimeMillis(2000, 2, 29, 0);
         assertEquals(29, chrono.dayOfMonth().get(feb29));
     }

     @Test
     public void testDefaultCutoverLeapYear() {
         // Default cutover is 1582-10-15. Year 2000 is far after cutover (Gregorian side).
         GJChronology chrono = GJChronology.getInstanceUTC();
         DateTimeField dayOfMonth = chrono.dayOfMonth();
         ReadablePartial partial = new YearMonth(2000, 2);
         assertEquals(29, dayOfMonth.getMaximumValue(partial));
     }

     // --- isLeap verifications --------------------------------------------------

     @Test
     public void testIsLeapWithCutoverAfterLeapYear() {
         GJChronology chrono = chronologyWithCutover(new DateTime(2001, 1, 1, 0, 0, UTC));
         long millisJan2000 = chrono.getDateTimeMillis(2000, 1, 1, 0);
         long millisJan2001 = chrono.getDateTimeMillis(2001, 1, 1, 0);

         assertTrue("2000 is leap", chrono.year().isLeap(millisJan2000));
         assertFalse("2001 is not leap", chrono.year().isLeap(millisJan2001));
     }

     @Test
     public void testJulianLeapWithCutoverAfter1900() {
         // 1900 is leap in Julian, not in Gregorian. This year is on Julian side.
         GJChronology chrono = chronologyWithCutover(new DateTime(2000, 1, 1, 0, 0, UTC));
         long millis1900 = chrono.getDateTimeMillis(1900, 1, 1, 0);
         assertTrue("Julian 1900 is leap", chrono.year().isLeap(millis1900));
     }

     @Test
     public void testGregorianNonLeapWithCutoverBefore1900() {
         // 1900 is not leap in Gregorian. Year is on Gregorian side.
         GJChronology chrono = chronologyWithCutover(new DateTime(1800, 1, 1, 0, 0, UTC));
         long millis1900 = chrono.getDateTimeMillis(1900, 1, 1, 0);
         assertFalse("Gregorian 1900 is not leap", chrono.year().isLeap(millis1900));
     }
 }
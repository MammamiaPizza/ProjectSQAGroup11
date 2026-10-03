package org.joda.time.chrono;

 import org.joda.time.DateTimeFieldType;
 import org.joda.time.IllegalFieldValueException;
 import org.joda.time.MonthDay;
 import org.joda.time.chrono.gj.GJChronology;
 import org.junit.Before;
 import org.junit.Test;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 /**
  * Tests for BasicMonthOfYearDateTimeField.add(ReadablePartial, int, int[], int) —
  * the month arithmetic on MonthDay partials that validates day-of-month.
  *
  * Bug 151 (Time-14): adding/subtracting months from a leap-day (Feb 29)
  * incorrectly throws IllegalFieldValueException or produces wrong result
  * when the target month is not February.
  */
 public class TestBasicMonthOfYearDateTimeFieldAddPartial {

     private GJChronology chrono;

     @Before
     public void setUp() {
         chrono = GJChronology.getInstanceUTC();
     }

     // --- Helper to get MonthDay as [month, day] values array ---
     private int[] monthDayValues(int month, int day) {
         return new int[] {month, day};
     }

     // --- Helper to invoke add on the month field of a MonthDay ---
     private MonthDay addMonthsToMonthDay(int month, int day, int valueToAdd) {
         int[] values = monthDayValues(month, day);
         int[] result = chrono.monthOfYear().add(
                 MonthDay.ZERO,  // partial shape; actual values come from values array
                 0,              // fieldIndex for month
                 values,
                 valueToAdd);
         return new MonthDay(result[0], result[1]);
     }

     // ========================================================================
     // CORE BUG REPRODUCTIONS: Feb 29 ± N months where target month has >= 29 days
     // ========================================================================

     /**
      * Bug: plusMonths(-1) from 2020-02-29 should yield 2020-01-29, not throw.
      * Target January has 31 days, so 29 is valid.
      */
     @Test
     public void testFeb29_minus1Month_toJanuary() {
         MonthDay result = addMonthsToMonthDay(2, 29, -1);
         assertEquals(new MonthDay(1, 29), result);
     }

     /**
      * Bug: plusMonths(1) from 2020-02-29 should yield 2020-03-29.
      * Target March has 31 days, so 29 is valid.
      */
     @Test
     public void testFeb29_plus1Month_toMarch() {
         MonthDay result = addMonthsToMonthDay(2, 29, 1);
         assertEquals(new MonthDay(3, 29), result);
     }

     /**
      * Bug: plusMonths(-2) from 2020-02-29 should yield 2019-12-29.
      */
     @Test
     public void testFeb29_minus2Months_toDecember() {
         MonthDay result = addMonthsToMonthDay(2, 29, -2);
         assertEquals(new MonthDay(12, 29), result);
     }

     /**
      * Bug: plusMonths(-14) from 2020-02-29 should yield 2019-01-29.
      * Crossing year boundary with negative offset, target January has 31 days.
      */
     @Test
     public void testFeb29_minus14Months_crossesYear() {
         MonthDay result = addMonthsToMonthDay(2, 29, -14);
         assertEquals(new MonthDay(12, 29), result); // 2020-02 minus 14 = 2018-12
     }

     // ========================================================================
     // END-OF-MONTH ADJUSTMENT: last-day-of-month → last-day-of-target-month
     // ========================================================================

     /**
      * From a leap Feb 29 (last day of month), adding 12 months goes to a non-leap Feb.
      * The 29th is not valid, so it should clamp to Feb 28.
      */
     @Test
     public void testFeb29_plus12Months_toNonLeapFeb_clampsTo28() {
         MonthDay result = addMonthsToMonthDay(2, 29, 12);
         assertEquals(new MonthDay(2, 28), result);
     }

     /**
      * Jan 31 + 1 month → Feb 28 (non-leap target, end-of-month adjustment).
      */
     @Test
     public void testJan31_plus1Month_clampsToFeb28() {
         MonthDay result = addMonthsToMonthDay(1, 31, 1);
         assertEquals(new MonthDay(2, 28), result);
     }

     /**
      * Jan 31 + 1 month with a leap-year offset (adding 60 months to cross leap years).
      * The end-of-month clamping should still work.
      */
     @Test
     public void testEndOfMonth_adjustsToLastDayOfTarget() {
         // Aug 31 + 1 month → Sep 30
         MonthDay result = addMonthsToMonthDay(8, 31, 1);
         assertEquals(new MonthDay(9, 30), result);
     }

     // ========================================================================
     // DAY BOUNDARY CROSSING VIA MINUS_DAYS / PLUS_DAYS THROUGH MONTH ADD
     // ========================================================================

     /**
      * Indirectly test via month arithmetic: adding -1 month to March 29
      * should yield Feb 28 (non-leap clamping).
      */
     @Test
     public void testMar29_minus1Month_clampsToFeb28() {
         MonthDay result = addMonthsToMonthDay(3, 29, -1);
         assertEquals(new MonthDay(2, 28), result);
     }

     /**
      * March 31 - 1 month → Feb 28 (end-of-month adjustment from 31-in-March to Feb).
      */
     @Test
     public void testMar31_minus1Month_clampsToFeb28() {
         MonthDay result = addMonthsToMonthDay(3, 31, -1);
         assertEquals(new MonthDay(2, 28), result);
     }

     // ========================================================================
     // ZERO AND LARGE OFFSETS
     // ========================================================================

     /**
      * Adding zero months should return the identical values array.
      */
     @Test
     public void testAddZeroMonths_noChange() {
         MonthDay result = addMonthsToMonthDay(2, 29, 0);
         assertEquals(new MonthDay(2, 29), result);
     }

     /**
      * Large offset: Feb 28 + 48 months → 4 years later, still Feb 28.
      */
     @Test
     public void testFeb28_plus48Months_sameDay() {
         MonthDay result = addMonthsToMonthDay(2, 28, 48);
         assertEquals(new MonthDay(2, 28), result);
     }

     /**
      * Large negative offset: Dec 31 - 36 months → Dec 31 three years earlier.
      */
     @Test
     public void testDec31_minus36Months_sameDay() {
         MonthDay result = addMonthsToMonthDay(12, 31, -36);
         assertEquals(new MonthDay(12, 31), result);
     }
 }
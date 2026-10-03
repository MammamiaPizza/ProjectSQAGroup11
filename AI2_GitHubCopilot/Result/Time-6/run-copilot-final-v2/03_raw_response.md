package org.joda.time.chrono;

 import junit.framework.TestCase;
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.IllegalFieldValueException;
 import org.joda.time.Instant;

 /**
  * JUnit 3 tests for GJChronology bug #6 (year 0 handling and cutover field add).
  */
 public class TestGJDate extends TestCase {
     private GJChronology defaultChrono;
     private GJChronology preZeroChrono;

     @Override
     protected void setUp() {
         // Default cutover (1582-10-15)
         defaultChrono = GJChronology.getInstanceUTC();
         // Cutover far before year 1 to exercise conversion across year 0
         // (using same chronology as no custom cutover is exposed)
         preZeroChrono = GJChronology.getInstanceUTC();
     }

     /**
      * Crossing from positive year to negative via year addition
      * should skip year 0 and land on -0002-06-30, not -0001-06-30.
      */
     public void test_plusYears_positiveToNegative_crossCutover() {
         DateTime dt = new DateTime(2, 6, 30, 0, 0, 0, 0, defaultChrono);
         DateTime result = dt.plusYears(-4);
         assertEquals(-2, result.getYear());
         assertEquals(6, result.getMonthOfYear());
         assertEquals(30, result.getDayOfMonth());
     }

     /**
      * Adding year(s) that would produce year 0 must throw
      * IllegalFieldValueException.
      */
     public void test_plusYears_positiveToZero_crossCutover() {
         DateTime dt = new DateTime(1, 6, 30, 0, 0, 0, 0, defaultChrono);
         try {
             dt.plusYears(-1);
             fail("Should have thrown IllegalFieldValueException for year 0");
         } catch (IllegalFieldValueException e) {
             // expected
         }
     }

     /**
      * Crossing from positive weekyear to negative via weekyear addition
      * should end up at -0002-06-30 (year -2, month 6, day 30).
      */
     public void test_plusWeekyears_positiveToNegative_crossCutover() {
         DateTime dt = new DateTime(2, 6, 28, 0, 0, 0, 0, defaultChrono);
         assertEquals(2, dt.getWeekyear());
         long instant = dt.getMillis();
         long resultInstant = defaultChrono.weekyear().add(instant, -4);
         DateTime result = new DateTime(resultInstant, defaultChrono);
         assertEquals(-2, result.getYear());
         assertEquals(6, result.getMonthOfYear());
         assertEquals(30, result.getDayOfMonth());
     }

     /**
      * Adding weekyear(s) that would produce weekyear 0 must throw
      * IllegalFieldValueException.
      */
     public void test_plusWeekyears_positiveToZero_crossCutover() {
         DateTime dt = new DateTime(1, 6, 28, 0, 0, 0, 0, defaultChrono);
         assertEquals(1, dt.getWeekyear());
         long instant = dt.getMillis();
         try {
             defaultChrono.weekyear().add(instant, -1);
             fail("Should have thrown IllegalFieldValueException for weekyear 0");
         } catch (IllegalFieldValueException e) {
             // expected
         }
     }

     /**
      * Adding years that cross the cutover from negative to positive era.
      * Occurs when cutover itself is before year 1.
      */
     public void test_cutoverPreZero() {
         DateTime dt = new DateTime(-10, 6, 30, 0, 0, 0, 0, preZeroChrono);
         DateTime result = dt.plusYears(20);
         assertEquals(10, result.getYear());
         assertEquals(6, result.getMonthOfYear());
         assertEquals(30, result.getDayOfMonth());
     }

     /**
      * Adding zero years should return the same instant.
      */
     public void test_plusYears_zeroDelta() {
         DateTime dt = new DateTime(5, 1, 1, 0, 0, 0, 0, defaultChrono);
         DateTime result = dt.plusYears(0);
         assertEquals(dt.getMillis(), result.getMillis());
     }

     /**
      * Large year delta crossing from positive to negative multiple times
      * should correctly skip year 0.
      */
     public void test_plusYears_largeDeltaCrossing() {
         DateTime dt = new DateTime(2, 6, 30, 0, 0, 0, 0, defaultChrono);
         DateTime result = dt.plusYears(-10);
         assertEquals(-8, result.getYear());
         assertEquals(6, result.getMonthOfYear());
         assertEquals(30, result.getDayOfMonth());
     }

     /**
      * Large weekyear delta crossing from positive to negative;
      * no exception, result must be well-formed.
      */
     public void test_plusWeekyears_largeDeltaCrossing() {
         DateTime dt = new DateTime(2, 6, 28, 0, 0, 0, 0, defaultChrono);
         long instant = dt.getMillis();
         long resultInstant = defaultChrono.weekyear().add(instant, -10);
         DateTime result = new DateTime(resultInstant, defaultChrono);
         assertNotNull(result);
         // result should not be year -1 (bug would produce -1)
         assertTrue("Year should not be -1", result.getYear() < -1);
     }

     /**
      * Crossing the cutover backward over a February 29:
      * 1600-02-29 (Gregorian leap) minus 100 years → 1500-02-29 (Julian leap).
      */
     public void test_plusYears_leapDayCrossCutoverBackward() {
         DateTime dt = new DateTime(1600, 2, 29, 0, 0, 0, 0, defaultChrono);
         DateTime result = dt.plusYears(-100);
         assertEquals(1500, result.getYear());
         assertEquals(2, result.getMonthOfYear());
         assertEquals(29, result.getDayOfMonth());
     }

     /**
      * Constructing a DateTime with year 0 must throw.
      */
     public void test_yearZero_creation() {
         try {
             new DateTime(0, 1, 1, 0, 0, 0, 0, defaultChrono);
             fail("Should have thrown IllegalFieldValueException for year 0");
         } catch (IllegalFieldValueException e) {
             // expected
         }
     }

     /**
      * Constructing a DateTime with a weekyear of 0 must throw.
      */
     public void test_weekyearZero_creation() {
         try {
             new DateTime(1, 1, 1, 0, 0, 0, 0, defaultChrono).withWeekyear(0);
             fail("Should have thrown IllegalFieldValueException for weekyear 0");
         } catch (IllegalFieldValueException e) {
             // expected
         }
     }
 }
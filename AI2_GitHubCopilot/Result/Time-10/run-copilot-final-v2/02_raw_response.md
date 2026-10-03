import junit.framework.TestCase;
 import junit.framework.TestSuite;
 import org.joda.time.Days;
 import org.joda.time.IllegalFieldValueException;
 import org.joda.time.MonthDay;
 import org.joda.time.Months;
 import org.joda.time.TimeOfDay;
 import org.joda.time.YearMonth;

 /**
  * Tests for BaseSingleFieldPeriod.between(ReadablePartial,ReadablePartial,ReadablePeriod)
  * Bug: MonthDay(2,29) throws IllegalFieldValueException because default year is not leap.
  */
 public class TestBaseSingleFieldPeriodBetweenBug10 extends TestCase {

     public static TestSuite suite() {
         return new TestSuite(TestBaseSingleFieldPeriodBetweenBug10.class);
     }

     // Days.between with MonthDay(2,29) to MonthDay(3,1) – the original trigger
     public void testDaysBetween_feb29_to_mar1() {
         int result = Days.daysBetween(new MonthDay(2, 29), new MonthDay(3, 1)).getDays();
         assertEquals(1, result);
     }

     // Days.between with MonthDay(2,28) to MonthDay(2,29)
     public void testDaysBetween_feb28_to_feb29() {
         int result = Days.daysBetween(new MonthDay(2, 28), new MonthDay(2, 29)).getDays();
         assertEquals(1, result);
     }

     // Days.between with MonthDay(2,28) to MonthDay(3,1) in a non-leap year (1970)
     public void testDaysBetween_feb28_to_mar1() {
         int result = Days.daysBetween(new MonthDay(2, 28), new MonthDay(3, 1)).getDays();
         assertEquals(1, result);
     }

     // Days.between with a January transition
     public void testDaysBetween_jan31_to_feb2() {
         int result = Days.daysBetween(new MonthDay(1, 31), new MonthDay(2, 2)).getDays();
         assertEquals(2, result);
     }

     // Months.between with MonthDay(2,29) to MonthDay(3,1) – the second trigger
     public void testMonthsBetween_feb29_to_mar1() {
         int result = Months.monthsBetween(new MonthDay(2, 29), new MonthDay(3, 1)).getMonths();
         assertEquals(0, result);
     }

     // Months.between with MonthDay(2,28) to MonthDay(2,29)
     public void testMonthsBetween_feb28_to_feb29() {
         int result = Months.monthsBetween(new MonthDay(2, 28), new MonthDay(2, 29)).getMonths();
         assertEquals(0, result);
     }

     // Months.between with a month boundary that does not complete a month
     public void testMonthsBetween_jan31_to_feb1() {
         int result = Months.monthsBetween(new MonthDay(1, 31), new MonthDay(2, 1)).getMonths();
         assertEquals(0, result);
     }

     // Months.between with a full 4-month difference
     public void testMonthsBetween_jan1_to_may1() {
         int result = Months.monthsBetween(new MonthDay(1, 1), new MonthDay(5, 1)).getMonths();
         assertEquals(4, result);
     }

     // Null start must throw IllegalArgumentException
     public void testBetween_nullStart() {
         try {
             Days.daysBetween(null, new MonthDay(1, 1));
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException expected) {
         }
     }

     // Null end must throw IllegalArgumentException
     public void testBetween_nullEnd() {
         try {
             Days.daysBetween(new MonthDay(1, 1), null);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException expected) {
         }
     }

     // Different partial sizes must throw IllegalArgumentException
     public void testBetween_differentSize() {
         try {
             Days.daysBetween(new MonthDay(1, 1), new TimeOfDay(10, 30));
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException expected) {
         }
     }

     // Different field types (same size) must throw IllegalArgumentException
     public void testBetween_differentFieldTypes() {
         try {
             Days.daysBetween(new MonthDay(1, 1), new YearMonth(2020, 1));
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException expected) {
         }
     }

     // An invalid day (Feb 30) should still throw IllegalFieldValueException
     public void testDaysBetween_feb30_invalid() {
         try {
             Days.daysBetween(new MonthDay(2, 30), new MonthDay(3, 1));
             fail("Expected IllegalFieldValueException");
         } catch (IllegalFieldValueException expected) {
         }
     }
 }
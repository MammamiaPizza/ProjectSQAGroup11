import junit.framework.TestCase;
 import org.joda.time.*;

 public class TestPeriodNormalizedStandardBug5 extends TestCase {

     // --- months type ---

     public void testNormalizedStandard_monthsType_withYearAndMonth() {
         Period p = Period.years(1).withMonths(1);
         Period result = p.normalizedStandard(PeriodType.months());
         assertEquals("Type should be months", PeriodType.months(), result.getPeriodType());
         assertEquals("Years should be 0", 0, result.getYears());
         assertEquals("Months should be 13", 13, result.getMonths());
         assertEquals("getWeeks should be 0", 0, result.getWeeks());
         assertEquals("Duration should match (approx)", p.toStandardDuration().getMillis(),
                 result.toStandardDuration().getMillis());
     }

     public void testNormalizedStandard_monthsType_zeroPeriod() {
         Period result = Period.ZERO.normalizedStandard(PeriodType.months());
         assertEquals("Type should be months", PeriodType.months(), result.getPeriodType());
         assertEquals("Months should be 0", 0, result.getMonths());
         assertEquals("Years should be 0", 0, result.getYears());
     }

     public void testNormalizedStandard_monthsType_negativePeriod() {
         Period p = Period.years(-1).withMonths(-1);
         Period result = p.normalizedStandard(PeriodType.months());
         assertEquals("Type should be months", PeriodType.months(), result.getPeriodType());
         assertEquals("Months should be -13", -13, result.getMonths());
         assertEquals("Years should be 0", 0, result.getYears());
         assertTrue("Duration should be negative", result.toStandardDuration().getMillis() < 0);
     }

     public void testNormalizedStandard_monthsType_onlyYears() {
         Period p = Period.years(2);
         Period result = p.normalizedStandard(PeriodType.months());
         assertEquals("Months should be 24", 24, result.getMonths());
         assertEquals("Years should be 0", 0, result.getYears());
     }

     // --- weeks type ---

     public void testNormalizedStandard_weeksType_withYear() {
         Period p = Period.years(1);
         Period result = p.normalizedStandard(PeriodType.weeks());
         assertEquals("Type should be weeks", PeriodType.weeks(), result.getPeriodType());
         assertEquals("Weeks should be 52", 52, result.getWeeks());
         assertEquals("Days should be 0", 0, result.getDays());
         assertEquals("Duration should match (approx)", p.toStandardDuration().getMillis(),
                 result.toStandardDuration().getMillis());
     }

     public void testNormalizedStandard_weeksType_withMonthAndWeeks() {
         Period p = Period.months(1).withWeeks(3);
         Period result = p.normalizedStandard(PeriodType.weeks());
         assertEquals("Type should be weeks", PeriodType.weeks(), result.getPeriodType());
         int expectedWeeks = 7;
         assertEquals("Weeks", expectedWeeks, result.getWeeks());
     }

     public void testNormalizedStandard_weeksType_zeroPeriod() {
         Period result = Period.ZERO.normalizedStandard(PeriodType.weeks());
         assertEquals("Type should be weeks", PeriodType.weeks(), result.getPeriodType());
         assertEquals("Weeks should be 0", 0, result.getWeeks());
     }

     // --- monthsWeeks type (custom) ---

     public void testNormalizedStandard_monthsWeeksType_withMonthAndWeeks() {
         PeriodType type = PeriodType.forFields(new DurationFieldType[]{
             DurationFieldType.months(), DurationFieldType.weeks()
         });
         Period p = Period.months(1).withWeeks(3);
         Period result = p.normalizedStandard(type);
         assertEquals("Type should be custom monthsWeeks", type, result.getPeriodType());
         assertEquals("Months should be 1", 1, result.getMonths());
         assertEquals("Weeks should be 3", 3, result.getWeeks());
         assertEquals("Years should be 0", 0, result.getYears());
         assertEquals("Days should be 0", 0, result.getDays());
     }

     public void testNormalizedStandard_monthsWeeksType_withYearAndMonth() {
         PeriodType type = PeriodType.forFields(new DurationFieldType[]{
             DurationFieldType.months(), DurationFieldType.weeks()
         });
         Period p = Period.years(1).withMonths(1);
         Period result = p.normalizedStandard(type);
         assertEquals("Type should be custom monthsWeeks", type, result.getPeriodType());
         assertEquals("Months should be 13", 13, result.getMonths());
         assertEquals("Weeks should be 0", 0, result.getWeeks());
     }

     // --- edge cases and exception handling ---

     public void testNormalizedStandard_nullType_throws() {
         try {
             Period.ZERO.normalizedStandard(null);
             fail("Expected IllegalArgumentException for null type");
         } catch (IllegalArgumentException e) {
             // expected
         }
     }

     public void testNormalizedStandard_retainsPrecision() {
         Period p = new Period(0, 0, 0, 1, 23, 59, 59, 999);
         Period result = p.normalizedStandard(PeriodType.standard());
         assertEquals("Standard type", PeriodType.standard(), result.getPeriodType());
         assertEquals(0, result.getYears());
         assertEquals(0, result.getMonths());
         assertEquals(0, result.getWeeks());
         assertEquals(1, result.getDays());
         assertEquals(23, result.getHours());
         assertEquals(59, result.getMinutes());
         assertEquals(59, result.getSeconds());
         assertEquals(999, result.getMillis());
     }

 }

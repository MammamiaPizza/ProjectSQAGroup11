package org.joda.time.format;

 import org.joda.time.Period;
 import org.junit.Test;

 import static org.junit.Assert.assertEquals;

 public class TestPeriodFormatterBuilderBug13 {

     private PeriodFormatter isoWithMillis() {
         return new PeriodFormatterBuilder()
                 .appendPrefix("PT")
                 .appendSecondsWithMillis()
                 .appendSuffix("S")
                 .toFormatter();
     }

     private PeriodFormatter isoWithOptionalMillis() {
         return new PeriodFormatterBuilder()
                 .appendPrefix("PT")
                 .appendSecondsWithOptionalMillis()
                 .appendSuffix("S")
                 .toFormatter();
     }

     private PeriodFormatter simpleSeconds() {
         return new PeriodFormatterBuilder()
                 .appendSeconds()
                 .appendSuffix("S")
                 .toFormatter();
     }

     @Test
     public void testNegativeMillisOnly() {
         Period p = Period.millis(-8);
         assertEquals("PT-0.008S", isoWithMillis().print(p));
     }

     @Test
     public void testNegativeSecondsAndMillis() {
         Period p = Period.millis(-1008);
         assertEquals("PT-1.008S", isoWithMillis().print(p));
     }

     @Test
     public void testZeroPeriod() {
         Period p = Period.ZERO;
         assertEquals("PT0.000S", isoWithMillis().print(p));
     }

     @Test
     public void testPositiveMillis() {
         Period p = Period.millis(8);
         assertEquals("PT0.008S", isoWithMillis().print(p));
     }

     @Test
     public void testNegativeLargeSeconds() {
         Period p = Period.seconds(-12345);
         assertEquals("PT-12345.000S", isoWithMillis().print(p));
     }

     @Test
     public void testNegativeOptionalMillis() {
         Period p = Period.millis(-1);
         assertEquals("PT-0.001S", isoWithOptionalMillis().print(p));
     }

     @Test
     public void testNegativeSecondsOnly() {
         Period p = Period.seconds(-5);
         assertEquals("-5S", simpleSeconds().print(p));
     }

     @Test
     public void testNegativeMinutesAndSeconds() {
         PeriodFormatter f = new PeriodFormatterBuilder()
                 .appendPrefix("P")
                 .appendMinutes()
                 .appendSuffix("M")
                 .appendSeparator("T")
                 .appendSecondsWithMillis()
                 .appendSuffix("S")
                 .toFormatter();
         Period p = Period.minutes(-2).withSeconds(-3).withMillis(-500);
         assertEquals("P-2MT-3.500S", f.print(p));
     }

     @Test
     public void testNegativeMillisOnlyNoPrefix() {
         PeriodFormatter f = new PeriodFormatterBuilder()
                 .appendSecondsWithMillis()
                 .appendSuffix("s")
                 .toFormatter();
         Period p = Period.millis(-8);
         assertEquals("-0.008s", f.print(p));
     }

     @Test
     public void testNegativeWithCustomPrefix() {
         PeriodFormatter f = new PeriodFormatterBuilder()
                 .appendPrefix("Time:")
                 .appendSecondsWithMillis()
                 .appendSuffix("s")
                 .toFormatter();
         Period p = Period.millis(-8);
         assertEquals("Time:-0.008s", f.print(p));
     }

     @Test
     public void testNegativeMillis3Digit() {
         PeriodFormatter f = new PeriodFormatterBuilder()
                 .appendMillis3Digit()
                 .appendSuffix("ms")
                 .toFormatter();
         Period p = Period.millis(-8);
         assertEquals("-008ms", f.print(p));
     }

     @Test
     public void testNegativeSecondsWithMillisNegativeZeroSeconds() {
         Period p = new Period(0, 0, 0, 0, 0, 0, 0, -1);
         assertEquals("PT-0.001S", isoWithMillis().print(p));
     }

 }

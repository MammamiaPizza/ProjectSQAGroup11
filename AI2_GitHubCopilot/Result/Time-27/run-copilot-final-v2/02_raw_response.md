package org.joda.time.format;

 import org.joda.time.Period;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link PeriodFormatterBuilder#appendSeconds()} focusing on the bug where
  * long-digit second values (more than 9 digits) cause an IllegalArgumentException.
  * The bug is triggered by parsing "PT1003199059S" (10 digits) with the default
  * maximumParsedDigits of 10. According to the specification, seconds can be any
  * integer value that fits in an int (up to 2,147,483,647), so a 10-digit number
  * that is <= Integer.MAX_VALUE must be parseable.
  *
  * @see <a
href="]8;id=md-1q02g9g;http://sourceforge.net/tracker/?func=detail&aid=2495455&group_id=97367&atid=617889http://sourceforge.net/tracker/?func=detail&aid=2495455&group_id=97367&atid=617889]8;;]8;;">Bug ]8;;
2495455</a>
  */
 public class TestPeriodFormatterBuilderBug27 {

     private PeriodFormatter secondsOnly() {
         return new PeriodFormatterBuilder()
                 .appendLiteral("PT")
                 .appendSeconds()
                 .appendLiteral("S")
                 .toFormatter();
     }

     private PeriodFormatter secondsOnly(int maxDigits) {
         return new PeriodFormatterBuilder()
                 .maximumParsedDigits(maxDigits)
                 .appendLiteral("PT")
                 .appendSeconds()
                 .appendLiteral("S")
                 .toFormatter();
     }

     @Test
     public void testParseSecondsOneDigit() {
         Period p = secondsOnly().parsePeriod("PT1S");
         assertEquals("seconds", 1, p.getSeconds());
     }

     @Test
     public void testParseSecondsZero() {
         Period p = secondsOnly().parsePeriod("PT0S");
         assertEquals("seconds", 0, p.getSeconds());
     }

     @Test
     public void testParseSecondsMax9Digits() {
         Period p = secondsOnly().parsePeriod("PT999999999S");
         assertEquals("seconds", 999999999, p.getSeconds());
     }

     /**
      * Reproducing the reported bug: parsing "PT1003199059S" (10 digits)
      * must succeed and return the correct number of seconds.
      */
     @Test
     public void testParseSeconds10DigitsBug2495455() {
         Period p = secondsOnly().parsePeriod("PT1003199059S");
         assertEquals("seconds", 1003199059, p.getSeconds());
     }

     @Test
     public void testParseSecondsMaxInt10Digits() {
         Period p = secondsOnly().parsePeriod("PT2147483647S");
         assertEquals("seconds", Integer.MAX_VALUE, p.getSeconds());
     }

     @Test
     public void testParseSeconds9DigitsNotAffected() {
         // ensure values of 9 digits are still parsed correctly
         Period p = secondsOnly().parsePeriod("PT123456789S");
         assertEquals("seconds", 123456789, p.getSeconds());
     }

     @Test
     public void testParseSecondsWithLargerMaxParsedDigits() {
         // explicitly setting a larger maxParsedDigits should definitely work
         PeriodFormatter fmt = secondsOnly(15);
         Period p = fmt.parsePeriod("PT1003199059S");
         assertEquals("seconds", 1003199059, p.getSeconds());
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseSecondsRejectsNegativeWhenConfigured() {
         PeriodFormatter fmt = new PeriodFormatterBuilder()
                 .rejectSignedValues(true)
                 .appendLiteral("PT")
                 .appendSeconds()
                 .appendLiteral("S")
                 .toFormatter();
         fmt.parsePeriod("PT-1S");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseSecondsMissingSuffix() {
         secondsOnly().parsePeriod("PT100");
     }

     @Test
     public void testParseSecondsMultipleCallsToFormatter() {
         PeriodFormatter fmt = secondsOnly();
         // parsing different valid values consecutively should work
         Period p1 = fmt.parsePeriod("PT1S");
         assertEquals("seconds", 1, p1.getSeconds());
         Period p2 = fmt.parsePeriod("PT999999S");
         assertEquals("seconds", 999999, p2.getSeconds());
         Period p3 = fmt.parsePeriod("PT0S");
         assertEquals("seconds", 0, p3.getSeconds());
     }
 }
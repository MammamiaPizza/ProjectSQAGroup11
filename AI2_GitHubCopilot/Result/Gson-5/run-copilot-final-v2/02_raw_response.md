package com.google.gson.internal.bind.util;

 import junit.framework.TestCase;

 import java.text.ParseException;
 import java.text.ParsePosition;
 import java.util.Date;
 import java.util.TimeZone;

 public class ISO8601UtilsTest extends TestCase {

     /**
      * Test the bug-triggering input: offset with hour only, no colon and no minutes.
      * The expected correct behaviour (ISO8601 spec) is to parse and return Unix epoch (0).
      * With the buggy version this will throw a ParseException.
      */
     public void testParseDateWithShortOffsetWithoutColon() throws ParseException {
         String input = "1970-01-01T01:00:00+01";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         assertNotNull(result);
         assertEquals(0L, result.getTime());
     }

     /**
      * Positive offset with hours and minutes (colon separated).
      * "1970-01-01T05:30:00+05:30" corresponds to midnight UTC (epoch 0).
      */
     public void testParseDateWithPositiveOffsetHoursAndMinutes() throws ParseException {
         String input = "1970-01-01T05:30:00+05:30";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         assertEquals(0L, result.getTime());
     }

     /**
      * Offset Z (UTC).
      */
     public void testParseDateWithZuluOffset() throws ParseException {
         String input = "1970-01-01T00:00:00Z";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         assertEquals(0L, result.getTime());
     }

     /**
      * Negative offset with hours and minutes.
      * "1970-01-01T00:00:00-08:00" corresponds to 1970-01-01T08:00:00Z.
      */
     public void testParseDateWithNegativeOffset() throws ParseException {
         String input = "1970-01-01T00:00:00-08:00";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         long expected = 8 * 3600 * 1000L; // +8 hours
         assertEquals(expected, result.getTime());
     }

     /**
      * Milliseconds with offset Z.
      */
     public void testParseDateWithMilliseconds() throws ParseException {
         String input = "1970-01-01T00:00:00.123Z";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         assertEquals(123L, result.getTime());
     }

     /**
      * Date only (no time component, no time zone).
      * The parse will use the local default timezone — we only assert that
      * it does not throw and returns a non‑null Date.
      */
     public void testParseDateOnlyNoTimeOrZone() throws ParseException {
         String input = "1970-01-01";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         assertNotNull(result);
     }

     /**
      * Invalid offset hour (e.g. +25:00) should cause the parse to fail.
      */
     public void testParseDateWithInvalidOffsetHour() {
         String input = "1970-01-01T00:00:00+25:00";
         try {
             ISO8601Utils.parse(input, new ParsePosition(0));
             fail("Expected ParseException");
         } catch (ParseException e) {
             // expected
         }
     }

     /**
      * Offset without colon (+hhmm format, basic ISO8601) is accepted.
      */
     public void testParseDateWithOffsetBasicFormat() throws ParseException {
         String input = "1970-01-01T01:30:00+0130";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         // Local 01:30:00 +0130 -> 00:00:00 UTC -> epoch 0
         assertEquals(0L, result.getTime());
     }

     /**
      * Trailing characters after a valid timezone offset should not break parsing,
      * and the parse position must stop after the timezone indicator.
      */
     public void testParseDateWithTrailingCharacters() throws ParseException {
         String input = "1970-01-01T00:00:00Z garbage";
         ParsePosition pos = new ParsePosition(0);
         Date result = ISO8601Utils.parse(input, pos);
         assertNotNull(result);
         // After consuming date, time and 'Z' the index should stop right after 'Z' (position 20).
         assertEquals(20, pos.getIndex());
     }

     /**
      * Time without any timezone indicator should be rejected.
      */
     public void testParseDateWithNoTimezoneIndicator() {
         String input = "1970-01-01T00:00:00";
         try {
             ISO8601Utils.parse(input, new ParsePosition(0));
             fail("Expected ParseException");
         } catch (ParseException e) {
             // expected
         }
     }

     /**
      * Invalid month should cause the parse to fail (Calendar strict mode).
      */
     public void testParseDateWithInvalidMonth() {
         String input = "1970-13-01T00:00:00Z";
         try {
             ISO8601Utils.parse(input, new ParsePosition(0));
             fail("Expected ParseException");
         } catch (ParseException e) {
             // expected
         }
     }

     /**
      * Leap second (60) is accepted and silently clamped to 59.
      */
     public void testParseDateWithLeapSecond() throws ParseException {
         String input = "1970-01-01T00:00:60Z";
         Date result = ISO8601Utils.parse(input, new ParsePosition(0));
         // seconds=60 clamped to 59, so the instant is epoch + 59 seconds
         assertEquals(59 * 1000L, result.getTime());
     }

 }
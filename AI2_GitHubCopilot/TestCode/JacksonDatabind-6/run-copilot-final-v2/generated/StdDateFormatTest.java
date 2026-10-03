package com.fasterxml.jackson.databind.util;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;

 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.TimeZone;

 import org.junit.Before;
 import org.junit.Test;

 public class StdDateFormatTest {

     private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

     private StdDateFormat df;

     @Before
     public void setUp() {
         df = new StdDateFormat(GMT);
     }

     // Helper: create a Date at given UTC time
     private static Date utcDate(int year, int month, int day,
                                 int hour, int minute, int second, int millis) {
         Calendar cal = new GregorianCalendar(GMT);
         cal.set(Calendar.MILLISECOND, 0);
         cal.set(year, month - 1, day, hour, minute, second);
         cal.set(Calendar.MILLISECOND, millis);
         return cal.getTime();
     }

     @Test
     public void testMissingSecondsPositiveOffset() throws Exception {
         Date parsed = df.parse("1997-07-16T19:20+01:00");
         Date expected = utcDate(1997, 7, 16, 18, 20, 0, 0);
         assertEquals(expected, parsed);
    }

     @Test
     public void testMissingSecondsZ() throws Exception {
         Date parsed = df.parse("1997-07-16T19:20Z");
         Date expected = utcDate(1997, 7, 16, 19, 20, 0, 0);
         assertEquals(expected, parsed);
     }

     @Test
     public void testMissingSecondsNegativeOffset() throws Exception {
         Date parsed = df.parse("1997-07-16T19:20-05:00");
         // UTC = 19:20 + 5:00 = 00:20 next day
         Date expected = utcDate(1997, 7, 17, 0, 20, 0, 0);
         assertEquals(expected, parsed);
     }

     @Test
     public void testPartialMillisOneDigit() throws Exception {
         Date parsed = df.parse("2014-10-03T18:00:00.6-05:00");
         // UTC = 18:00 + 5:00 = 23:00, millis .6 sec = 600 ms
         Date expected = utcDate(2014, 10, 3, 23, 0, 0, 600);
         assertEquals(expected, parsed);
     }

     @Test
     public void testPartialMillisTwoDigits() throws Exception {
         Date parsed = df.parse("2014-10-03T18:00:00.60-05:00");
         Date expected = utcDate(2014, 10, 3, 23, 0, 0, 600);
         assertEquals(expected, parsed);
     }

     @Test
     public void testPartialMillisThreeDigits() throws Exception {
         Date parsed = df.parse("2014-10-03T18:00:00.600-05:00");
         Date expected = utcDate(2014, 10, 3, 23, 0, 0, 600);
         assertEquals(expected, parsed);
     }

     @Test
     public void testFullPrecisionWithOffset() throws Exception {
         Date parsed = df.parse("1997-07-16T19:20:30.123+01:00");
         Date expected = utcDate(1997, 7, 16, 18, 20, 30, 123);
         assertEquals(expected, parsed);
     }

     @Test
     public void testDateOnly() throws Exception {
         Date parsed = df.parse("2020-01-01");
         Date expected = utcDate(2020, 1, 1, 0, 0, 0, 0);
         assertEquals(expected, parsed);
     }

     @Test
     public void testRFC1123() throws Exception {
         Date parsed = df.parse("Wed, 02 Oct 2002 15:00:00 +0200");
         Date expected = utcDate(2002, 10, 2, 13, 0, 0, 0);
         assertEquals(expected, parsed);
     }

     @Test(expected = ParseException.class)
     public void testMissingTimezoneInIsoThrows() throws Exception {
         df.parse("1997-07-16T19:20:30");
     }

     @Test(expected = NullPointerException.class)
     public void testNullThrows() throws Exception {
         df.parse(null);
     }

     @Test
     public void testInvalidInputsThrowParseException() {
         String[] badInputs = {
             "",
             "invalid",
             "1997-07-16T19:20:00.600+01:00extra",
             "1997-07-16 19:20:30",
             "1997-07"
         };
         for (String s : badInputs) {
             try {
                 df.parse(s);
                 fail("Expected ParseException for input: " + s);
             } catch (ParseException e) {
                 // expected
             }
         }
     }
 }

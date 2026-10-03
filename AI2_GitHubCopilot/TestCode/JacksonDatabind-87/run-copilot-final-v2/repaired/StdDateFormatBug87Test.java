package com.fasterxml.jackson.databind.util;

 import static org.junit.Assert.*;

 import java.text.ParseException;
 import java.text.ParsePosition;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.TimeZone;

 import org.junit.Test;

 public class StdDateFormatBug87Test {

     // Oracle: no-timezone ISO8601 string must be interpreted as UTC, i.e.,
     // yielding the same Date as the same string with 'Z' appended.
     // This must hold regardless of the StdDateFormat's configured timezone.
     // The following helpers compute an expected Date for a local date-time
     // interpreted in the given time zone.

     private static Date localDateTimeInTz(String yyyymmddThhmmss, TimeZone tz) {
         String[] parts = yyyymmddThhmmss.split("-|T|:");
         int y = Integer.parseInt(parts[0]);
         int m = Integer.parseInt(parts[1]) - 1; // Calendar.JANUARY=0
         int d = Integer.parseInt(parts[2]);
         int hh = Integer.parseInt(parts[3]);
         int mm = Integer.parseInt(parts[4]);
         int ss = (parts.length > 5) ? Integer.parseInt(parts[5]) : 0;
         Calendar cal = Calendar.getInstance(tz);
         cal.clear();
         cal.set(y, m, d, hh, mm, ss);
         cal.set(Calendar.MILLISECOND, 0);
         return cal.getTime();    }


     @Test
     public void testParseNoTimezoneWithUtcMatchesZ() throws Exception {
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
         Date d1 = fmt.parse("1970-01-01T00:00:00");
         Date d2 = fmt.parse("1970-01-01T00:00:00Z");
         assertEquals("UTC-configured parse must equal Z-form", d2, d1);
         assertEquals("epoch 0", 0L, d1.getTime());
     }

     @Test
     public void testParseNoTimezoneWithNonDefaultTimezoneStillUtc() throws Exception {
         // Non-default timezone must NOT leak into parsing of no-timezone strings
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("GMT-8:00"));
         Date d = fmt.parse("1970-01-01T00:00:00");
         Date z = fmt.parse("1970-01-01T00:00:00Z");
         assertEquals("no-timezone string is treated as UTC even with non-UTC timezone", z, d);
         assertEquals("epoch 0", 0L, d.getTime());
     }

     @Test
     public void testParseWithExplicitOffset() throws Exception {
         // explicit offset in string takes precedence, not instance timezone
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
         Date d = fmt.parse("1970-01-01T00:00:00+01:00");
         // expected: 1970-01-01T00:00:00+01:00 ↔ -01:00 from UTC → -3600000 ms
         assertEquals(-3600000L, d.getTime());
     }

     @Test
     public void testParseDateOnlyMidnightUtc() throws Exception {
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
         Date dt = fmt.parse("1970-01-01");
         assertEquals(0L, dt.getTime());
     }

     @Test
     public void testParseWithZAlwaysUtc() throws Exception {
         // Z means UTC even when the formatter's timezone differs
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("GMT+05:00"));
         Date dt = fmt.parse("1970-01-01T00:00:00Z");        assertEquals(0L, dt.getTime());
     }

     @Test
     public void testParsePlainIntegerTimestamp() throws Exception {
         StdDateFormat fmt = new StdDateFormat();
         assertEquals(0L, fmt.parse("0").getTime());
         assertEquals(150000L, fmt.parse("150000").getTime());
         // negative
         assertEquals(-60000L, fmt.parse("-60000").getTime());
     }

     @Test
     public void testParseEmptyStringThrows() {
         try {
             Date d = new StdDateFormat().parse("");
             assertNull("Empty string should not parse to a valid date", d);
         } catch (ParseException e) {
             // expected and acceptable
         }
     }

     @Test(expected = NullPointerException.class)
     public void testParseNullThrows() throws Exception {
         new StdDateFormat().parse(null);
     }

     @Test(expected = ParseException.class)
     public void testParseMalformedThrows() throws Exception {
         new StdDateFormat().parse("abc-def-ghi");
     }

     @Test
     public void testParseWhitespaceTrimmed() throws Exception {
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
         Date d = fmt.parse("  1970-01-01T00:00:00Z  ");        assertEquals(0L, d.getTime());
     }

     @Test
     public void testSetTimeZoneDoesNotAffectNoTimezoneParse() throws Exception {
         // Regression: setTimeZone must not cause no-timezone strings to be parsed
         // with the new timezone; they must remain UTC.
         StdDateFormat fmt = new StdDateFormat();
         fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
         // First parse with Z to confirm format is working
         assertNotNull(fmt.parse("1970-01-01T00:00:00Z"));
         // Change timezone
         fmt.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
         Date d = fmt.parse("1970-01-01T00:00:00");
         assertEquals("no-timezone parse must still be UTC aftersetTimeZone", 0L, d.getTime());
     }

     @Test
     public void testWithTimeZoneCreatesNewInstance() throws Exception {
         StdDateFormat orig = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
         StdDateFormat copy = orig.withTimeZone(TimeZone.getTimeZone("GMT-8:00"));
         assertNotSame(orig, copy);
         // parse no-timezone on both; both must return UTC
         Date d1 = orig.parse("1970-01-01T00:00:00");
         Date d2 = copy.parse("1970-01-01T00:00:00");
         assertEquals("UTC", 0L, d1.getTime());
         assertEquals("PST", 0L, d2.getTime());
     }

     @Test
     public void testParseBoundaryYears() throws Exception {
         StdDateFormat fmt = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
         Date d1 = fmt.parse("0001-01-01T00:00:00Z");
         assertNotNull(d1);
         assertTrue(d1.getTime() < 0L);
         Date d2 = fmt.parse("9999-12-31T23:59:59Z");
         assertNotNull(d2);
         assertTrue(d2.getTime() > 0L);
     }
 }

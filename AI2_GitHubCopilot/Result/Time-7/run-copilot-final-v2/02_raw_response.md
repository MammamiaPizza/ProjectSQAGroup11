package org.joda.time.format;

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.IllegalFieldValueException;
import junit.framework.TestCase;

public class TestDateTimeFormatterBug7 extends TestCase {

 private DateTimeFormatter formatter;

 protected void setUp() {
     formatter = DateTimeFormat.forPattern("M d");
 }

 public void testParseInto_feb29_leapYearUTC_success() {
     MutableDateTime instant = new MutableDateTime(2000, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     int pos = formatter.parseInto(instant, "2 29", 0);
     assertEquals(4, pos);
     assertEquals(2, instant.getMonthOfYear());
     assertEquals(29, instant.getDayOfMonth());
 }

 public void testParseInto_feb29_leapYearNewYork_success() {
     DateTimeZone zone = DateTimeZone.forID("America/New_York");
     MutableDateTime instant = new MutableDateTime(2004, 12, 31, 23, 59, 59, 999, zone);
     int pos = formatter.parseInto(instant, "2 29", 0);
     assertEquals(4, pos);
     assertEquals(2, instant.getMonthOfYear());
     assertEquals(29, instant.getDayOfMonth());
 }

 public void testParseInto_feb29_leapYearTokyo_success() {
     DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
     MutableDateTime instant = new MutableDateTime(2004, 12, 31, 23, 59, 59, 999, zone);
     int pos = formatter.parseInto(instant, "2 29", 0);
     assertEquals(4, pos);
     assertEquals(2, instant.getMonthOfYear());
     assertEquals(29, instant.getDayOfMonth());
 }

 public void testParseInto_feb29_nonLeapYear_exception() {
     MutableDateTime instant = new MutableDateTime(2001, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     try {
         formatter.parseInto(instant, "2 29", 0);
         fail("Expected IllegalFieldValueException");
     } catch (IllegalFieldValueException e) {
         assertTrue(e.getMessage().contains("29"));
     }
 }

 public void testParseInto_feb29_nonLeapCenturyYear_exception() {
     MutableDateTime instant = new MutableDateTime(2100, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     try {
         formatter.parseInto(instant, "2 29", 0);
         fail("Expected IllegalFieldValueException");
     } catch (IllegalFieldValueException e) {
         assertTrue(e.getMessage().contains("29"));
     }
 }

 public void testParseInto_feb28_anyYear_success() {
     MutableDateTime instant = new MutableDateTime(2001, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     int pos = formatter.parseInto(instant, "2 28", 0);
     assertEquals(4, pos);
     assertEquals(2, instant.getMonthOfYear());
     assertEquals(28, instant.getDayOfMonth());
 }

 public void testParseInto_invalidDay30_exception() {
     MutableDateTime instant = new MutableDateTime(2000, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     try {
         formatter.parseInto(instant, "2 30", 0);
         fail("Expected IllegalFieldValueException");
     } catch (IllegalFieldValueException e) {
         assertTrue(e.getMessage().contains("30"));
     }
 }

 public void testParseInto_invalidDay0_exception() {
     MutableDateTime instant = new MutableDateTime(2000, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     try {
         formatter.parseInto(instant, "2 0", 0);
         fail("Expected IllegalFieldValueException");
     } catch (IllegalFieldValueException e) {
         // expected
     }
 }

 public void testParseInto_invalidMonth_exception() {
     MutableDateTime instant = new MutableDateTime(2000, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     try {
         formatter.parseInto(instant, "13 1", 0);
         fail("Expected IllegalFieldValueException");
     } catch (IllegalFieldValueException e) {
         assertTrue(e.getMessage().contains("13"));
     }
 }

 public void testParseInto_feb29_yearZero_leapYear_success() {
     MutableDateTime instant = new MutableDateTime(0, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
     int pos = formatter.parseInto(instant, "2 29", 0);
     assertEquals(4, pos);
     assertEquals(2, instant.getMonthOfYear());
     assertEquals(29, instant.getDayOfMonth());
 }

}
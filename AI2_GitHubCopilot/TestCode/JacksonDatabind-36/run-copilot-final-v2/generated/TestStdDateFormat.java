package com.fasterxml.jackson.databind;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.databind.util.StdDateFormat;

public class TestStdDateFormat {

 // Helper to create StdDateFormat with explicit lenient flag
 private static StdDateFormat newStdDateFormat(TimeZone tz, Locale loc, Boolean lenient) throws
Exception {
     Constructor<StdDateFormat> ctor = StdDateFormat.class.getDeclaredConstructor(
             TimeZone.class, Locale.class, Boolean.class);
     ctor.setAccessible(true);
     return ctor.newInstance(tz, loc, lenient);
 }

 @Test
 public void testDefaultLenient() {
     assertTrue(StdDateFormat.instance.isLenient());
 }

 @Test
 public void testSetTimeZoneAndParseISO8601() throws Exception {
     StdDateFormat df = new StdDateFormat();
     df.setTimeZone(TimeZone.getTimeZone("GMT-8:00"));
     Date d = df.parse("2016-06-15T12:00:00.000-0800");
     assertNotNull(d);
 }

 @Test
 public void testParseISO8601WithZ() throws Exception {
     StdDateFormat df = new StdDateFormat();
     Date d = df.parse("2016-06-15T12:00:00.000Z");
     assertNotNull(d);
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
     cal.setTime(d);
     assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
 }

 @Test
 public void testParseRFC1123() throws Exception {
     StdDateFormat df = new StdDateFormat();
     Date d = df.parse("Wed, 15 Jun 2016 12:00:00 GMT");
     assertNotNull(d);
 }

 @Test
 public void testParsePlainDate() throws Exception {
     StdDateFormat df = new StdDateFormat();
     Date d = df.parse("2016-06-15");
     assertNotNull(d);
 }

 @Test
 public void testParseTimestamp() throws Exception {
     StdDateFormat df = new StdDateFormat();
     Date d = df.parse("0");
     assertNotNull(d);
     assertEquals(0L, d.getTime());
 }

 @Test
 public void testParseNegativeTimestamp() throws Exception {
     StdDateFormat df = new StdDateFormat();
     Date d = df.parse("-1");
     assertNotNull(d);
     assertEquals(-1L, d.getTime());
 }

 @Test(expected = ParseException.class)
 public void testParseEmptyString() throws Exception {
     StdDateFormat.instance.parse("");
 }

 @Test(expected = NullPointerException.class)
 public void testParseNullString() throws Exception {
     StdDateFormat.instance.parse(null);
 }

 @Test
 public void testCloneRetainsLenientFalse() throws Exception {
     StdDateFormat df = newStdDateFormat(TimeZone.getTimeZone("UTC"),
             Locale.getDefault(), Boolean.FALSE);
     StdDateFormat clone = df.clone();
     assertFalse(clone.isLenient());
 }

 @Test
 public void testWithTimeZonePreservesLenient() throws Exception {
     StdDateFormat df = newStdDateFormat(TimeZone.getTimeZone("UTC"),
             Locale.getDefault(), Boolean.FALSE);
     StdDateFormat df2 = df.withTimeZone(TimeZone.getTimeZone("PST"));
     assertFalse(df2.isLenient());
     assertEquals("PST", df2.getTimeZone().getID());
 }

 @Test
 public void testLenientFalseRejectsInvalidDate() throws Exception {
     // Bug 803 / 36: lenient=false should cause invalid dates to be rejected,
     // but leads to NullPointerException in the buggy version
     StdDateFormat df = newStdDateFormat(TimeZone.getTimeZone("UTC"),
             Locale.getDefault(), Boolean.FALSE);
     // Try to parse an ISO8601 date that is invalid under strict parsing
     try {
         Date d = df.parse("2016-02-30"); // February 30th is invalid
         // If no exception, the lenient flag was ignored (still a bug)
         assertNull("Lenient=false should reject invalid date, but returned: " + d, d);
     } catch (ParseException e) {
         // expected
     }
 }

}

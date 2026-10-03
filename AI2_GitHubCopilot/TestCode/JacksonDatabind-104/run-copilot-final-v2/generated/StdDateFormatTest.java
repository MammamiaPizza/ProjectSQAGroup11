package com.fasterxml.jackson.databind.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;
import static org.junit.Assert.*;

public class StdDateFormatTest {

 private static final StdDateFormat DF =
         StdDateFormat.instance.withTimeZone(TimeZone.getTimeZone("UTC"));

 /**
  * Creates a Date for the given proleptic year (0 = 1 BC, -1 = 2 BC, …).
  */
 private static Date createDate(int prolepticYear,
                                int month, int day,
                                int hour, int minute, int second, int ms) {
     GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
     cal.clear();
     if (prolepticYear <= 0) {
         cal.set(Calendar.ERA, GregorianCalendar.BC);
         cal.set(Calendar.YEAR, 1 - prolepticYear);
     } else {
         cal.set(Calendar.ERA, GregorianCalendar.AD);
         cal.set(Calendar.YEAR, prolepticYear);
     }
     cal.set(Calendar.MONTH, month - 1);
     cal.set(Calendar.DAY_OF_MONTH, day);
     cal.set(Calendar.HOUR_OF_DAY, hour);
     cal.set(Calendar.MINUTE, minute);
     cal.set(Calendar.SECOND, second);
     cal.set(Calendar.MILLISECOND, ms);
     return cal.getTime();
 }

 /**
  * Extracts the proleptic year from a Date using UTC.
  */
 private static int getYear(Date date) {
     GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
     cal.setTime(date);
     if (cal.get(Calendar.ERA) == GregorianCalendar.BC) {
         return 1 - cal.get(Calendar.YEAR);
     }
     return cal.get(Calendar.YEAR);
 }

 @Test
 public void testFormatYear9999() {
     Date d = createDate(9999, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("9999-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatYear10000() {
     Date d = createDate(10000, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("+10000-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatYear10204() {
     Date d = createDate(10204, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("+10204-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatYear0() {
     Date d = createDate(0, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("+0000-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatYearNegative1() {
     Date d = createDate(-1, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("-0001-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatYearNegative9999() {
     Date d = createDate(-9999, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("-9999-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatYearNegative10000() {
     Date d = createDate(-10000, 1, 1, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("-10000-01-01T00:00:00.000"));
 }

 @Test
 public void testFormatVeryLargeYear() {
     Date d = createDate(123456, 7, 20, 0, 0, 0, 0);
     String formatted = DF.format(d);
     assertTrue(formatted.startsWith("+123456-07-20T00:00:00.000"));
 }

 @Test
 public void testParseYear10000() throws Exception {
     Date d = DF.parse("+10000-01-01T00:00:00.000+0000");
     assertEquals(10000, getYear(d));
 }

 @Test
 public void testParseYear0() throws Exception {
     Date d = DF.parse("+0000-01-01T00:00:00.000+0000");
     assertEquals(0, getYear(d));
 }

 @Test
 public void testParseYearNegative1() throws Exception {
     Date d = DF.parse("-0001-01-01T00:00:00.000+0000");
     assertEquals(-1, getYear(d));
 }

 @Test
 public void testParseYearNegative10000() throws Exception {
     Date d = DF.parse("-10000-01-01T00:00:00.000+0000");
     assertEquals(-10000, getYear(d));
 }

}

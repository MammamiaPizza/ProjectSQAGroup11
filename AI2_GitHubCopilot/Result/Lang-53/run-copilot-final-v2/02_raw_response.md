package org.apache.commons.lang.time;

import static org.junit.Assert.;
import org.junit.;
import java.util.*;

public class DateUtilsTest {
    private static TimeZone ORIGINAL_TZ;

 @BeforeClass
 public static void setUpClass() {
     ORIGINAL_TZ = TimeZone.getDefault();
     TimeZone.setDefault(DateUtils.UTC_TIME_ZONE);
 }

 @AfterClass
 public static void tearDownClass() {
     TimeZone.setDefault(ORIGINAL_TZ);
 }

 @Test
 public void testRoundMinuteDown() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 8, 20);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round(input, Calendar.MINUTE);
     Calendar expected = (Calendar) cal.clone();
     expected.set(Calendar.SECOND, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundMinuteUp() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 8, 40);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round(input, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 2, 8, 9, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundMinuteExactly30Seconds() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 8, 30);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round(input, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 2, 8, 9, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundMinuteExactMinute() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 9, 0);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round(input, Calendar.MINUTE);
     Calendar expected = (Calendar) cal.clone();
     expected.set(Calendar.SECOND, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundMinuteHourRollover() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 10, 59, 45);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round(input, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 2, 11, 0, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundMinuteDayRollover() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 23, 59, 50);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round(input, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 3, 0, 0, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testRoundMinuteNullDate() {
     DateUtils.round((Date) null, Calendar.MINUTE);
 }

 @Test
 public void testRoundObjectWithDate() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 8, 40);
     cal.set(Calendar.MILLISECOND, 0);
     Date input = cal.getTime();
     Date result = DateUtils.round((Object) input, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 2, 8, 9, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundObjectWithCalendar() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 8, 40);
     cal.set(Calendar.MILLISECOND, 0);
     Date result = DateUtils.round((Object) cal, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 2, 8, 9, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected.getTime(), result);
 }

 @Test
 public void testRoundCalendar() {
     Calendar cal = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     cal.set(2007, 6, 2, 8, 8, 40);
     cal.set(Calendar.MILLISECOND, 0);
     Calendar result = DateUtils.round(cal, Calendar.MINUTE);
     Calendar expected = Calendar.getInstance(DateUtils.UTC_TIME_ZONE);
     expected.set(2007, 6, 2, 8, 9, 0);
     expected.set(Calendar.MILLISECOND, 0);
     assertEquals(expected, result);
 }

}
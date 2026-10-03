package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilsLang346Test {

    private static final TimeZone UTC = TimeZone.getTimeZone("GMT");

    private Calendar calendar(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        Calendar calendar = new GregorianCalendar(UTC);
        calendar.clear();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar;
    }

    @Test
    public void testRoundCalendarMinuteRoundsUpToNextMinute() {
        Calendar input = calendar(2007, Calendar.JULY, 2, 8, 8, 30, 0);

        Calendar rounded = DateUtils.round(input, Calendar.MINUTE);

        assertEquals(calendar(2007, Calendar.JULY, 2, 8, 9, 0, 0).getTimeInMillis(),
                rounded.getTimeInMillis());
        assertEquals(8, input.get(Calendar.MINUTE));
        assertEquals(30, input.get(Calendar.SECOND));
    }

    @Test
    public void testRoundMinuteRoundsDownBelowHalfMinuteAndUpAtHalfMinute() {
        Calendar belowHalf = calendar(2007, Calendar.JULY, 2, 8, 8, 29, 999);
        Calendar atHalf = calendar(2007, Calendar.JULY, 2, 8, 8, 30, 0);

        Calendar roundedBelowHalf = DateUtils.round(belowHalf, Calendar.MINUTE);
        Calendar roundedAtHalf = DateUtils.round(atHalf, Calendar.MINUTE);

        assertEquals(calendar(2007, Calendar.JULY, 2, 8, 8, 0, 0).getTimeInMillis(),
                roundedBelowHalf.getTimeInMillis());
        assertEquals(calendar(2007, Calendar.JULY, 2, 8, 9, 0, 0).getTimeInMillis(),
                roundedAtHalf.getTimeInMillis());
    }

    @Test
    public void testRoundMinuteCarriesIntoNextHour() {
        Calendar input = calendar(2007, Calendar.JULY, 2, 8, 59, 30, 250);

        Calendar rounded = DateUtils.round(input, Calendar.MINUTE);

        assertEquals(calendar(2007, Calendar.JULY, 2, 9, 0, 0, 0).getTimeInMillis(),
                rounded.getTimeInMillis());
    }

    @Test
    public void testDateAndObjectRoundOverloadsMatchCalendarMinuteRounding() {
        Calendar input = calendar(2007, Calendar.JULY, 2, 8, 8, 45, 125);
        Date expected = calendar(2007, Calendar.JULY, 2, 8, 9, 0, 0).getTime();

        Date roundedDate = DateUtils.round(input.getTime(), Calendar.MINUTE);
        Date roundedObject = DateUtils.round((Object) input, Calendar.MINUTE);

        assertEquals(expected, roundedDate);
        assertEquals(expected, roundedObject);
    }

@Test
public void testAddMillisecondsHandlesNegativeAmountsWithoutMutatingInput() {
    java.util.Date input = new java.util.Date(123456789L);

    java.util.Date result = org.apache.commons.lang.time.DateUtils.addMilliseconds(input, -7);

    org.junit.Assert.assertEquals(123456782L, result.getTime());
    org.junit.Assert.assertEquals(123456789L, input.getTime());
}

@Test
public void testAddRejectsNullDate() {
    try {
        org.apache.commons.lang.time.DateUtils.add(null, java.util.Calendar.DAY_OF_MONTH, 1);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        org.junit.Assert.assertEquals("The date must not be null", expected.getMessage());
    }
}

@Test
public void testIsSameDayCalendarUsesCalendarLocalDateFields() {
    java.util.Calendar utc = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("GMT"));
    java.util.Calendar pacific = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("GMT-08:00"));
    utc.clear();
    pacific.clear();
    utc.set(2007, java.util.Calendar.JULY, 2, 8, 0, 0);
    pacific.set(2007, java.util.Calendar.JULY, 2, 8, 0, 0);

    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameDay(utc, pacific));
}
}

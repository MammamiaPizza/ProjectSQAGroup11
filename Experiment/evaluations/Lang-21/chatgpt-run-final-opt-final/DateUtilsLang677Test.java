package org.apache.commons.lang3.time;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilsLang677Test {

    private static GregorianCalendar calendar(TimeZone timeZone, int hourOfDay) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.clear();
        calendar.set(2010, Calendar.JUNE, 15, hourOfDay, 20, 30);
        calendar.set(Calendar.MILLISECOND, 456);
        return calendar;
    }

    private static void setLocalFields(Calendar calendar, int hourOfDay) {
        calendar.clear();
        calendar.set(2010, Calendar.JUNE, 15, hourOfDay, 20, 30);
        calendar.set(Calendar.MILLISECOND, 456);
    }

    @Test
    public void isSameLocalTimeReturnsTrueForEqualLocalFieldsInDifferentTimeZones() {
        Calendar utc = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar plusFive = calendar(TimeZone.getTimeZone("GMT+05:00"), 10);

        assertFalse(utc.getTimeInMillis() == plusFive.getTimeInMillis());
        assertTrue(DateUtils.isSameLocalTime(utc, plusFive));
    }

    @Test
    public void isSameLocalTimeDistinguishesMorningAndAfternoonWithSameHourField() {
        Calendar morning = calendar(TimeZone.getTimeZone("GMT"), 1);
        Calendar afternoon = calendar(TimeZone.getTimeZone("GMT"), 13);

        assertFalse(DateUtils.isSameLocalTime(morning, afternoon));
    }

    @Test
    public void isSameLocalTimeReturnsFalseForSameInstantWithDifferentLocalHours() {
        Calendar utc = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar plusFive = new GregorianCalendar(TimeZone.getTimeZone("GMT+05:00"));
        plusFive.setTimeInMillis(utc.getTimeInMillis());

        assertFalse(DateUtils.isSameLocalTime(utc, plusFive));
    }

    @Test
    public void isSameLocalTimeReturnsFalseWhenMillisecondsDiffer() {
        Calendar first = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar second = calendar(TimeZone.getTimeZone("GMT"), 10);
        second.set(Calendar.MILLISECOND, 457);

        assertFalse(DateUtils.isSameLocalTime(first, second));
    }

    @Test
    public void isSameLocalTimeReturnsFalseWhenDateDiffers() {
        Calendar first = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar second = calendar(TimeZone.getTimeZone("GMT"), 10);
        second.add(Calendar.DAY_OF_MONTH, 1);

        assertFalse(DateUtils.isSameLocalTime(first, second));
    }

    @Test
    public void isSameLocalTimeRequiresSameCalendarImplementation() {
        Calendar standard = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar alternate = new AlternateGregorianCalendar(TimeZone.getTimeZone("GMT"));
        setLocalFields(alternate, 10);

        assertFalse(DateUtils.isSameLocalTime(standard, alternate));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameLocalTimeRejectsNullFirstCalendar() {
        DateUtils.isSameLocalTime(null, calendar(TimeZone.getTimeZone("GMT"), 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameLocalTimeRejectsNullSecondCalendar() {
        DateUtils.isSameLocalTime(calendar(TimeZone.getTimeZone("GMT"), 10), null);
    }

    private static final class AlternateGregorianCalendar extends GregorianCalendar {
        private static final long serialVersionUID = 1L;

        private AlternateGregorianCalendar(TimeZone timeZone) {
            super(timeZone);
        }
    }

@org.junit.Test
public void addConvenienceMethodsMatchCalendarArithmetic() {
    java.util.Calendar calendar = new java.util.GregorianCalendar(
            org.apache.commons.lang3.time.DateUtils.UTC_TIME_ZONE);
    calendar.clear();
    calendar.set(2001, java.util.Calendar.JANUARY, 15, 10, 20, 30);
    calendar.set(java.util.Calendar.MILLISECOND, 400);
    java.util.Date source = calendar.getTime();

    assertCalendarAddition(source, java.util.Calendar.YEAR, 1,
            org.apache.commons.lang3.time.DateUtils.addYears(source, 1));
    assertCalendarAddition(source, java.util.Calendar.MONTH, 1,
            org.apache.commons.lang3.time.DateUtils.addMonths(source, 1));
    assertCalendarAddition(source, java.util.Calendar.WEEK_OF_YEAR, 1,
            org.apache.commons.lang3.time.DateUtils.addWeeks(source, 1));
    assertCalendarAddition(source, java.util.Calendar.DAY_OF_MONTH, 1,
            org.apache.commons.lang3.time.DateUtils.addDays(source, 1));
    assertCalendarAddition(source, java.util.Calendar.HOUR_OF_DAY, 1,
            org.apache.commons.lang3.time.DateUtils.addHours(source, 1));
    assertCalendarAddition(source, java.util.Calendar.MINUTE, 1,
            org.apache.commons.lang3.time.DateUtils.addMinutes(source, 1));
    assertCalendarAddition(source, java.util.Calendar.SECOND, 1,
            org.apache.commons.lang3.time.DateUtils.addSeconds(source, 1));
    assertCalendarAddition(source, java.util.Calendar.MILLISECOND, 1,
            org.apache.commons.lang3.time.DateUtils.addMilliseconds(source, 1));
}

@org.junit.Test
public void addDaysRejectsNullDate() {
    try {
        org.apache.commons.lang3.time.DateUtils.addDays(null, 1);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void ceilingObjectAcceptsDateAndCalendar() {
    java.util.Calendar calendar = new java.util.GregorianCalendar(
            org.apache.commons.lang3.time.DateUtils.UTC_TIME_ZONE);
    calendar.clear();
    calendar.set(2001, java.util.Calendar.JANUARY, 15, 10, 20, 30);

    java.util.Calendar expectedCalendar = new java.util.GregorianCalendar(
            org.apache.commons.lang3.time.DateUtils.UTC_TIME_ZONE);
    expectedCalendar.clear();
    expectedCalendar.set(2001, java.util.Calendar.JANUARY, 15, 11, 0, 0);

    java.util.Date expected = expectedCalendar.getTime();
    org.junit.Assert.assertEquals(expected,
            org.apache.commons.lang3.time.DateUtils.ceiling((Object) calendar.getTime(),
                    java.util.Calendar.HOUR_OF_DAY));
    org.junit.Assert.assertEquals(expected,
            org.apache.commons.lang3.time.DateUtils.ceiling((Object) calendar,
                    java.util.Calendar.HOUR_OF_DAY));
}

@org.junit.Test
public void parseDateStrictlyRejectsInvalidCalendarDate() throws java.lang.Exception {
    java.util.Date parsed = org.apache.commons.lang3.time.DateUtils.parseDate(
            "2011-02-29", "yyyy-MM-dd");
    java.util.Calendar parsedCalendar = java.util.Calendar.getInstance();
    parsedCalendar.setTime(parsed);
    org.junit.Assert.assertEquals(2011, parsedCalendar.get(java.util.Calendar.YEAR));
    org.junit.Assert.assertEquals(java.util.Calendar.MARCH,
            parsedCalendar.get(java.util.Calendar.MONTH));
    org.junit.Assert.assertEquals(1,
            parsedCalendar.get(java.util.Calendar.DAY_OF_MONTH));

    try {
        org.apache.commons.lang3.time.DateUtils.parseDateStrictly(
                "2011-02-29", "yyyy-MM-dd");
        org.junit.Assert.fail("Expected ParseException");
    } catch (java.text.ParseException expected) {
        // expected
    }
}

private void assertCalendarAddition(java.util.Date source, int field, int amount,
        java.util.Date actual) {
    java.util.Calendar expected = java.util.Calendar.getInstance();
    expected.setTime(source);
    expected.add(field, amount);
    org.junit.Assert.assertEquals(expected.getTime(), actual);
}
}

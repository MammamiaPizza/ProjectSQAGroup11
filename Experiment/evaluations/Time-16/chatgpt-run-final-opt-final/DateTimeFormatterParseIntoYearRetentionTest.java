package org.joda.time.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.junit.Test;

public class DateTimeFormatterParseIntoYearRetentionTest {

    private static MutableDateTime instant(int year, int month, int day, DateTimeZone zone) {
        return new MutableDateTime(year, month, day, 12, 20, 30, 0, zone);
    }

    private static void assertDateTime(
            MutableDateTime actual, int year, int month, int day, DateTimeZone zone) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonthOfYear());
        assertEquals(day, actual.getDayOfMonth());
        assertEquals(12, actual.getHourOfDay());
        assertEquals(20, actual.getMinuteOfHour());
        assertEquals(30, actual.getSecondOfMinute());
        assertEquals(0, actual.getMillisOfSecond());
        assertEquals(zone, actual.getZone());
    }

    @Test
    public void testParseIntoMonthOnlyRetainsBaseYearAtStartOfYear() {
        DateTimeZone zone = DateTimeZone.forOffsetHours(9);
        MutableDateTime target = instant(2004, 1, 1, zone);

        int position = DateTimeFormat.forPattern("MM").parseInto(target, "05", 0);

        assertEquals(2, position);
        assertDateTime(target, 2004, 5, 1, zone);
    }

    @Test
    public void testParseIntoMonthOnlyRetainsBaseYearAtEndOfYear() {
        DateTimeZone zone = DateTimeZone.forOffsetHours(9);
        MutableDateTime target = instant(2004, 12, 31, zone);

        int position = DateTimeFormat.forPattern("MM").parseInto(target, "05", 0);

        assertEquals(2, position);
        assertDateTime(target, 2004, 5, 31, zone);
    }

    @Test
    public void testParseIntoJanuaryRetainsBaseYearAndDay() {
        DateTimeZone zone = DateTimeZone.forOffsetHours(1);
        MutableDateTime target = instant(2004, 5, 9, zone);

        int position = DateTimeFormat.forPattern("MM").parseInto(target, "01", 0);

        assertEquals(2, position);
        assertDateTime(target, 2004, 1, 9, zone);
    }

    @Test
    public void testParseIntoDecemberRetainsBaseYearAndDay() {
        DateTimeZone zone = DateTimeZone.forOffsetHours(9);
        MutableDateTime target = instant(2004, 5, 31, zone);

        int position = DateTimeFormat.forPattern("MM").parseInto(target, "12", 0);

        assertEquals(2, position);
        assertDateTime(target, 2004, 12, 31, zone);
    }

    @Test
    public void testParseIntoMonthDayLeapDayRetainsBaseLeapYear() {
        MutableDateTime target = instant(2004, 1, 1, DateTimeZone.UTC);

        int position = DateTimeFormat.forPattern("MM-dd").parseInto(target, "02-29", 0);

        assertEquals(5, position);
        assertDateTime(target, 2004, 2, 29, DateTimeZone.UTC);
    }

    @Test
    public void testParseIntoMonthDayDoesNotReplaceBaseYearWithConfiguredDefaultYear() {
        MutableDateTime target = instant(2004, 1, 1, DateTimeZone.UTC);
        DateTimeFormatter formatter =
                DateTimeFormat.forPattern("MM-dd").withDefaultYear(2012);

        int position = formatter.parseInto(target, "02-29", 0);

        assertEquals(5, position);
        assertDateTime(target, 2004, 2, 29, DateTimeZone.UTC);
    }

    @Test
    public void testParseIntoAtNonZeroPositionRetainsBaseYear() {
        DateTimeZone zone = DateTimeZone.forOffsetHours(9);
        MutableDateTime target = instant(2004, 1, 9, zone);

        int position = DateTimeFormat.forPattern("MM").parseInto(target, "xx05", 2);

        assertEquals(4, position);
        assertDateTime(target, 2004, 5, 9, zone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseIntoRejectsNullInstant() {
        DateTimeFormat.forPattern("MM").parseInto(null, "05", 0);
    }

    @Test
    public void testParseIntoReportsNegativePositionForInvalidMonthText() {
        MutableDateTime target = instant(2004, 5, 9, DateTimeZone.UTC);

        int position = DateTimeFormat.forPattern("MM").parseInto(target, "xx", 0);

        assertTrue(position < 0);
    }
}

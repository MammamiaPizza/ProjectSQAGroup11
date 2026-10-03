package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StdDateFormatBug87Test
{
    private TimeZone originalDefaultTimeZone;

    @Before
    public void forceDeterministicDefaultTimeZone() {
        originalDefaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @After
    public void restoreDefaultTimeZone() {
        TimeZone.setDefault(originalDefaultTimeZone);
    }

    @Test
    public void parsesTimezoneLessIsoUsingWithTimeZoneConfiguration() throws Exception {
        TimeZone plusTwo = TimeZone.getTimeZone("GMT+02:00");
        StdDateFormat format = new StdDateFormat().withTimeZone(plusTwo);

        Date parsed = format.parse("1970-01-01T00:00:00");

        assertEquals(-2L * 60L * 60L * 1000L, parsed.getTime());
    }

    @Test
    public void parsesTimezoneLessIsoUsingSetTimeZoneConfiguration() throws Exception {
        TimeZone minusFive = TimeZone.getTimeZone("GMT-05:00");
        StdDateFormat format = new StdDateFormat();
        format.setTimeZone(minusFive);

        Date parsed = format.parse("1970-01-01T00:00:00.250");

        assertEquals(5L * 60L * 60L * 1000L + 250L, parsed.getTime());
    }

    @Test
    public void changingTimezoneClearsCachedTimezoneLessIsoParser() throws Exception {
        StdDateFormat format = new StdDateFormat();
        TimeZone plusTwo = TimeZone.getTimeZone("GMT+02:00");
        TimeZone minusFive = TimeZone.getTimeZone("GMT-05:00");

        format.setTimeZone(plusTwo);
        assertEquals(-2L * 60L * 60L * 1000L,
                format.parse("1970-01-01T00:00:00").getTime());

        format.setTimeZone(minusFive);
        assertEquals(5L * 60L * 60L * 1000L,
                format.parse("1970-01-01T00:00:00").getTime());
    }

    @Test
    public void parsePositionPathUsesConfiguredTimezoneForTimezoneLessIso() {
        TimeZone plusTwo = TimeZone.getTimeZone("GMT+02:00");
        StdDateFormat format = new StdDateFormat().withTimeZone(plusTwo);
        String input = "2017-03-14T15:09:26.250";
        ParsePosition position = new ParsePosition(0);

        Date parsed = format.parse(input, position);

        assertNotNull(parsed);
        assertEquals(localTime(plusTwo, 2017, 3, 14, 15, 9, 26, 250).getTime(),
                parsed.getTime());
        assertEquals(input.length(), position.getIndex());
    }

    @Test
    public void parsesExplicitIsoTimezoneIndependentlyOfConfiguredTimezone() throws Exception {
        StdDateFormat format = new StdDateFormat()
                .withTimeZone(TimeZone.getTimeZone("GMT-05:00"));

        Date parsed = format.parse("2017-03-14T15:09:26+02:00");

        assertEquals(localTime(TimeZone.getTimeZone("UTC"),
                2017, 3, 14, 13, 9, 26, 0).getTime(), parsed.getTime());
    }

    @Test
    public void parsesPlainDateAndRfc1123Date() throws Exception {
        TimeZone plusTwo = TimeZone.getTimeZone("GMT+02:00");
        StdDateFormat format = new StdDateFormat().withTimeZone(plusTwo);

        assertEquals(localTime(plusTwo, 2017, 3, 14, 0, 0, 0, 0).getTime(),
                format.parse("2017-03-14").getTime());
        assertEquals(localTime(TimeZone.getTimeZone("UTC"),
                2017, 3, 14, 15, 9, 26, 0).getTime(),
                format.parse("Tue, 14 Mar 2017 15:09:26 GMT").getTime());
    }

    @Test
    public void invalidInputFailsForBothParseApis() {
        StdDateFormat format = new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            format.parse("not a date");
            fail("Expected ParseException for invalid date text");
        } catch (ParseException expected) {
            assertTrue(expected.getMessage().contains("not a date"));
        }

        ParsePosition position = new ParsePosition(0);
        assertNull(format.parse("not a date", position));
    }

    private static Date localTime(TimeZone timeZone, int year, int month, int day,
            int hour, int minute, int second, int millisecond) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone, Locale.US);
        calendar.clear();
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(GregorianCalendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }
}

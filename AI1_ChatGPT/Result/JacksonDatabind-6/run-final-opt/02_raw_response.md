package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StdDateFormatBug570Test
{
    private StdDateFormat utcFormat() {
        return new StdDateFormat().withTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private long utcMillis(int year, int month, int day,
            int hour, int minute, int second, int millisecond) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTimeInMillis();
    }

    @Test
    public void parsesIso8601WithoutSecondsAndColonTimezone() throws Exception {
        Date result = utcFormat().parse("1997-07-16T19:20+01:00");

        assertEquals(utcMillis(1997, 7, 16, 18, 20, 0, 0), result.getTime());
    }

    @Test
    public void parsesIso8601OneDigitFractionWithColonTimezone() throws Exception {
        Date result = utcFormat().parse("2014-10-03T18:00:00.6-05:00");

        assertEquals(utcMillis(2014, 10, 3, 23, 0, 0, 600), result.getTime());
    }

    @Test
    public void parsesIso8601TwoDigitFractionWithColonTimezone() throws Exception {
        Date result = utcFormat().parse("2014-10-03T18:00:00.06-05:00");

        assertEquals(utcMillis(2014, 10, 3, 23, 0, 0, 60), result.getTime());
    }

    @Test
    public void parsePositionAlsoParsesMissingSecondsWithTimezone() {
        ParsePosition position = new ParsePosition(0);
        Date result = utcFormat().parse("1997-07-16T19:20+01:00", position);

        assertNotNull(result);
        assertEquals(utcMillis(1997, 7, 16, 18, 20, 0, 0), result.getTime());
    }

    @Test
    public void parsesStandardIso8601UtcValue() throws Exception {
        Date result = utcFormat().parse("2014-10-03T18:00:00.006Z");

        assertEquals(utcMillis(2014, 10, 3, 18, 0, 0, 6), result.getTime());
    }

    @Test
    public void parsesPlainDateUsingConfiguredTimezone() throws Exception {
        Date result = utcFormat().parse("2014-10-03");

        assertEquals(utcMillis(2014, 10, 3, 0, 0, 0, 0), result.getTime());
    }

    @Test
    public void parsesStringifiedTimestamp() throws Exception {
        Date result = utcFormat().parse("0");

        assertEquals(0L, result.getTime());
    }

    @Test
    public void rejectsNonDateText() {
        try {
            utcFormat().parse("not a date");
            fail("Expected ParseException for non-date text");
        } catch (ParseException e) {
            assertTrue(e.getErrorOffset() >= -1);
        }
    }
}
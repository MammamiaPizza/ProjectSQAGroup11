package com.fasterxml.jackson.databind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StdDateFormatLeniencyTest
{
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private StdDateFormat newFormat() {
        return new StdDateFormat(UTC, Locale.US);
    }

    private Date utcDate(int year, int month, int day, int hour, int minute, int second) {
        GregorianCalendar calendar = new GregorianCalendar(UTC, Locale.US);
        calendar.clear();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }

    @Test
    public void testSetLenientOnFreshFormatAndReportState() {
        StdDateFormat format = newFormat();

        assertTrue(format.isLenient());

        format.setLenient(false);
        assertFalse(format.isLenient());

        format.setLenient(true);
        assertTrue(format.isLenient());
    }

    @Test
    public void testStrictLeniencyParsesSupportedValidFormats() throws Exception {
        StdDateFormat format = newFormat();
        format.setLenient(false);

        assertEquals(utcDate(2015, 1, 2, 3, 4, 5),
                format.parse("2015-01-02T03:04:05Z"));
        assertEquals(utcDate(2015, 1, 2, 0, 0, 0),
                format.parse("2015-01-02"));
        assertEquals(utcDate(2015, 1, 2, 3, 4, 5),
                format.parse("Fri, 02 Jan 2015 03:04:05 GMT"));
    }

    @Test
    public void testLenientSettingControlsInvalidPlainDateAfterCachedParse() throws Exception {
        StdDateFormat format = newFormat();

        format.setLenient(true);
        assertEquals(utcDate(2015, 3, 2, 0, 0, 0),
                format.parse("2015-02-30"));

        format.setLenient(false);
        try {
            format.parse("2015-02-30");
            fail("Strict parsing must reject an invalid calendar date");
        } catch (ParseException expected) {
            assertNotNull(expected.getMessage());
        }

        format.setLenient(true);
        assertEquals(utcDate(2015, 3, 2, 0, 0, 0),
                format.parse("2015-02-30"));
    }

    @Test
    public void testParseWithPositionHandlesValidAndInvalidDatesAccordingToLeniency() {
        StdDateFormat format = newFormat();
        format.setLenient(false);

        ParsePosition validPosition = new ParsePosition(0);
        Date valid = format.parse("2015-01-02", validPosition);
        assertEquals(utcDate(2015, 1, 2, 0, 0, 0), valid);
        assertEquals(10, validPosition.getIndex());

        ParsePosition invalidPosition = new ParsePosition(0);
        assertNull(format.parse("2015-02-30", invalidPosition));
    }
}
package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import junit.framework.TestCase;

public class ISO8601UtilsTest extends TestCase {

    public void testFormatUtcWithoutAndWithMilliseconds() {
        Date date = new Date(123L);

        assertEquals("1970-01-01T00:00:00Z", ISO8601Utils.format(date));
        assertEquals("1970-01-01T00:00:00Z", ISO8601Utils.format(date, false));
        assertEquals("1970-01-01T00:00:00.123Z", ISO8601Utils.format(date, true));
    }

    public void testFormatUsesSuppliedPositiveAndNegativeTimezoneOffsets() {
        Date epoch = new Date(0L);

        TimeZone plusFiveThirty =
                new SimpleTimeZone(5 * 60 * 60 * 1000 + 30 * 60 * 1000, "PLUS_0530");
        TimeZone minusThree =
                new SimpleTimeZone(-3 * 60 * 60 * 1000, "MINUS_0300");

        assertEquals("1970-01-01T05:30:00+05:30",
                ISO8601Utils.format(epoch, false, plusFiveThirty));
        assertEquals("1969-12-31T21:00:00-03:00",
                ISO8601Utils.format(epoch, false, minusThree));
    }

    public void testParseDateOnlyAndAdvancesParsePosition() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("20160229", position);

        assertEquals(8, position.getIndex());

        java.util.Calendar expected = new java.util.GregorianCalendar(2016, 1, 29);
        assertEquals(expected.getTime(), parsed);
    }

    public void testParseDateWithUtcTimezoneWithoutTimeComponent() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("1970-01-01Z", position);

        assertEquals(11, position.getIndex());
        assertEquals(new Date(0L), parsed);
    }

    public void testParseCompactDateTimeWithFractionAndUtcTimezone() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("19700102T030405.6789Z", position);

        assertEquals(21, position.getIndex());
        assertEquals(new Date(97445678L), parsed);
    }

    public void testParseFractionalSecondsWithOneAndTwoDigits() throws Exception {
        ParsePosition oneDigitPosition = new ParsePosition(0);
        Date oneDigit = ISO8601Utils.parse("1970-01-01T00:00:00.1Z", oneDigitPosition);

        assertEquals(22, oneDigitPosition.getIndex());
        assertEquals(new Date(100L), oneDigit);

        ParsePosition twoDigitPosition = new ParsePosition(0);
        Date twoDigits = ISO8601Utils.parse("1970-01-01T00:00:00.12Z", twoDigitPosition);

        assertEquals(23, twoDigitPosition.getIndex());
        assertEquals(new Date(120L), twoDigits);
    }

    public void testParseTimeWithoutSeconds() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("1970-01-01T00:00Z", position);

        assertEquals(17, position.getIndex());
        assertEquals(new Date(0L), parsed);
    }

    public void testParseLeapSecondIsTruncatedToLastValidSecond() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("1970-01-01T00:00:60Z", position);

        assertEquals(20, position.getIndex());
        assertEquals(new Date(59000L), parsed);
    }

    public void testParseShortHourTimezoneOffsets() throws Exception {
        Date plusOne = ISO8601Utils.parse(
                "1970-01-01T01:00:00+01", new ParsePosition(0));
        assertEquals(new Date(0L), plusOne);

        Date zero = ISO8601Utils.parse(
                "1970-01-01T00:00:00+00", new ParsePosition(0));
        assertEquals(new Date(0L), zero);

        Date negative = ISO8601Utils.parse(
                "1970-01-01T00:30:00-01", new ParsePosition(0));
        assertEquals(new Date(90L * 60L * 1000L), negative);
    }

    public void testParseFourDigitTimezoneOffsetWithoutColon() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("1970-01-01T00:00:00+0130", position);

        assertEquals(24, position.getIndex());
        assertEquals(new Date(-90L * 60L * 1000L), parsed);
    }

    public void testParseExplicitZeroTimezoneOffsets() throws Exception {
        ParsePosition compactPosition = new ParsePosition(0);
        Date compact = ISO8601Utils.parse("1970-01-01T00:00:00+0000", compactPosition);

        assertEquals(24, compactPosition.getIndex());
        assertEquals(new Date(0L), compact);

        ParsePosition colonPosition = new ParsePosition(0);
        Date colon = ISO8601Utils.parse("1970-01-01T00:00:00+00:00", colonPosition);

        assertEquals(25, colonPosition.getIndex());
        assertEquals(new Date(0L), colon);
    }

    public void testParseTimezoneWithMinutes() throws Exception {
        ParsePosition position = new ParsePosition(0);

        Date parsed = ISO8601Utils.parse("1970-01-01T00:30:00-01:30", position);

        assertEquals(25, position.getIndex());
        assertEquals(new Date(2L * 60L * 60L * 1000L), parsed);
    }

    public void testParseHonorsInitialParsePosition() throws Exception {
        ParsePosition position = new ParsePosition(2);

        Date parsed = ISO8601Utils.parse("xx1970-01-01T00:00:00Z", position);

        assertEquals(22, position.getIndex());
        assertEquals(new Date(0L), parsed);
    }

    public void testParseRejectsMissingTimezoneForDateTime() {
        assertParseFails("1970-01-01T00:00:00");
    }

    public void testParseRejectsMissingTimezoneAfterFraction() {
        assertParseFails("1970-01-01T00:00:00.123");
    }

    public void testParseRejectsInvalidCalendarDate() {
        assertParseFails("2015-02-29T00:00:00Z");
    }

    public void testParseRejectsNonNumericDateComponent() {
        assertParseFails("1970-0A-01T00:00:00Z");
    }

    public void testParseRejectsNonNumericFirstDateDigit() {
        assertParseFails("A970-01-01T00:00:00Z");
    }

    public void testParseRejectsTooShortInput() {
        assertParseFails("");
    }

    public void testParseRejectsInvalidTimezoneIndicator() {
        assertParseFails("1970-01-01T00:00:00X");
    }

    public void testParseRejectsInvalidTimezoneOffset() {
        assertParseFails("1970-01-01T00:00:00+99");
    }

    private void assertParseFails(String input) {
        try {
            ISO8601Utils.parse(input, new ParsePosition(0));
            fail("Expected ParseException for: " + input);
        } catch (ParseException expected) {
            assertNotNull(expected.getMessage());
            assertTrue(expected.getMessage().contains("Failed to parse date"));
        }
    }
}

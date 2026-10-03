package org.joda.time.format;

import junit.framework.TestCase;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;

public class DateTimeFormatterFeb29RegressionTest extends TestCase {

    private DateTimeFormatter monthDayFormatter() {
        return DateTimeFormat.forPattern("M d");
    }

    public void testParseIntoMonthDayFeb29NewYorkStartOfYear() {
        DateTimeZone zone = DateTimeZone.forID("America/New_York");
        MutableDateTime instant = new MutableDateTime(2001, 1, 1, 0, 0, 0, 0, zone);

        int position = monthDayFormatter().parseInto(instant, "2 29", 0);

        assertEquals(4, position);
        assertEquals(2, instant.getMonthOfYear());
        assertEquals(29, instant.getDayOfMonth());
        assertEquals(zone, instant.getZone());
    }

    public void testParseIntoMonthDayFeb29TokyoEndOfYear() {
        DateTimeZone zone = DateTimeZone.forID("Asia/Tokyo");
        MutableDateTime instant = new MutableDateTime(2001, 12, 31, 0, 0, 0, 0, zone);

        int position = monthDayFormatter().parseInto(instant, "2 29", 0);

        assertEquals(4, position);
        assertEquals(2, instant.getMonthOfYear());
        assertEquals(29, instant.getDayOfMonth());
        assertEquals(zone, instant.getZone());
    }

    public void testParseIntoMonthDayNormalFebruaryDate() {
        DateTimeZone zone = DateTimeZone.forID("America/New_York");
        MutableDateTime instant = new MutableDateTime(2001, 1, 1, 0, 0, 0, 0, zone);

        int position = monthDayFormatter().parseInto(instant, "2 28", 0);

        assertEquals(4, position);
        assertEquals(2, instant.getMonthOfYear());
        assertEquals(28, instant.getDayOfMonth());
    }

    public void testParseIntoRejectsInvalidFebruaryDay() {
        MutableDateTime instant = new MutableDateTime(
                2001, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);

        try {
            monthDayFormatter().parseInto(instant, "2 30", 0);
            fail("February 30 must not be accepted");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
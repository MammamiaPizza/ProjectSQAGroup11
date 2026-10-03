package org.apache.commons.lang3.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class FastDateParserLang831Test {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Test
    public void parsesMonthAndWeekdayInput() throws Exception {
        FastDateParser parser = new FastDateParser("M E", UTC, Locale.US);

        Date result = parser.parse("3 Tue");

        Calendar calendar = Calendar.getInstance(UTC, Locale.US);
        calendar.setTime(result);
        assertEquals(1970, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
        assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.TUESDAY, calendar.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void parsePositionParsesMonthAndWeekdayInput() {
        FastDateParser parser = new FastDateParser("M E", UTC, Locale.US);
        ParsePosition position = new ParsePosition(0);

        Date result = parser.parse("3 Tue", position);

        assertNotNull(result);
        Calendar calendar = Calendar.getInstance(UTC, Locale.US);
        calendar.setTime(result);
        assertEquals(1970, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
        assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.TUESDAY, calendar.get(Calendar.DAY_OF_WEEK));
        assertEquals(5, position.getIndex());
    }

    @Test
    public void parsesCompleteDateWithMatchingWeekday() throws Exception {
        FastDateParser parser = new FastDateParser("yyyy-MM-dd E", UTC, Locale.US);

        Date result = parser.parse("1970-03-03 Tue");

        Calendar calendar = Calendar.getInstance(UTC, Locale.US);
        calendar.setTime(result);
        assertEquals(1970, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
        assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(Calendar.TUESDAY, calendar.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void parsePositionConsumesOnlyTheMatchingCompleteDate() {
        FastDateParser parser = new FastDateParser("yyyy-MM-dd E", UTC, Locale.US);
        ParsePosition position = new ParsePosition(2);

        Date result = parser.parse("xx1970-03-03 Tue trailing", position);

        Calendar calendar = Calendar.getInstance(UTC, Locale.US);
        calendar.setTime(result);
        assertEquals(1970, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
        assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(16, position.getIndex());
    }
}
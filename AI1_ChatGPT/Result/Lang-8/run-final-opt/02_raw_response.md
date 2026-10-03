package org.apache.commons.lang3.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class FastDatePrinterCalendarTimezoneTest {

    private FastDatePrinter newPrinter() {
        return new FastDatePrinter(
                "h:mma z",
                TimeZone.getTimeZone("America/Los_Angeles"),
                Locale.US);
    }

    private Calendar newBangkokCalendar() {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Bangkok"), Locale.US);
        calendar.clear();
        calendar.set(2012, Calendar.JANUARY, 15, 14, 43, 0);
        return calendar;
    }

    @Test
    public void testFormatCalendarUsesCalendarTimezoneForTimezoneName() {
        assertEquals("2:43PM ICT", newPrinter().format(newBangkokCalendar()));
    }

    @Test
    public void testFormatCalendarIntoBufferUsesCalendarTimezoneForTimezoneName() {
        FastDatePrinter printer = newPrinter();
        StringBuffer buffer = new StringBuffer("prefix:");

        StringBuffer result = printer.format(newBangkokCalendar(), buffer);

        assertSame(buffer, result);
        assertEquals("prefix:2:43PM ICT", buffer.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFormatObjectRejectsUnsupportedObjectType() {
        newPrinter().format(new Object(), new StringBuffer(), new FieldPosition(0));
    }
}
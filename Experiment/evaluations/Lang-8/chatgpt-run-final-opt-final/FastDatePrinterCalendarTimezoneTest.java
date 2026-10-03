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

@org.junit.Test
public void testFormatDateAndMillisInPrinterTimezone() {
    FastDatePrinter printer = new FastDatePrinter(
            "yyyy-MM-dd HH:mm Z",
            java.util.TimeZone.getTimeZone("UTC"),
            java.util.Locale.US);

    java.util.Date epoch = new java.util.Date(0L);
    org.junit.Assert.assertEquals("1970-01-01 00:00 +0000", printer.format(epoch));
    org.junit.Assert.assertEquals(printer.format(epoch), printer.format(0L));

    java.lang.StringBuffer buffer = new java.lang.StringBuffer("prefix:");
    org.junit.Assert.assertSame(buffer, printer.format(epoch, buffer));
    org.junit.Assert.assertEquals("prefix:1970-01-01 00:00 +0000", buffer.toString());
}

@org.junit.Test
public void testFormatObjectLongUsesMillisFormatting() {
    FastDatePrinter printer = new FastDatePrinter(
            "yyyy-MM-dd HH:mm Z",
            java.util.TimeZone.getTimeZone("UTC"),
            java.util.Locale.US);

    java.lang.StringBuffer buffer = new java.lang.StringBuffer("prefix:");
    org.junit.Assert.assertSame(buffer,
            printer.format(java.lang.Long.valueOf(0L), buffer, new java.text.FieldPosition(0)));
    org.junit.Assert.assertEquals("prefix:1970-01-01 00:00 +0000", buffer.toString());
}

@org.junit.Test
public void testEqualsHashCodeAndConfigurationAccessors() {
    java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
    FastDatePrinter printer = new FastDatePrinter("yyyy", utc, java.util.Locale.US);
    FastDatePrinter equalPrinter = new FastDatePrinter("yyyy", utc, java.util.Locale.US);
    FastDatePrinter differentPattern = new FastDatePrinter("yy", utc, java.util.Locale.US);
    FastDatePrinter differentTimezone = new FastDatePrinter(
            "yyyy", java.util.TimeZone.getTimeZone("GMT+01:00"), java.util.Locale.US);
    FastDatePrinter differentLocale = new FastDatePrinter("yyyy", utc, java.util.Locale.FRANCE);

    org.junit.Assert.assertTrue(printer.equals(equalPrinter));
    org.junit.Assert.assertEquals(printer.hashCode(), equalPrinter.hashCode());
    org.junit.Assert.assertFalse(printer.equals(differentPattern));
    org.junit.Assert.assertFalse(printer.equals(differentTimezone));
    org.junit.Assert.assertFalse(printer.equals(differentLocale));
    org.junit.Assert.assertFalse(printer.equals("yyyy"));
    org.junit.Assert.assertFalse(printer.equals(null));

    org.junit.Assert.assertEquals("yyyy", printer.getPattern());
    org.junit.Assert.assertEquals(utc, printer.getTimeZone());
    org.junit.Assert.assertEquals(java.util.Locale.US, printer.getLocale());
    org.junit.Assert.assertTrue(printer.getMaxLengthEstimate() >= printer.format(0L).length());
}
}

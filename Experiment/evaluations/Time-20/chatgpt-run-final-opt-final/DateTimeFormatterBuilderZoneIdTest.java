package org.joda.time.format;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class DateTimeFormatterBuilderZoneIdTest {

    private DateTimeFormatter newZoneIdFormatter() {
        return new DateTimeFormatterBuilder()
                .appendYear(4, 4)
                .appendLiteral('-')
                .appendMonthOfYear(2)
                .appendLiteral('-')
                .appendDayOfMonth(2)
                .appendLiteral(' ')
                .appendHourOfDay(2)
                .appendLiteral(':')
                .appendMinuteOfHour(2)
                .appendLiteral(' ')
                .appendTimeZoneId()
                .toFormatter();
    }

    @Test
    public void testParseDawsonCreekZoneIdContainingUnderscore() {
        DateTime parsed = newZoneIdFormatter()
                .parseDateTime("2007-03-04 12:30 America/Dawson_Creek");

        assertEquals(DateTimeZone.forID("America/Dawson_Creek"), parsed.getZone());
        assertEquals(2007, parsed.getYear());
        assertEquals(3, parsed.getMonthOfYear());
        assertEquals(4, parsed.getDayOfMonth());
        assertEquals(12, parsed.getHourOfDay());
        assertEquals(30, parsed.getMinuteOfHour());
    }

    @Test
    public void testPrintAndParseDawsonCreekZoneId() {
        DateTimeFormatter formatter = newZoneIdFormatter();
        DateTime original = new DateTime(
                2007, 3, 4, 12, 30, 0, 0,
                DateTimeZone.forID("America/Dawson_Creek"));

        String text = formatter.print(original);
        DateTime parsed = formatter.parseDateTime(text);

        assertEquals("2007-03-04 12:30 America/Dawson_Creek", text);
        assertEquals(original, parsed);
        assertEquals(original.getZone(), parsed.getZone());
    }

    @Test
    public void testParseNormalSlashSeparatedZoneId() {
        DateTime parsed = newZoneIdFormatter()
                .parseDateTime("2007-03-04 12:30 America/New_York");

        assertEquals(DateTimeZone.forID("America/New_York"), parsed.getZone());
        assertEquals(12, parsed.getHourOfDay());
        assertEquals(30, parsed.getMinuteOfHour());
    }

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullFormatterIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimeFormatter) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullPrinterIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimePrinter) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullParserIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimeParser) null);
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testAppendNullParserArrayIsRejected() {
    new org.joda.time.format.DateTimeFormatterBuilder()
        .append((org.joda.time.format.DateTimePrinter) null,
                (org.joda.time.format.DateTimeParser[]) null);
}
}

package org.jfree.data.time.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.data.time.Week;
import org.junit.AfterClass;
import org.junit.Test;

public class WeekDefectsTest {

    private static final TimeZone ORIGINAL_TIME_ZONE = TimeZone.getDefault();
    private static final Locale ORIGINAL_LOCALE = Locale.getDefault();

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale.setDefault(Locale.US);
    }

    @AfterClass
    public static void restoreDefaults() {
        TimeZone.setDefault(ORIGINAL_TIME_ZONE);
        Locale.setDefault(ORIGINAL_LOCALE);
    }

    @Test
    public void dateAndTimeZoneConstructorUsesSuppliedTimeZone() {
        Date instant = new Date(1451781000000L); // 2016-01-03 00:30:00 UTC
        Week week = new Week(instant, TimeZone.getTimeZone("America/Los_Angeles"));

        assertEquals(1, week.getWeek());
        assertEquals(2016, week.getYearValue());
    }

    @Test
    public void dateAndTimeZoneConstructorRejectsNullTimeZone() {
        try {
            new Week(new Date(1451781000000L), null);
            fail("Expected an IllegalArgumentException for a null time zone.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Null 'zone' argument.", expected.getMessage());
        }
    }

    @Test
    public void dateTimeZoneAndLocaleConstructorExtractsReportedWeekThirtyFive() {
        Date date = new Date(999259200000L); // 2001-08-31 12:00:00 UTC
        Week week = new Week(date, TimeZone.getTimeZone("Europe/London"), Locale.UK);

        assertEquals(35, week.getWeek());
        assertEquals(2001, week.getYearValue());
    }

    @Test
    public void dateConstructorUsesDefaultTimeZoneAndLocale() {
        Week week = new Week(new Date(1451781000000L)); // 2016-01-03 00:30:00 UTC

        assertEquals(2, week.getWeek());
        assertEquals(2016, week.getYearValue());
    }

    @Test
    public void dateTimeZoneAndLocaleConstructorRejectsNullArguments() {
        TimeZone utc = TimeZone.getTimeZone("UTC");

        try {
            new Week(null, utc, Locale.US);
            fail("Expected an IllegalArgumentException for a null date.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Null 'time' argument.", expected.getMessage());
        }

        try {
            new Week(new Date(0L), null, Locale.US);
            fail("Expected an IllegalArgumentException for a null time zone.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Null 'zone' argument.", expected.getMessage());
        }

        try {
            new Week(new Date(0L), utc, null);
            fail("Expected an IllegalArgumentException for a null locale.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Null 'locale' argument.", expected.getMessage());
        }
    }
}
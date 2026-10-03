package org.apache.commons.lang3.time;

import static org.junit.Assert.assertEquals;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class FastDateFormatLang538Test {

    private static final TimeZone UTC = TimeZone.getTimeZone("GMT");
    private static final TimeZone GMT_MINUS_8 = TimeZone.getTimeZone("GMT-08:00");
    private static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private long millis(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = new GregorianCalendar(UTC, Locale.US);
        calendar.clear();
        calendar.set(year, month, day, hour, minute, second);
        return calendar.getTimeInMillis();
    }

    @Test
    public void formatCalendarUsesForcedUtcRatherThanSourceCalendarTimezone() {
        long instant = millis(2009, Calendar.OCTOBER, 16, 16, 42, 16);
        Calendar sourceCalendar = new GregorianCalendar(GMT_MINUS_8, Locale.US);
        sourceCalendar.setTimeInMillis(instant);

        FastDateFormat format = FastDateFormat.getInstance(ISO_PATTERN, UTC, Locale.US);

        assertEquals("2009-10-16T16:42:16.000Z", format.format(sourceCalendar));
    }

    @Test
    public void formatDateAndMillisUseForcedTimezoneEvenWhenDefaultTimezoneDiffers() {
        TimeZone originalDefault = TimeZone.getDefault();
        try {
            TimeZone.setDefault(GMT_MINUS_8);
            long instant = millis(2009, Calendar.OCTOBER, 16, 16, 42, 16);
            FastDateFormat format = FastDateFormat.getInstance(ISO_PATTERN, UTC, Locale.US);

            assertEquals("2009-10-16T16:42:16.000Z", format.format(new Date(instant)));
            assertEquals("2009-10-16T16:42:16.000Z", format.format(instant));
        } finally {
            TimeZone.setDefault(originalDefault);
        }
    }

    @Test
    public void formatCalendarConvertsAcrossDayBoundaryIntoFormatterTimezone() {
        long instant = millis(2009, Calendar.OCTOBER, 16, 0, 15, 0);
        Calendar sourceCalendar = new GregorianCalendar(GMT_MINUS_8, Locale.US);
        sourceCalendar.setTimeInMillis(instant);

        FastDateFormat format = FastDateFormat.getInstance(ISO_PATTERN, UTC, Locale.US);

        assertEquals("2009-10-16T00:15:00.000Z", format.format(sourceCalendar));
    }

    @Test
    public void formatCalendarAlsoUsesForcedTimezoneThroughFormatObjectApi() {
        long instant = millis(2009, Calendar.OCTOBER, 16, 16, 42, 16);
        Calendar sourceCalendar = new GregorianCalendar(GMT_MINUS_8, Locale.US);
        sourceCalendar.setTimeInMillis(instant);
        FastDateFormat format = FastDateFormat.getInstance(ISO_PATTERN, UTC, Locale.US);

        StringBuffer result = new StringBuffer();
        format.format(sourceCalendar, result, new FieldPosition(0));

        assertEquals("2009-10-16T16:42:16.000Z", result.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatRejectsUnsupportedObjectTypes() {
        FastDateFormat format = FastDateFormat.getInstance(ISO_PATTERN, UTC, Locale.US);
        format.format(new Object(), new StringBuffer(), new FieldPosition(0));
    }
}

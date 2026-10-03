package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class DurationFormatUtilsLang63Test {

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static long gmtMillis(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(GMT);
        calendar.clear();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    @Test
    public void testJiraLang281MonthEndPeriodKeepsNineDayRemainder() {
        long start = gmtMillis(2001, Calendar.JANUARY, 31);
        long end = gmtMillis(2001, Calendar.MARCH, 9);

        assertEquals("1 months 09",
                DurationFormatUtils.formatPeriod(start, end, "M' months 'dd", true, GMT));
    }

    @Test
    public void testFormatPeriodAcrossMonthBoundaryWithDaysAndMonths() {
        long start = gmtMillis(2001, Calendar.JANUARY, 31);
        long end = gmtMillis(2001, Calendar.MARCH, 9);

        assertEquals("01-09",
                DurationFormatUtils.formatPeriod(start, end, "MM-dd", true, GMT));
    }

    @Test
    public void testFormatPeriodForEqualInstantsUsesZeroValues() {
        long instant = gmtMillis(2001, Calendar.JUNE, 15);

        assertEquals("00:00:00.000",
                DurationFormatUtils.formatPeriod(instant, instant, "HH:mm:ss.SSS", true, GMT));
    }

    @Test
    public void testFormatPeriodBelowTwentyEightDaysUsesDurationFields() {
        long start = gmtMillis(2001, Calendar.JANUARY, 1);
        long end = start + 27L * DateUtils.MILLIS_PER_DAY
                + 2L * DateUtils.MILLIS_PER_HOUR
                + 3L * DateUtils.MILLIS_PER_MINUTE
                + 4L * DateUtils.MILLIS_PER_SECOND
                + 5L;

        assertEquals("27 02:03:04.005",
                DurationFormatUtils.formatPeriod(start, end, "d HH:mm:ss.SSS", true, GMT));
    }

    @Test
    public void testFormatDurationHMSPadsAllTimeFields() {
        long duration = DateUtils.MILLIS_PER_HOUR
                + 2L * DateUtils.MILLIS_PER_MINUTE
                + 3L * DateUtils.MILLIS_PER_SECOND
                + 4L;

        assertEquals("1:02:03.004", DurationFormatUtils.formatDurationHMS(duration));
    }

    @Test
    public void testFormatDurationCarriesOmittedHoursIntoMinutes() {
        long duration = DateUtils.MILLIS_PER_HOUR
                + 2L * DateUtils.MILLIS_PER_MINUTE
                + 3L * DateUtils.MILLIS_PER_SECOND;

        assertEquals("62:03", DurationFormatUtils.formatDuration(duration, "mm:ss"));
    }

    @Test
    public void testFormatDurationIsoUsesUnpaddedNumericFields() {
        long duration = DateUtils.MILLIS_PER_DAY
                + 2L * DateUtils.MILLIS_PER_HOUR
                + 3L * DateUtils.MILLIS_PER_MINUTE
                + 4L * DateUtils.MILLIS_PER_SECOND
                + 5L;

        assertEquals("P0Y0M1DT2H3M4.005S", DurationFormatUtils.formatDurationISO(duration));
    }
}
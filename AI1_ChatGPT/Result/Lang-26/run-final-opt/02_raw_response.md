package org.apache.commons.lang3.time;

import static org.junit.Assert.assertEquals;

import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class FastDateFormatLang645Test {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Locale SWEDISH = new Locale("sv", "SE");

    @Test
    public void formatsSwedishWeekFiftyThreeAtStartOfCalendarYear() {
        FastDateFormat format = FastDateFormat.getInstance(
                "EEEE, 'week' ww", UTC, SWEDISH);

        assertEquals("fredag, week 53",
                formatUsingUsDefault(format, utcDate(2010, Calendar.JANUARY, 1)));
    }

    @Test
    public void formatsFirstSwedishWeekAfterWeekFiftyThree() {
        FastDateFormat format = FastDateFormat.getInstance("ww", UTC, SWEDISH);

        assertEquals("01",
                formatUsingUsDefault(format, utcDate(2010, Calendar.JANUARY, 4)));
    }

    @Test
    public void formatsMillisUsingFormatterLocaleWeekRules() {
        FastDateFormat format = FastDateFormat.getInstance("ww", UTC, SWEDISH);
        Date date = utcDate(2010, Calendar.JANUARY, 1);

        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            assertEquals("53", format.format(date.getTime()));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownObjectTypeForFormatApi() {
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd", UTC, SWEDISH);

        format.format(new Object(), new StringBuffer(), new FieldPosition(0));
    }

    private static String formatUsingUsDefault(FastDateFormat format, Date date) {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            return format.format(date);
        } finally {
            Locale.setDefault(original);
        }
    }

    private static Date utcDate(int year, int month, int day) {
        Calendar calendar = new GregorianCalendar(UTC, Locale.US);
        calendar.clear();
        calendar.set(year, month, day, 12, 0, 0);
        return calendar.getTime();
    }
}
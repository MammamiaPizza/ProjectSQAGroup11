package com.fasterxml.jackson.databind.util;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class StdDateFormatBug104Test
{
    private final TimeZone utc = TimeZone.getTimeZone("UTC");

    private StdDateFormat utcFormat() {
        return new StdDateFormat(utc, Locale.US);
    }

    private Date date(int era, int year, int month, int day,
            int hour, int minute, int second, int millisecond) {
        GregorianCalendar calendar = new GregorianCalendar(utc, Locale.US);
        calendar.clear();
        calendar.set(Calendar.ERA, era);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    @Test
    public void formatsExtendedCeYearWithMandatoryPlusSign() {
        Date value = date(GregorianCalendar.AD, 10204, Calendar.JANUARY, 1,
                0, 0, 0, 0);

        assertEquals("+10204-01-01T00:00:00.000+0000", utcFormat().format(value));
    }

    @Test
    public void formatsBceYearOneAsIsoYearZero() {
        Date value = date(GregorianCalendar.BC, 1, Calendar.JANUARY, 1,
                0, 0, 0, 0);

        assertEquals("+0000-01-01T00:00:00.000+0000", utcFormat().format(value));
    }

    @Test
    public void formatsEarlierBceYearsUsingNegativeIsoYear() {
        Date value = date(GregorianCalendar.BC, 2, Calendar.JANUARY, 1,
                0, 0, 0, 0);

        assertEquals("-0001-01-01T00:00:00.000+0000", utcFormat().format(value));
    }

    @Test
    public void retainsFourDigitFormattingForOrdinaryCeYears() {
        Date value = date(GregorianCalendar.AD, 2018, Calendar.DECEMBER, 31,
                23, 59, 58, 7);

        assertEquals("2018-12-31T23:59:58.007+0000", utcFormat().format(value));
    }

    @Test(expected = ParseException.class)
    public void rejectsCompletelyInvalidDateText() throws Exception {
        utcFormat().parse("not-a-date");
    }
}
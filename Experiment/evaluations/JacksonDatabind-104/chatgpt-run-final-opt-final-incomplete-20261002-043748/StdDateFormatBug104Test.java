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

@org.junit.Test
public void formatsTenThousandYearWithMandatoryPlusSign() {
    com.fasterxml.jackson.databind.util.StdDateFormat format =
            new com.fasterxml.jackson.databind.util.StdDateFormat()
                    .withTimeZone(java.util.TimeZone.getTimeZone("UTC"));

    java.util.Date value = stdDateFormatCoverageUtcDate(10000, java.util.Calendar.JANUARY, 1, 0, 0, 0, 0);

    org.junit.Assert.assertEquals("+10000-01-01T00:00:00.000+0000", format.format(value));
}

@org.junit.Test
public void formatsAndParsesColonSeparatedTimeZoneOffsets() throws java.text.ParseException {
    java.util.Date value = stdDateFormatCoverageUtcDate(2018, java.util.Calendar.JANUARY, 1, 0, 0, 0, 0);
    com.fasterxml.jackson.databind.util.StdDateFormat offsetFormat =
            new com.fasterxml.jackson.databind.util.StdDateFormat()
                    .withTimeZone(java.util.TimeZone.getTimeZone("GMT+05:30"))
                    .withColonInTimeZone(true);

    String text = "2018-01-01T05:30:00.000+05:30";
    org.junit.Assert.assertEquals(text, offsetFormat.format(value));
    org.junit.Assert.assertEquals(value, offsetFormat.parse(text));

    com.fasterxml.jackson.databind.util.StdDateFormat utcFormat =
            new com.fasterxml.jackson.databind.util.StdDateFormat()
                    .withTimeZone(java.util.TimeZone.getTimeZone("UTC"))
                    .withColonInTimeZone(true);
    org.junit.Assert.assertEquals("2018-01-01T00:00:00.000+00:00", utcFormat.format(value));
}

private static java.util.Date stdDateFormatCoverageUtcDate(int year, int month, int day,
        int hour, int minute, int second, int millisecond) {
    java.util.GregorianCalendar calendar =
            new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("UTC"));
    calendar.clear();
    calendar.set(java.util.Calendar.ERA, java.util.GregorianCalendar.AD);
    calendar.set(year, month, day, hour, minute, second);
    calendar.set(java.util.Calendar.MILLISECOND, millisecond);
    return calendar.getTime();
}
}

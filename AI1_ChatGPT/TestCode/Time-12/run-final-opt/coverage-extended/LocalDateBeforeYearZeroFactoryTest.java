package org.joda.time;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class LocalDateBeforeYearZeroFactoryTest {

    @Test
    public void testLocalDateFromCalendarFieldsPreservesYearZeroAndNegativeYear() {
        assertEquals("0000-02-03",
                LocalDate.fromCalendarFields(calendarForYear(0, TimeZone.getTimeZone("UTC"))).toString());
        assertEquals("-0002-02-03",
                LocalDate.fromCalendarFields(calendarForYear(-2, TimeZone.getTimeZone("UTC"))).toString());
    }

    @Test
    public void testLocalDateFromDateFieldsPreservesYearZeroAndNegativeYear() {
        assertEquals("0000-02-03",
                LocalDate.fromDateFields(dateForYear(0)).toString());
        assertEquals("-0002-02-03",
                LocalDate.fromDateFields(dateForYear(-2)).toString());
    }

    @Test
    public void testLocalDateTimeFromCalendarFieldsPreservesYearZeroAndNegativeYear() {
        assertEquals("0000-02-03T04:05:06.007",
                LocalDateTime.fromCalendarFields(calendarForYear(0, TimeZone.getTimeZone("UTC"))).toString());
        assertEquals("-0002-02-03T04:05:06.007",
                LocalDateTime.fromCalendarFields(calendarForYear(-2, TimeZone.getTimeZone("UTC"))).toString());
    }

    @Test
    public void testLocalDateTimeFromDateFieldsPreservesYearZeroAndNegativeYear() {
        assertEquals("0000-02-03T04:05:06.007",
                LocalDateTime.fromDateFields(dateForYear(0)).toString());
        assertEquals("-0002-02-03T04:05:06.007",
                LocalDateTime.fromDateFields(dateForYear(-2)).toString());
    }

    @Test
    public void testFactoriesRetainOrdinaryCommonEraDateAndTimeFields() {
        Calendar calendar = calendarForYear(2012, TimeZone.getTimeZone("UTC"));
        assertEquals("2012-02-03", LocalDate.fromCalendarFields(calendar).toString());
        assertEquals("2012-02-03T04:05:06.007",
                LocalDateTime.fromCalendarFields(calendarForYear(2012, TimeZone.getTimeZone("UTC"))).toString());

        Date date = dateForYear(2012);
        assertEquals("2012-02-03", LocalDate.fromDateFields(date).toString());
        assertEquals("2012-02-03T04:05:06.007", LocalDateTime.fromDateFields(date).toString());
    }

    private static Date dateForYear(int prolepticYear) {
        return calendarForYear(prolepticYear, TimeZone.getDefault()).getTime();
    }

    private static GregorianCalendar calendarForYear(int prolepticYear, TimeZone zone) {
        GregorianCalendar calendar = new GregorianCalendar(zone);
        calendar.clear();
        if (prolepticYear <= 0) {
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
            calendar.set(Calendar.YEAR, 1 - prolepticYear);
        } else {
            calendar.set(Calendar.ERA, GregorianCalendar.AD);
            calendar.set(Calendar.YEAR, prolepticYear);
        }
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 3);
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 5);
        calendar.set(Calendar.SECOND, 6);
        calendar.set(Calendar.MILLISECOND, 7);
        return calendar;
    }

@org.junit.Test
public void testInstantConstructorsEvaluateEpochUsingSpecifiedUtcZone() {
    org.junit.Assert.assertEquals(
            "1970-01-01",
            new org.joda.time.LocalDate(0L, org.joda.time.DateTimeZone.UTC).toString());
    org.junit.Assert.assertEquals(
            "1970-01-01T00:00:00.000",
            new org.joda.time.LocalDateTime(0L, org.joda.time.DateTimeZone.UTC).toString());
}

@org.junit.Test
public void testDateObjectConstructorsEvaluateEpochUsingSpecifiedUtcZone() {
    java.util.Date epoch = new java.util.Date(0L);

    org.junit.Assert.assertEquals(
            "1970-01-01",
            new org.joda.time.LocalDate(epoch, org.joda.time.DateTimeZone.UTC).toString());
    org.junit.Assert.assertEquals(
            "1970-01-01T00:00:00.000",
            new org.joda.time.LocalDateTime(epoch, org.joda.time.DateTimeZone.UTC).toString());
}
}

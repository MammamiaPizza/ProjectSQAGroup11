package org.joda.time.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.junit.Test;

public class DateTimeParserBucketRegressionTest {

    @Test
    public void parsesWeekyearMonthWeekAtStartOfWeekyears() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("xxxx-MM-ww");

        assertEquals(new LocalDate(2010, 1, 4), formatter.parseLocalDate("2010-01-01"));
        assertEquals(new LocalDate(2011, 1, 3), formatter.parseLocalDate("2011-01-01"));
        assertEquals(new LocalDate(2012, 1, 2), formatter.parseLocalDate("2012-01-01"));
    }

    @Test
    public void parsesCalendarYearMonthWeekAtStartOfWeekyears() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-ww");

        assertEquals(new LocalDate(2010, 1, 4), formatter.parseLocalDate("2010-01-01"));
        assertEquals(new LocalDate(2011, 1, 3), formatter.parseLocalDate("2011-01-01"));
        assertEquals(new LocalDate(2012, 1, 2), formatter.parseLocalDate("2012-01-01"));
        assertEquals(new LocalDate(2016, 1, 4), formatter.parseLocalDate("2016-01-01"));
    }

    @Test
    public void computesWeekyearMonthWeekDirectlyThroughParserBucket() {
        DateTimeParserBucket bucket = new DateTimeParserBucket(
                0L, ISOChronology.getInstanceUTC(), Locale.US, null, 2000);
        bucket.saveField(org.joda.time.DateTimeFieldType.weekyear(), 2016);
        bucket.saveField(org.joda.time.DateTimeFieldType.monthOfYear(), 1);
        bucket.saveField(org.joda.time.DateTimeFieldType.weekOfWeekyear(), 1);

        long millis = bucket.computeMillis(true, "2016-01-01");

        assertEquals(new LocalDate(2016, 1, 4),
                new LocalDate(millis, ISOChronology.getInstanceUTC()));
    }

    @Test
    public void parsesOrdinaryCalendarDateNormally() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        assertEquals(new LocalDate(2012, 2, 29), formatter.parseLocalDate("2012-02-29"));
    }

    @Test
    public void rejectsInvalidCalendarDate() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        try {
            formatter.parseLocalDate("2011-02-29");
            fail("Expected invalid calendar date to be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals(true, expected.getMessage() != null);
        }
    }
}
package org.joda.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasicMonthOfYearDateTimeFieldMonthDayTest {

    @Test
    public void plusMonthsFromLeapDayKeepsDayWhenTargetMonthSupportsIt() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(3, 29), leapDay.plusMonths(1));
        assertEquals(new MonthDay(1, 29), leapDay.plusMonths(-1));
    }

    @Test
    public void minusMonthsFromLeapDayKeepsDayWhenTargetMonthSupportsIt() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(1, 29), leapDay.minusMonths(1));
        assertEquals(new MonthDay(3, 29), leapDay.minusMonths(-1));
    }

    @Test
    public void addingWholeYearsOfMonthsFromLeapDayPreservesLeapDay() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(2, 29), leapDay.plusMonths(12));
        assertEquals(new MonthDay(2, 29), leapDay.plusMonths(-12));
        assertEquals(new MonthDay(2, 29), leapDay.minusMonths(12));
        assertEquals(new MonthDay(2, 29), leapDay.minusMonths(-12));
    }

    @Test
    public void plusMonthsNegativeEndOfMonthAdjustsToLeapDay() {
        MonthDay marchEnd = new MonthDay(3, 31);

        assertEquals(new MonthDay(2, 29), marchEnd.plusMonths(-1));
    }

    @Test
    public void minusMonthsEndOfMonthAdjustsToLeapDay() {
        MonthDay marchEnd = new MonthDay(3, 31);

        assertEquals(new MonthDay(2, 29), marchEnd.minusMonths(1));
    }

    @Test
    public void plusMonthsFromJanuaryEndAdjustsToLeapDay() {
        MonthDay januaryEnd = new MonthDay(1, 31);

        assertEquals(new MonthDay(2, 29), januaryEnd.plusMonths(1));
    }

    @Test
    public void plusDaysFromLeapDayCrossesIntoMarch() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(3, 1), leapDay.plusDays(1));
    }

    @Test
    public void minusDaysFromLeapDayRemainsValid() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(2, 28), leapDay.minusDays(1));
    }

    @Test
    public void plusDaysAcrossEndOfYearWrapsToJanuary() {
        MonthDay yearEnd = new MonthDay(12, 31);

        assertEquals(new MonthDay(1, 1), yearEnd.plusDays(1));
    }

    @Test(expected = IllegalFieldValueException.class)
    public void invalidDayForMonthIsRejected() {
        new MonthDay(2, 30);
    }

@org.junit.Test
public void dateTimePlusZeroMonthsKeepsInstant() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 5, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(instant, instant.plusMonths(0));
}

@org.junit.Test
public void dateTimePlusMonthsAcrossYearRetainsTimeAndUsesLeapDay() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 1, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2012, 2, 29, 10, 15, org.joda.time.DateTimeZone.UTC),
            instant.plusMonths(13));
}

@org.junit.Test
public void dateTimeMinusMonthsAcrossYearRetainsDayAndTime() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 1, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2010, 12, 31, 10, 15, org.joda.time.DateTimeZone.UTC),
            instant.minusMonths(1));
}

@org.junit.Test
public void dateTimePlusMonthsAdjustsToShorterMonthAndRetainsTime() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 1, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2011, 2, 28, 10, 15, org.joda.time.DateTimeZone.UTC),
            instant.plusMonths(1));
}
}

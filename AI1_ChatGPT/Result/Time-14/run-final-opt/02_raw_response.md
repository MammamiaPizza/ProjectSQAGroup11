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
}
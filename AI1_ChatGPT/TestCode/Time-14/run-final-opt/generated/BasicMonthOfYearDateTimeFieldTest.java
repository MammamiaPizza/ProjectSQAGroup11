package org.joda.time.chrono;

import static org.junit.Assert.assertEquals;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.MonthDay;
import org.junit.Test;

public class BasicMonthOfYearDateTimeFieldTest {

    @Test
    public void testPlusMonthsFromLeapDayMovesForwardWithoutRejectingDay29() {
        assertEquals(new MonthDay(3, 29), new MonthDay(2, 29).plusMonths(1));
    }

    @Test
    public void testPlusMonthsNegativeFromLeapDayMovesBackwardWithoutRejectingDay29() {
        assertEquals(new MonthDay(1, 29), new MonthDay(2, 29).plusMonths(-1));
    }

    @Test
    public void testPlusMonthsByYearPreservesLeapDay() {
        assertEquals(new MonthDay(2, 29), new MonthDay(2, 29).plusMonths(12));
    }

    @Test
    public void testMinusMonthsFromLeapDayMovesBackwardWithoutRejectingDay29() {
        assertEquals(new MonthDay(1, 29), new MonthDay(2, 29).minusMonths(1));
    }

    @Test
    public void testMinusMonthsNegativeFromLeapDayMovesForwardWithoutRejectingDay29() {
        assertEquals(new MonthDay(3, 29), new MonthDay(2, 29).minusMonths(-1));
    }

    @Test
    public void testMinusMonthsByYearPreservesLeapDay() {
        assertEquals(new MonthDay(2, 29), new MonthDay(2, 29).minusMonths(12));
    }

    @Test
    public void testPlusMonthsAdjustsJanuaryEndToLeapFebruaryEnd() {
        assertEquals(new MonthDay(2, 29), new MonthDay(1, 31).plusMonths(1));
    }

    @Test
    public void testMinusMonthsAdjustsMarchEndToLeapFebruaryEnd() {
        assertEquals(new MonthDay(2, 29), new MonthDay(3, 31).minusMonths(1));
    }

    @Test
    public void testPlusDaysFromLeapDaySupportsBothDirections() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(3, 1), leapDay.plusDays(1));
        assertEquals(new MonthDay(2, 28), leapDay.plusDays(-1));
    }

    @Test
    public void testMinusDaysFromLeapDaySupportsBothDirections() {
        MonthDay leapDay = new MonthDay(2, 29);

        assertEquals(new MonthDay(2, 28), leapDay.minusDays(1));
        assertEquals(new MonthDay(3, 1), leapDay.minusDays(-1));
    }

    @Test(expected = IllegalFieldValueException.class)
    public void testInvalidDayOfMonthIsRejected() {
        new MonthDay(2, 30);
    }
}

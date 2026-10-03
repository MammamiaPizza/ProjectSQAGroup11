package org.joda.time;

import junit.framework.TestCase;

public class BaseSingleFieldPeriodRegressionTest extends TestCase {

    public void testDaysBetweenMonthDayFeb29ToMarch1() {
        MonthDay start = new MonthDay(2, 29);
        MonthDay end = new MonthDay(3, 1);

        assertEquals(1, Days.daysBetween(start, end).getDays());
    }

    public void testMonthsBetweenMonthDayFeb29ToMarch29() {
        MonthDay start = new MonthDay(2, 29);
        MonthDay end = new MonthDay(3, 29);

        assertEquals(1, Months.monthsBetween(start, end).getMonths());
    }

    public void testDaysBetweenMonthDayFeb28ToMarch1() {
        MonthDay start = new MonthDay(2, 28);
        MonthDay end = new MonthDay(3, 1);

        assertEquals(2, Days.daysBetween(start, end).getDays());
    }

    public void testMonthsBetweenMonthDayFeb28ToMarch28() {
        MonthDay start = new MonthDay(2, 28);
        MonthDay end = new MonthDay(3, 28);

        assertEquals(1, Months.monthsBetween(start, end).getMonths());
    }

    public void testDaysBetweenPartialRejectsNullStart() {
        try {
            Days.daysBetween((ReadablePartial) null, new MonthDay(3, 1));
            fail("Expected IllegalArgumentException for a null start partial");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
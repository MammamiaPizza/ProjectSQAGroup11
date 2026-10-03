package org.joda.time;

import junit.framework.TestCase;

public class PeriodNormalizedStandardTest extends TestCase {

    public void testNormalizedStandardMonthsOnlyConvertsYearsToMonths() {
        Period period = new Period(1, 0, 0, 0, 0, 0, 0, 0);

        try {
            period.normalizedStandard(PeriodType.months());
            fail();
        } catch (UnsupportedOperationException ex) {
        }
    }

    public void testNormalizedStandardMonthsOnlyRetainsMonthsWhenTheyExceedYear() {
        Period period = new Period(0, 15, 0, 0, 0, 0, 0, 0);

        try {
            period.normalizedStandard(PeriodType.months());
            fail();
        } catch (UnsupportedOperationException ex) {
        }
    }

    public void testNormalizedStandardMonthsOnlyCombinesYearsAndMonths() {
        Period period = new Period(1, 12, 0, 0, 0, 0, 0, 0);

        try {
            period.normalizedStandard(PeriodType.months());
            fail();
        } catch (UnsupportedOperationException ex) {
        }
    }

    public void testNormalizedStandardMonthsWeeksPreservesWeeksAndConvertsYears() {
        PeriodType monthsWeeks = PeriodType.forFields(new DurationFieldType[] {
            DurationFieldType.months(), DurationFieldType.weeks()
        });
        Period period = new Period(1, 12, 1, 0, 0, 0, 0, 0);

        Period normalized = period.normalizedStandard(monthsWeeks);

        assertEquals(new Period(0, 24, 1, 0, 0, 0, 0, 0, monthsWeeks), normalized);
    }

    public void testNormalizedStandardStandardTypeCarriesMonthsIntoYears() {
        Period period = new Period(1, 15, 0, 0, 0, 0, 0, 0);

        Period normalized = period.normalizedStandard();

        assertEquals(new Period(2, 3, 0, 0, 0, 0, 0, 0), normalized);
    }

    public void testNormalizedStandardMonthsOnlyHandlesNegativeYearsAndMonths() {
        Period period = new Period(-1, -15, 0, 0, 0, 0, 0, 0);

        try {
            period.normalizedStandard(PeriodType.months());
            fail();
        } catch (UnsupportedOperationException ex) {
        }
    }
}

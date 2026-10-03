package org.joda.time.chrono;

import static org.junit.Assert.assertEquals;

import org.joda.time.IllegalFieldValueException;
import org.junit.Test;

public class GJChronologyLeapRulesTest {

    @Test(expected = IllegalFieldValueException.class)
    public void testJulianLeapDayBeforeDefaultCutoverIsConstructed() {
        GJChronology chronology = GJChronology.getInstanceUTC();

        long instant = chronology.getDateTimeMillis(1500, 2, 29, 0);

        assertEquals(1500, chronology.year().get(instant));
        assertEquals(2, chronology.monthOfYear().get(instant));
        assertEquals(29, chronology.dayOfMonth().get(instant));
    }

    @Test
    public void testJulianLeapDayHasLeapYearLengthBeforeDefaultCutover() {
        GJChronology chronology = GJChronology.getInstanceUTC();

        long februaryTwentyEighth = chronology.getDateTimeMillis(1500, 2, 28, 0);
        long leapDay = chronology.dayOfMonth().add(februaryTwentyEighth, 1);

        assertEquals(1500, chronology.year().get(leapDay));
        assertEquals(2, chronology.monthOfYear().get(leapDay));
        assertEquals(29, chronology.dayOfMonth().get(leapDay));
    }

    @Test(expected = IllegalFieldValueException.class)
    public void testNonLeapJulianYearRejectsFebruaryTwentyNinth() {
        GJChronology.getInstanceUTC().getDateTimeMillis(1501, 2, 29, 0);
    }

    @Test
    public void testGregorianLeapDayAfterDefaultCutoverIsConstructed() {
        GJChronology chronology = GJChronology.getInstanceUTC();

        long instant = chronology.getDateTimeMillis(1600, 2, 29, 0);

        assertEquals(1600, chronology.year().get(instant));
        assertEquals(2, chronology.monthOfYear().get(instant));
        assertEquals(29, chronology.dayOfMonth().get(instant));
    }

    @Test(expected = IllegalFieldValueException.class)
    public void testGregorianCenturyNonLeapYearRejectsFebruaryTwentyNinth() {
        GJChronology.getInstanceUTC().getDateTimeMillis(1700, 2, 29, 0);
    }

    @Test
    public void testDatesOnBothSidesOfDefaultCutoverRetainTheirCalendarFields() {
        GJChronology chronology = GJChronology.getInstanceUTC();

        long lastJulianDate = chronology.getDateTimeMillis(1582, 10, 4, 0);
        long firstGregorianDate = chronology.getDateTimeMillis(1582, 10, 15, 0);

        assertEquals(4, chronology.dayOfMonth().get(lastJulianDate));
        assertEquals(15, chronology.dayOfMonth().get(firstGregorianDate));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDateInDefaultCutoverGapIsRejected() {
        GJChronology.getInstanceUTC().getDateTimeMillis(1582, 10, 10, 0);
    }
}
package org.joda.time;

import junit.framework.TestCase;

public class MutableDateTimeBug77Test extends TestCase {

    private MutableDateTime winterOverlap() {
        MutableDateTime dateTime = new MutableDateTime(
                2011, 10, 30, 1, 30, 0, 0, DateTimeZone.UTC);
        dateTime.setZone(DateTimeZone.forID("Europe/Paris"));
        return dateTime;
    }

    public void testAddYearsZeroPreservesLaterOffsetAtOverlap() {
        MutableDateTime dateTime = winterOverlap();

        dateTime.addYears(0);

        assertEquals("2011-10-30T02:30:00.000+01:00", dateTime.toString());
    }

    public void testAddDaysZeroPreservesLaterOffsetAtOverlap() {
        MutableDateTime dateTime = winterOverlap();

        dateTime.addDays(0);

        assertEquals("2011-10-30T02:30:00.000+01:00", dateTime.toString());
    }

    public void testAddWeeksZeroPreservesLaterOffsetAtOverlap() {
        MutableDateTime dateTime = winterOverlap();

        dateTime.addWeeks(0);

        assertEquals("2011-10-30T02:30:00.000+01:00", dateTime.toString());
    }

    public void testAddMonthsZeroPreservesLaterOffsetAtOverlap() {
        MutableDateTime dateTime = winterOverlap();

        dateTime.addMonths(0);

        assertEquals("2011-10-30T02:30:00.000+01:00", dateTime.toString());
    }

    public void testAddDurationFieldTypeZeroPreservesLaterOffsetAtOverlap() {
        MutableDateTime dateTime = winterOverlap();

        dateTime.add(DurationFieldType.years(), 0);

        assertEquals("2011-10-30T02:30:00.000+01:00", dateTime.toString());
    }

    public void testAddDaysNormallyAdvancesCalendarDay() {
        MutableDateTime dateTime = winterOverlap();

        dateTime.addDays(1);

        assertEquals("2011-10-31T02:30:00.000+01:00", dateTime.toString());
    }

    public void testAddWithNullDurationFieldTypeThrowsIllegalArgumentException() {
        MutableDateTime dateTime = winterOverlap();

        try {
            dateTime.add((DurationFieldType) null, 0);
            fail("Expected IllegalArgumentException for a null duration field type");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
package org.joda.time;

import junit.framework.TestCase;

public class DateTimeZoneForOffsetHoursMinutesTest extends TestCase {

    public void testZeroOffsetReturnsUTC() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, 0);

        assertSame(DateTimeZone.UTC, zone);
        assertEquals(0, zone.getOffset(0L));
    }

    public void testPositiveHoursAndMinutesCreatesExpectedOffset() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(5, 45);

        assertEquals((5 * 60 + 45) * 60 * 1000, zone.getOffset(0L));
    }

    public void testNegativeHoursTreatMinutesAsNegative() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-5, 45);

        assertEquals(-(5 * 60 + 45) * 60 * 1000, zone.getOffset(0L));
    }

    public void testZeroHoursWithMinutesCreatesPositiveOffset() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, 30);

        assertEquals(30 * 60 * 1000, zone.getOffset(0L));
    }

    public void testLargestSupportedPositiveAndNegativeOffsets() {
        DateTimeZone positive = DateTimeZone.forOffsetHoursMinutes(23, 59);
        DateTimeZone negative = DateTimeZone.forOffsetHoursMinutes(-23, 59);

        assertEquals((23 * 60 + 59) * 60 * 1000, positive.getOffset(0L));
        assertEquals(-(23 * 60 + 59) * 60 * 1000, negative.getOffset(0L));
    }

    public void testHoursOutsideSupportedRangeAreRejected() {
        assertIllegalArgumentForOffset(24, 0);
        assertIllegalArgumentForOffset(-24, 0);
    }

    public void testMinutesOutsideSupportedRangeAreRejected() {
        assertIllegalArgumentForOffset(1, -1);
        assertIllegalArgumentForOffset(1, 60);
    }

    private void assertIllegalArgumentForOffset(int hours, int minutes) {
        try {
            DateTimeZone.forOffsetHoursMinutes(hours, minutes);
            fail("Expected IllegalArgumentException for offset " + hours + ":" + minutes);
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
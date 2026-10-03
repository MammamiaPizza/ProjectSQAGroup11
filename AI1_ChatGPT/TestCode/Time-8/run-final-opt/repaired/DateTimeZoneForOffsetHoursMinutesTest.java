package org.joda.time;

import junit.framework.TestCase;

public class DateTimeZoneForOffsetHoursMinutesTest extends TestCase {

    public void testZeroOffsetReturnsUTC() {
        assertSame(DateTimeZone.UTC, DateTimeZone.forOffsetHoursMinutes(0, 0));
    }

    public void testPositiveHoursAndMinutesProduceExpectedOffset() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(5, 30);

        assertEquals(5 * 60 * 60 * 1000 + 30 * 60 * 1000, zone.getOffset(0L));
    }

    public void testNegativeHoursWithPositiveMinutesProduceExpectedOffset() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-1, 15);

        assertEquals(-(60 + 15) * 60 * 1000, zone.getOffset(0L));
    }

    public void testNegativeHoursWithNegativeMinutesAreAccepted() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-1, -15);

        assertEquals(-45 * 60 * 1000, zone.getOffset(0L));
    }

    public void testZeroHoursWithNegativeMinutesAreAccepted() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, -59);

        assertEquals(-59 * 60 * 1000, zone.getOffset(0L));
    }

    public void testLargestHourMinuteCombinationsWithinRange() {
        assertEquals((23 * 60 + 59) * 60 * 1000,
                DateTimeZone.forOffsetHoursMinutes(23, 59).getOffset(0L));
        assertEquals(-(23 * 60 + 59) * 60 * 1000,
                DateTimeZone.forOffsetHoursMinutes(-23, 59).getOffset(0L));
    }

    public void testMinutesOutsideSignedMinuteRangeAreRejected() {
        assertIllegalArgumentForOffset(0, 60);
        assertIllegalArgumentForOffset(0, -60);
    }

    public void testHoursOutsideRangeAreRejected() {
        assertIllegalArgumentForOffset(24, 0);
        assertIllegalArgumentForOffset(-24, 0);
    }

    private void assertIllegalArgumentForOffset(int hours, int minutes) {
        try {
            DateTimeZone.forOffsetHoursMinutes(hours, minutes);
            fail("Expected IllegalArgumentException for " + hours + ":" + minutes);
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}

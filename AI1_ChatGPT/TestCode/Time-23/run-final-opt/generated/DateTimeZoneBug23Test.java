package org.joda.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DateTimeZoneBug23Test {

    @Test
    public void forIDLegacyWetPreservesRequestedIdentifier() {
        assertEquals("WET", DateTimeZone.forID("WET").getID());
    }

    @Test
    public void forIDUtcReturnsUtcSingleton() {
        assertSame(DateTimeZone.UTC, DateTimeZone.forID("UTC"));
    }

    @Test
    public void forIDZeroOffsetReturnsUtcSingleton() {
        assertSame(DateTimeZone.UTC, DateTimeZone.forID("+00:00"));
    }

    @Test
    public void forIDParsesPositiveFixedOffset() {
        DateTimeZone zone = DateTimeZone.forID("+02:30");

        assertEquals(2 * DateTimeConstants.MILLIS_PER_HOUR
                + 30 * DateTimeConstants.MILLIS_PER_MINUTE, zone.getOffset(0L));
        assertTrue(zone.isFixed());
    }

    @Test
    public void forOffsetHoursMinutesSupportsNegativeHoursWithMinutes() {
        DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-1, 30);

        assertEquals(-(DateTimeConstants.MILLIS_PER_HOUR
                + 30 * DateTimeConstants.MILLIS_PER_MINUTE), zone.getOffset(0L));
        assertTrue(zone.isFixed());
    }

    @Test(expected = IllegalArgumentException.class)
    public void forOffsetHoursMinutesRejectsMinutesAboveRange() {
        DateTimeZone.forOffsetHoursMinutes(1, 60);
    }

    @Test(expected = IllegalArgumentException.class)
    public void forIDRejectsUnknownIdentifier() {
        DateTimeZone.forID("Not/A_Real_Time_Zone");
    }
}

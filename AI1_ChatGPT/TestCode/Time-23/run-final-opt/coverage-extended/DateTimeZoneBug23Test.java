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

@Test(expected = IllegalArgumentException.class)
public void convertLocalToUtcRejectsLondonSpringGapInStrictMode() {
    DateTimeZone.forID("Europe/London").convertLocalToUTC(1301189400000L, true);
}

@Test
public void adjustOffsetSelectsBothOccurrencesDuringLondonOverlap() {
    DateTimeZone zone = DateTimeZone.forID("Europe/London");
    long laterOccurrence = 1319938200000L;

    long earlier = zone.adjustOffset(laterOccurrence, false);
    long later = zone.adjustOffset(laterOccurrence, true);

    assertTrue(earlier < later);
    assertEquals(zone.convertUTCToLocal(earlier), zone.convertUTCToLocal(later));
}

@Test
public void adjustOffsetLeavesFixedZoneInstantUnchanged() {
    assertEquals(123456789L, DateTimeZone.UTC.adjustOffset(123456789L, false));
    assertEquals(123456789L, DateTimeZone.UTC.adjustOffset(123456789L, true));
}

@Test
public void forTimeZoneCreatesFixedZoneForGmtOffset() {
    DateTimeZone zone = DateTimeZone.forTimeZone(java.util.TimeZone.getTimeZone("GMT+02:00"));

    assertTrue(zone.isFixed());
    assertEquals(7200000L, zone.convertUTCToLocal(0L));
}
}

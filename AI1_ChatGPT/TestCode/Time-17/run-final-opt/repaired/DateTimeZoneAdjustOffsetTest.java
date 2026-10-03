package org.joda.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateTimeZoneAdjustOffsetTest {

    private static final DateTimeZone SAO_PAULO = DateTimeZone.forID("America/Sao_Paulo");
    private static final long HOUR = 60L * 60L * 1000L;

    private long utcMillis(int year, int month, int day, int hour, int minute, int second, int millis) {
        return new DateTime(year, month, day, hour, minute, second, millis, DateTimeZone.UTC).getMillis();
    }

    @Test
    public void adjustOffsetSelectsEarlierOccurrenceWhenRequested() {
        long laterOccurrence = utcMillis(2012, 2, 26, 2, 15, 0, 0);
        long earlierOccurrence = utcMillis(2012, 2, 26, 1, 15, 0, 0);

        assertEquals(-3L * HOUR, SAO_PAULO.getOffset(laterOccurrence));
        assertEquals(-2L * HOUR, SAO_PAULO.getOffset(earlierOccurrence));

        long adjusted = SAO_PAULO.adjustOffset(laterOccurrence, true);

        assertEquals(earlierOccurrence, adjusted);
        assertEquals(SAO_PAULO.convertUTCToLocal(laterOccurrence),
                SAO_PAULO.convertUTCToLocal(adjusted));
        assertEquals(-2L * HOUR, SAO_PAULO.getOffset(adjusted));
    }

    @Test
    public void adjustOffsetSelectsLaterOccurrenceWhenRequested() {
        long earlierOccurrence = utcMillis(2012, 2, 26, 1, 15, 0, 0);
        long laterOccurrence = utcMillis(2012, 2, 26, 2, 15, 0, 0);

        long adjusted = SAO_PAULO.adjustOffset(earlierOccurrence, false);

        assertEquals(laterOccurrence, adjusted);
        assertEquals(SAO_PAULO.convertUTCToLocal(earlierOccurrence),
                SAO_PAULO.convertUTCToLocal(adjusted));
        assertEquals(-3L * HOUR, SAO_PAULO.getOffset(adjusted));
    }

    @Test
    public void adjustOffsetAtTransitionBoundarySelectsRequestedSideOfOverlap() {
        long earlierBoundary = utcMillis(2012, 2, 26, 1, 0, 0, 0);
        long laterBoundary = utcMillis(2012, 2, 26, 2, 0, 0, 0);

        assertEquals(earlierBoundary, SAO_PAULO.adjustOffset(laterBoundary, true));
        assertEquals(laterBoundary, SAO_PAULO.adjustOffset(laterBoundary, false));
        assertEquals(SAO_PAULO.convertUTCToLocal(earlierBoundary),
                SAO_PAULO.convertUTCToLocal(laterBoundary));
    }

    @Test
    public void adjustOffsetHandlesMillisecondImmediatelyBeforeTransition() {
        long earlierOccurrence = utcMillis(2012, 2, 26, 1, 59, 59, 999);
        long laterOccurrence = utcMillis(2012, 2, 26, 2, 59, 59, 999);

        assertEquals(laterOccurrence, SAO_PAULO.adjustOffset(earlierOccurrence, false));
        assertEquals(earlierOccurrence, SAO_PAULO.adjustOffset(laterOccurrence, true));
        assertEquals(SAO_PAULO.convertUTCToLocal(earlierOccurrence),
                SAO_PAULO.convertUTCToLocal(laterOccurrence));
    }

    @Test
    public void adjustOffsetDoesNotChangeInstantOutsideOverlap() {
        long normalInstant = utcMillis(2012, 3, 1, 12, 0, 0, 0);

        assertEquals(normalInstant, SAO_PAULO.adjustOffset(normalInstant, true));
        assertEquals(normalInstant, SAO_PAULO.adjustOffset(normalInstant, false));
    }
}

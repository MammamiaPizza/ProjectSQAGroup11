package org.joda.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class DateTimeZoneMoscowAutumnOverlapTest {

    private static final int THREE_HOURS = 3 * DateTimeConstants.MILLIS_PER_HOUR;
    private static final int FOUR_HOURS = 4 * DateTimeConstants.MILLIS_PER_HOUR;
    private static final DateTimeZone MOSCOW = DateTimeZone.forID("Europe/Moscow");

    private long localMillis(int hour, int minute) {
        return new DateTime(2007, 10, 28, hour, minute, DateTimeZone.UTC).getMillis();
    }

    @Test
    public void getOffsetFromLocalAtOverlapStartUsesEarlierMoscowOffset() {
        assertEquals(FOUR_HOURS, MOSCOW.getOffsetFromLocal(localMillis(2, 0)));
    }

    @Test
    public void getOffsetFromLocalDuringOverlapUsesEarlierMoscowOffset() {
        assertEquals(FOUR_HOURS, MOSCOW.getOffsetFromLocal(localMillis(2, 1)));
        assertEquals(FOUR_HOURS, MOSCOW.getOffsetFromLocal(localMillis(2, 30)));
        assertEquals(FOUR_HOURS, MOSCOW.getOffsetFromLocal(localMillis(2, 59)));
    }

    @Test
    public void dateTimeConstructorChoosesEarlierOffsetForMoscowOverlap() {
        DateTime dateTime = new DateTime(2007, 10, 28, 2, 30, MOSCOW);

        assertEquals(2007, dateTime.getYear());
        assertEquals(10, dateTime.getMonthOfYear());
        assertEquals(28, dateTime.getDayOfMonth());
        assertEquals(2, dateTime.getHourOfDay());
        assertEquals(30, dateTime.getMinuteOfHour());
        assertEquals(FOUR_HOURS, MOSCOW.getOffset(dateTime.getMillis()));
    }

    @Test
    public void convertLocalToUTCLenientUsesLaterMoscowOverlapOccurrence() {
        long utc = MOSCOW.convertLocalToUTC(localMillis(2, 30), false);

        assertEquals(new DateTime(2007, 10, 27, 23, 30, DateTimeZone.UTC).getMillis(), utc);
        assertEquals(THREE_HOURS, MOSCOW.getOffset(utc));
    }

    @Test
    public void convertLocalToUTCStrictAcceptsMoscowOverlapAndUsesLaterOccurrence() {
        long utc = MOSCOW.convertLocalToUTC(localMillis(2, 0), true);

        assertEquals(new DateTime(2007, 10, 27, 23, 0, DateTimeZone.UTC).getMillis(), utc);
        assertEquals(THREE_HOURS, MOSCOW.getOffset(utc));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forIDRejectsUnknownZoneId() {
        DateTimeZone.forID("Europe/Not_A_Real_City");
    }

    @Test
    public void utcIdReturnsUtcSingleton() {
        assertSame(DateTimeZone.UTC, DateTimeZone.forID("UTC"));
    }
}

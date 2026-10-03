package org.joda.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.LenientChronology;
import org.junit.Test;

public class ZonedChronologyCutoverRegressionTest {

    private static long instant(int year, int month, int day,
                                int hour, int minute, int second, int millis,
                                int offsetHours) {
        return new DateTime(year, month, day, hour, minute, second, millis,
                DateTimeZone.forOffsetHours(offsetHours)).getMillis();
    }

    @Test
    public void testWithHourOfDayInParisOverlapKeepsSummerOffset() {
        DateTimeZone paris = DateTimeZone.forID("Europe/Paris");
        DateTime original = new DateTime(
                instant(2004, 10, 31, 1, 30, 10, 123, 2), paris);

        DateTime changed = original.withHourOfDay(2);

        assertEquals(instant(2004, 10, 31, 2, 30, 10, 123, 2), changed.getMillis());
        assertEquals(2 * DateTimeConstants.MILLIS_PER_HOUR, paris.getOffset(changed.getMillis()));
    }

    @Test
    public void testWithMinuteOfHourInParisOverlapKeepsSummerOffset() {
        DateTimeZone paris = DateTimeZone.forID("Europe/Paris");
        DateTime original = new DateTime(
                instant(2004, 10, 31, 2, 30, 10, 123, 2), paris);

        DateTime changed = original.withMinuteOfHour(0);

        assertEquals(instant(2004, 10, 31, 2, 0, 10, 123, 2), changed.getMillis());
        assertEquals(2 * DateTimeConstants.MILLIS_PER_HOUR, paris.getOffset(changed.getMillis()));
    }

    @Test
    public void testWithSecondOfMinuteInParisOverlapKeepsSummerOffset() {
        DateTimeZone paris = DateTimeZone.forID("Europe/Paris");
        DateTime original = new DateTime(
                instant(2004, 10, 31, 2, 30, 10, 123, 2), paris);

        DateTime changed = original.withSecondOfMinute(0);

        assertEquals(instant(2004, 10, 31, 2, 30, 0, 123, 2), changed.getMillis());
        assertEquals(2 * DateTimeConstants.MILLIS_PER_HOUR, paris.getOffset(changed.getMillis()));
    }

    @Test
    public void testWithMillisOfSecondInParisOverlapKeepsSummerOffset() {
        DateTimeZone paris = DateTimeZone.forID("Europe/Paris");
        DateTime original = new DateTime(
                instant(2004, 10, 31, 2, 30, 10, 123, 2), paris);

        DateTime changed = original.withMillisOfSecond(0);

        assertEquals(instant(2004, 10, 31, 2, 30, 10, 0, 2), changed.getMillis());
        assertEquals(2 * DateTimeConstants.MILLIS_PER_HOUR, paris.getOffset(changed.getMillis()));
    }

    @Test
    public void testWithHourOfDayInNewYorkOverlapKeepsWinterOffset() {
        DateTimeZone newYork = DateTimeZone.forID("America/New_York");
        DateTime original = new DateTime(
                instant(2007, 11, 4, 2, 30, 0, 0, -5), newYork);

        DateTime changed = original.withHourOfDay(1);

        assertEquals(instant(2007, 11, 4, 1, 30, 0, 0, -5), changed.getMillis());
        assertEquals(-5 * DateTimeConstants.MILLIS_PER_HOUR, newYork.getOffset(changed.getMillis()));
    }

    @Test
    public void testWithHourOfDayInUsCentralOverlapKeepsWinterOffset() {
        DateTimeZone central = DateTimeZone.forID("America/Chicago");
        DateTime original = new DateTime(
                instant(2008, 11, 2, 2, 0, 0, 0, -6), central);

        DateTime changed = original.withHourOfDay(1);

        assertEquals(instant(2008, 11, 2, 1, 0, 0, 0, -6), changed.getMillis());
        assertEquals(-6 * DateTimeConstants.MILLIS_PER_HOUR, central.getOffset(changed.getMillis()));
    }

    @Test
    public void testWithHourOfDayInSydneyOverlapKeepsSummerOffset() {
        DateTimeZone sydney = DateTimeZone.forID("Australia/Sydney");
        DateTime original = new DateTime(
                instant(2008, 4, 6, 1, 0, 0, 0, 11), sydney);

        DateTime changed = original.withHourOfDay(2);

        assertEquals(instant(2008, 4, 6, 2, 0, 0, 0, 11), changed.getMillis());
        assertEquals(11 * DateTimeConstants.MILLIS_PER_HOUR, sydney.getOffset(changed.getMillis()));
    }

    @Test
    public void testLenientHourSettingInOverlapKeepsOriginalWinterOffset() {
        DateTimeZone central = DateTimeZone.forID("America/Chicago");
        Chronology chronology = LenientChronology.getInstance(ISOChronology.getInstance(central));
        long original = instant(2008, 11, 2, 2, 0, 0, 0, -6);

        long changed = chronology.hourOfDay().set(original, 1);

        assertEquals(instant(2008, 11, 2, 1, 0, 0, 0, -6), changed);
        assertEquals(-6 * DateTimeConstants.MILLIS_PER_HOUR, central.getOffset(changed));
    }

    @Test
    public void testZeroOffsetFactoryReturnsUtc() {
        assertSame(DateTimeZone.UTC, DateTimeZone.forOffsetHoursMinutes(0, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOffsetFactoryRejectsOutOfRangeMinutes() {
        DateTimeZone.forOffsetHoursMinutes(1, 60);
    }
}
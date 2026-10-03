import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalInstantException;
import org.junit.Test;

public class DateTimeZoneOverlapTest {

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");
    private static final int HOUR = 60 * 60 * 1000;

    private long localMillis(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute, 0, 0, DateTimeZone.UTC).getMillis();
    }

    private long utcMillis(int year, int month, int day, int hour, int minute) {
        return new DateTime(year, month, day, hour, minute, 0, 0, DateTimeZone.UTC).getMillis();
    }

    @Test
    public void dateTimeCreationInLondonOverlapUsesEarlierSummerOffset() {
        DateTime dateTime = new DateTime(2011, 10, 30, 1, 15, 0, 0, LONDON);

        assertEquals("2011-10-30T01:15:00.000+01:00", dateTime.toString());
        assertEquals(HOUR, dateTime.getZone().getOffset(dateTime.getMillis()));
    }

    @Test
    public void getOffsetFromLocalUsesSummerOffsetThroughoutLondonOverlap() {
        assertEquals(HOUR, LONDON.getOffsetFromLocal(localMillis(2011, 10, 30, 1, 0)));
        assertEquals(HOUR, LONDON.getOffsetFromLocal(localMillis(2011, 10, 30, 1, 15)));
        assertEquals(HOUR, LONDON.getOffsetFromLocal(localMillis(2011, 10, 30, 1, 59)));
    }

    @Test
    public void getOffsetFromLocalUsesOffsetsOnEitherSideOfLondonOverlap() {
        assertEquals(HOUR, LONDON.getOffsetFromLocal(localMillis(2011, 10, 30, 0, 59)));
        assertEquals(0, LONDON.getOffsetFromLocal(localMillis(2011, 10, 30, 2, 0)));
    }

    @Test
    public void convertLocalToUtcChoosesEarlierOccurrenceForLondonOverlap() {
        long local = localMillis(2011, 10, 30, 1, 15);

        assertEquals(utcMillis(2011, 10, 30, 0, 15), LONDON.convertLocalToUTC(local, false));
        assertEquals(utcMillis(2011, 10, 30, 0, 15), LONDON.convertLocalToUTC(local, true));
    }

    @Test
    public void convertLocalToUtcWithOriginalInstantKeepsChosenOverlapOccurrence() {
        long local = localMillis(2011, 10, 30, 1, 15);
        long earlier = utcMillis(2011, 10, 30, 0, 15);
        long later = utcMillis(2011, 10, 30, 1, 15);

        assertEquals(earlier, LONDON.convertLocalToUTC(local, false, earlier));
        assertEquals(later, LONDON.convertLocalToUTC(local, false, later));
    }

    @Test
    public void adjustOffsetSelectsEarlierAndLaterLondonOverlapOccurrences() {
        long earlier = utcMillis(2011, 10, 30, 0, 15);
        long later = utcMillis(2011, 10, 30, 1, 15);

        assertEquals(earlier, LONDON.adjustOffset(earlier, false));
        assertEquals(later, LONDON.adjustOffset(earlier, true));
        assertEquals(earlier, LONDON.adjustOffset(later, false));
        assertEquals(later, LONDON.adjustOffset(later, true));
    }

    @Test(expected = IllegalInstantException.class)
    public void strictConversionRejectsLondonSpringGap() {
        long nonexistentLocalTime = localMillis(2011, 3, 27, 1, 15);

        LONDON.convertLocalToUTC(nonexistentLocalTime, true);
    }

    @Test
    public void nonStrictConversionMovesLondonSpringGapForward() {
        long nonexistentLocalTime = localMillis(2011, 3, 27, 1, 15);

        assertEquals(utcMillis(2011, 3, 27, 1, 15),
                LONDON.convertLocalToUTC(nonexistentLocalTime, false));
    }
}
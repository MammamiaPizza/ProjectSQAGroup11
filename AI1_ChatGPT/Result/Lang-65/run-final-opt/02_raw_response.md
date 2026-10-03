package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilsLang65Test {

    private static final TimeZone DENVER = TimeZone.getTimeZone("America/Denver");
    private static final long MDT_OVERLAP_INSTANT_WITH_MILLIS = 1099206123456L;
    private static final long MDT_OVERLAP_TRUNCATED_INSTANT = 1099206123000L;

    @Test
    public void truncateCalendarToSecondPreservesDaylightOffsetDuringFallBackOverlap() {
        Calendar input = new GregorianCalendar(DENVER);
        input.setTimeInMillis(MDT_OVERLAP_INSTANT_WITH_MILLIS);

        Calendar truncated = DateUtils.truncate(input, Calendar.SECOND);

        assertNotSame(input, truncated);
        assertEquals(MDT_OVERLAP_TRUNCATED_INSTANT, truncated.getTimeInMillis());
        assertEquals(-6 * 60 * 60 * 1000,
                truncated.get(Calendar.ZONE_OFFSET) + truncated.get(Calendar.DST_OFFSET));
        assertEquals(1, truncated.get(Calendar.HOUR_OF_DAY));
        assertEquals(2, truncated.get(Calendar.MINUTE));
        assertEquals(3, truncated.get(Calendar.SECOND));
        assertEquals(0, truncated.get(Calendar.MILLISECOND));
        assertEquals(MDT_OVERLAP_INSTANT_WITH_MILLIS, input.getTimeInMillis());
    }

    @Test
    public void truncateDateToSecondPreservesInstantDuringFallBackOverlap() {
        TimeZone originalDefault = TimeZone.getDefault();
        try {
            TimeZone.setDefault(DENVER);

            Date input = new Date(MDT_OVERLAP_INSTANT_WITH_MILLIS);
            Date truncated = DateUtils.truncate(input, Calendar.SECOND);

            assertEquals(MDT_OVERLAP_TRUNCATED_INSTANT, truncated.getTime());
        } finally {
            TimeZone.setDefault(originalDefault);
        }
    }

    @Test
    public void truncateCalendarToSecondRemovesOnlyMillisecondsOutsideDstTransition() {
        Calendar input = new GregorianCalendar(DateUtils.UTC_TIME_ZONE);
        input.setTimeInMillis(1589718896789L);

        Calendar truncated = DateUtils.truncate(input, Calendar.SECOND);

        assertEquals(1589718896000L, truncated.getTimeInMillis());
        assertEquals(2020, truncated.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, truncated.get(Calendar.MONTH));
        assertEquals(17, truncated.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, truncated.get(Calendar.HOUR_OF_DAY));
        assertEquals(34, truncated.get(Calendar.MINUTE));
        assertEquals(56, truncated.get(Calendar.SECOND));
        assertEquals(0, truncated.get(Calendar.MILLISECOND));
    }

    @Test
    public void truncateCalendarAtExactSecondKeepsSameInstant() {
        Calendar input = new GregorianCalendar(DateUtils.UTC_TIME_ZONE);
        input.setTimeInMillis(1589718896000L);

        Calendar truncated = DateUtils.truncate(input, Calendar.SECOND);

        assertEquals(input.getTimeInMillis(), truncated.getTimeInMillis());
        assertEquals(0, truncated.get(Calendar.MILLISECOND));
    }

    @Test
    public void truncateNullDateThrowsIllegalArgumentException() {
        try {
            DateUtils.truncate((Date) null, Calendar.SECOND);
            fail("Expected IllegalArgumentException for null date");
        } catch (IllegalArgumentException expected) {
            assertEquals("The date must not be null", expected.getMessage());
        }
    }
}
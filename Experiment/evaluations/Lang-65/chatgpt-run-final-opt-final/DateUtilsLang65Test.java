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

@org.junit.Test
public void addConvenienceMethodsApplyRequestedCalendarAmounts() {
    java.util.Date base = new java.util.Date(1589718896000L);

    org.junit.Assert.assertEquals(base.getTime() + 25L,
            org.apache.commons.lang.time.DateUtils.add(base, java.util.Calendar.MILLISECOND, 25).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 25L,
            org.apache.commons.lang.time.DateUtils.addMilliseconds(base, 25).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 2000L,
            org.apache.commons.lang.time.DateUtils.addSeconds(base, 2).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 120000L,
            org.apache.commons.lang.time.DateUtils.addMinutes(base, 2).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 3600000L,
            org.apache.commons.lang.time.DateUtils.addHours(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 86400000L,
            org.apache.commons.lang.time.DateUtils.addDays(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 7L * 86400000L,
            org.apache.commons.lang.time.DateUtils.addWeeks(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 31L * 86400000L,
            org.apache.commons.lang.time.DateUtils.addMonths(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 365L * 86400000L,
            org.apache.commons.lang.time.DateUtils.addYears(base, 1).getTime());
}

@org.junit.Test
public void calendarComparisonMethodsDistinguishInstantFromLocalFields() {
    java.util.Calendar first = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT"));
    java.util.Calendar second = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+02:00"));
    first.clear();
    second.clear();
    first.set(2020, java.util.Calendar.MAY, 17, 12, 34, 56);
    second.set(2020, java.util.Calendar.MAY, 17, 12, 34, 56);

    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameDay(first, second));
    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameLocalTime(first, second));
    org.junit.Assert.assertFalse(org.apache.commons.lang.time.DateUtils.isSameInstant(first, second));

    second.setTimeInMillis(first.getTimeInMillis());
    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameInstant(first, second));
    org.junit.Assert.assertFalse(org.apache.commons.lang.time.DateUtils.isSameLocalTime(first, second));
}

@org.junit.Test
public void parseDateUsesLaterPatternWhenEarlierPatternDoesNotMatch() throws java.text.ParseException {
    java.util.Date parsed = org.apache.commons.lang.time.DateUtils.parseDate(
            "1970-01-02T00:00:00+0000",
            new String[] { "yyyy/MM/dd", "yyyy-MM-dd'T'HH:mm:ssZ" });

    org.junit.Assert.assertEquals(86400000L, parsed.getTime());
}

@org.junit.Test
public void truncateCalendarToMinutePreservesDaylightOffsetDuringFallBackOverlap() {
    java.util.Calendar input = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("America/Denver"));
    input.setTimeInMillis(1099206123987L);

    java.util.Calendar truncated = org.apache.commons.lang.time.DateUtils.truncate(
            input, java.util.Calendar.MINUTE);

    org.junit.Assert.assertNotSame(input, truncated);
    org.junit.Assert.assertEquals(1099206120000L, truncated.getTimeInMillis());
    org.junit.Assert.assertEquals(-6 * 60 * 60 * 1000,
            truncated.get(java.util.Calendar.ZONE_OFFSET) + truncated.get(java.util.Calendar.DST_OFFSET));
    org.junit.Assert.assertEquals(0, truncated.get(java.util.Calendar.SECOND));
    org.junit.Assert.assertEquals(0, truncated.get(java.util.Calendar.MILLISECOND));
}
}

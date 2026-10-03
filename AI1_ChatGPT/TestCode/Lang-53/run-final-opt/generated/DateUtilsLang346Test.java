package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilsLang346Test {

    private static final TimeZone UTC = TimeZone.getTimeZone("GMT");

    private Calendar calendar(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        Calendar calendar = new GregorianCalendar(UTC);
        calendar.clear();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar;
    }

    @Test
    public void testRoundCalendarMinuteRoundsUpToNextMinute() {
        Calendar input = calendar(2007, Calendar.JULY, 2, 8, 8, 30, 0);

        Calendar rounded = DateUtils.round(input, Calendar.MINUTE);

        assertEquals(calendar(2007, Calendar.JULY, 2, 8, 9, 0, 0).getTimeInMillis(),
                rounded.getTimeInMillis());
        assertEquals(8, input.get(Calendar.MINUTE));
        assertEquals(30, input.get(Calendar.SECOND));
    }

    @Test
    public void testRoundMinuteRoundsDownBelowHalfMinuteAndUpAtHalfMinute() {
        Calendar belowHalf = calendar(2007, Calendar.JULY, 2, 8, 8, 29, 999);
        Calendar atHalf = calendar(2007, Calendar.JULY, 2, 8, 8, 30, 0);

        Calendar roundedBelowHalf = DateUtils.round(belowHalf, Calendar.MINUTE);
        Calendar roundedAtHalf = DateUtils.round(atHalf, Calendar.MINUTE);

        assertEquals(calendar(2007, Calendar.JULY, 2, 8, 8, 0, 0).getTimeInMillis(),
                roundedBelowHalf.getTimeInMillis());
        assertEquals(calendar(2007, Calendar.JULY, 2, 8, 9, 0, 0).getTimeInMillis(),
                roundedAtHalf.getTimeInMillis());
    }

    @Test
    public void testRoundMinuteCarriesIntoNextHour() {
        Calendar input = calendar(2007, Calendar.JULY, 2, 8, 59, 30, 250);

        Calendar rounded = DateUtils.round(input, Calendar.MINUTE);

        assertEquals(calendar(2007, Calendar.JULY, 2, 9, 0, 0, 0).getTimeInMillis(),
                rounded.getTimeInMillis());
    }

    @Test
    public void testDateAndObjectRoundOverloadsMatchCalendarMinuteRounding() {
        Calendar input = calendar(2007, Calendar.JULY, 2, 8, 8, 45, 125);
        Date expected = calendar(2007, Calendar.JULY, 2, 8, 9, 0, 0).getTime();

        Date roundedDate = DateUtils.round(input.getTime(), Calendar.MINUTE);
        Date roundedObject = DateUtils.round((Object) input, Calendar.MINUTE);

        assertEquals(expected, roundedDate);
        assertEquals(expected, roundedObject);
    }
}

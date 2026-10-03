package org.apache.commons.lang3.time;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

public class DateUtilsLang677Test {

    private static GregorianCalendar calendar(TimeZone timeZone, int hourOfDay) {
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        calendar.clear();
        calendar.set(2010, Calendar.JUNE, 15, hourOfDay, 20, 30);
        calendar.set(Calendar.MILLISECOND, 456);
        return calendar;
    }

    private static void setLocalFields(Calendar calendar, int hourOfDay) {
        calendar.clear();
        calendar.set(2010, Calendar.JUNE, 15, hourOfDay, 20, 30);
        calendar.set(Calendar.MILLISECOND, 456);
    }

    @Test
    public void isSameLocalTimeReturnsTrueForEqualLocalFieldsInDifferentTimeZones() {
        Calendar utc = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar plusFive = calendar(TimeZone.getTimeZone("GMT+05:00"), 10);

        assertFalse(utc.getTimeInMillis() == plusFive.getTimeInMillis());
        assertTrue(DateUtils.isSameLocalTime(utc, plusFive));
    }

    @Test
    public void isSameLocalTimeDistinguishesMorningAndAfternoonWithSameHourField() {
        Calendar morning = calendar(TimeZone.getTimeZone("GMT"), 1);
        Calendar afternoon = calendar(TimeZone.getTimeZone("GMT"), 13);

        assertFalse(DateUtils.isSameLocalTime(morning, afternoon));
    }

    @Test
    public void isSameLocalTimeReturnsFalseForSameInstantWithDifferentLocalHours() {
        Calendar utc = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar plusFive = new GregorianCalendar(TimeZone.getTimeZone("GMT+05:00"));
        plusFive.setTimeInMillis(utc.getTimeInMillis());

        assertFalse(DateUtils.isSameLocalTime(utc, plusFive));
    }

    @Test
    public void isSameLocalTimeReturnsFalseWhenMillisecondsDiffer() {
        Calendar first = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar second = calendar(TimeZone.getTimeZone("GMT"), 10);
        second.set(Calendar.MILLISECOND, 457);

        assertFalse(DateUtils.isSameLocalTime(first, second));
    }

    @Test
    public void isSameLocalTimeReturnsFalseWhenDateDiffers() {
        Calendar first = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar second = calendar(TimeZone.getTimeZone("GMT"), 10);
        second.add(Calendar.DAY_OF_MONTH, 1);

        assertFalse(DateUtils.isSameLocalTime(first, second));
    }

    @Test
    public void isSameLocalTimeRequiresSameCalendarImplementation() {
        Calendar standard = calendar(TimeZone.getTimeZone("GMT"), 10);
        Calendar alternate = new AlternateGregorianCalendar(TimeZone.getTimeZone("GMT"));
        setLocalFields(alternate, 10);

        assertFalse(DateUtils.isSameLocalTime(standard, alternate));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameLocalTimeRejectsNullFirstCalendar() {
        DateUtils.isSameLocalTime(null, calendar(TimeZone.getTimeZone("GMT"), 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameLocalTimeRejectsNullSecondCalendar() {
        DateUtils.isSameLocalTime(calendar(TimeZone.getTimeZone("GMT"), 10), null);
    }

    private static final class AlternateGregorianCalendar extends GregorianCalendar {
        private static final long serialVersionUID = 1L;

        private AlternateGregorianCalendar(TimeZone timeZone) {
            super(timeZone);
        }
    }
}

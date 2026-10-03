package org.joda.time;

import static org.junit.Assert.assertEquals;

import org.joda.time.chrono.ISOChronology;
import org.junit.Test;

public class BasePeriodFixedZoneTest {

    private static final Chronology FIXED_CHRONOLOGY =
            ISOChronology.getInstance(DateTimeZone.forOffsetHours(2));

    @Test
    public void durationToPeriodWithFixedZoneDecomposesTimeFieldsWithoutResidualMillis() {
        Duration duration = new Duration(64L * 1000L);

        Period period = duration.toPeriod(PeriodType.time(), FIXED_CHRONOLOGY);

        assertEquals(0, period.getHours());
        assertEquals(1, period.getMinutes());
        assertEquals(4, period.getSeconds());
        assertEquals(0, period.getMillis());
    }

    @Test
    public void longDurationConstructorWithFixedZoneDecomposesTimeFieldsWithoutResidualMillis() {
        Period period = new Period(64L * 1000L, PeriodType.time(), FIXED_CHRONOLOGY);

        assertEquals(0, period.getHours());
        assertEquals(1, period.getMinutes());
        assertEquals(4, period.getSeconds());
        assertEquals(0, period.getMillis());
    }

    @Test
    public void longDurationConstructorWithFixedZoneDecomposesWholeWeeksAndDays() {
        long duration = 8L * DateTimeConstants.MILLIS_PER_DAY;

        Period period = new Period(duration, PeriodType.standard(), FIXED_CHRONOLOGY);

        assertEquals(0, period.getYears());
        assertEquals(0, period.getMonths());
        assertEquals(1, period.getWeeks());
        assertEquals(1, period.getDays());
        assertEquals(0, period.getHours());
        assertEquals(0, period.getMinutes());
        assertEquals(0, period.getSeconds());
        assertEquals(0, period.getMillis());
    }

    @Test
    public void durationAndInstantRangeConstructionAgreeForFixedZone() {
        long duration = 8L * DateTimeConstants.MILLIS_PER_DAY;
        long start = 123456789L;

        Period fromDuration = new Period(duration, PeriodType.standard(), FIXED_CHRONOLOGY);
        Period fromRange = new Period(start, start + duration, PeriodType.standard(), FIXED_CHRONOLOGY);

        assertEquals(fromRange, fromDuration);
        assertEquals(0, fromDuration.getMillis());
    }

    @Test
    public void zeroDurationWithFixedZoneProducesOnlyZeroFields() {
        Period period = new Period(0L, PeriodType.standard(), FIXED_CHRONOLOGY);

        for (int i = 0; i < period.size(); i++) {
            assertEquals(0, period.getValue(i));
        }
    }
}

package org.joda.time.chrono;

import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

public class GJChronologyYearZeroCutoverTest extends TestCase {

    private Chronology chronologyWithCutoverAtYearOne() {
        Chronology gregorian = GregorianChronology.getInstanceUTC();
        long cutoverMillis = gregorian.getDateTimeMillis(1, 1, 1, 0);
        return GJChronology.getInstance(DateTimeZone.UTC, new Instant(cutoverMillis), 4);
    }

    private LocalDate date(Chronology chronology, long instant) {
        return new LocalDate(instant, chronology);
    }

    private void assertDate(LocalDate actual, int year, int month, int day) {
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonthOfYear());
        assertEquals(day, actual.getDayOfMonth());
    }

    public void testPreZeroDatesUseJulianSideOfCutover() {
        Chronology chronology = chronologyWithCutoverAtYearOne();

        long actual = chronology.getDateTimeMillis(-1, 6, 30, 0);
        long expected = JulianChronology.getInstanceUTC()
                .getDateTimeMillis(-1, 6, 30, 0);

        assertEquals(expected, actual);
        assertDate(date(chronology, actual), -1, 6, 30);
    }

    public void testDateAtCutoverUsesGregorianSide() {
        Chronology chronology = chronologyWithCutoverAtYearOne();

        long actual = chronology.getDateTimeMillis(1, 1, 1, 0);
        long expected = GregorianChronology.getInstanceUTC()
                .getDateTimeMillis(1, 1, 1, 0);

        assertEquals(expected, actual);
        assertDate(date(chronology, actual), 1, 1, 1);
    }

    public void testAddYearsAcrossCutoverFromPositiveToZeroSkipsUnsupportedYearZero() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(1, 6, 30, 0);

        LocalDate result = date(chronology, chronology.year().add(start, -1));

        assertDate(result, -1, 6, 30);
    }

    public void testAddYearsAcrossCutoverFromPositiveToNegativePreservesDate() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(1, 6, 30, 0);

        LocalDate result = date(chronology, chronology.year().add(start, -2));

        assertDate(result, -2, 6, 30);
    }

    public void testAddWeekyearsAcrossCutoverFromPositiveToZeroSkipsUnsupportedYearZero() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(1, 6, 30, 0);

        try {
            chronology.weekyear().add(start, -1);
            fail();
        } catch (IllegalFieldValueException ex) {
        }
    }

    public void testAddWeekyearsAcrossCutoverFromPositiveToNegativePreservesDate() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(1, 6, 30, 0);

        LocalDate result = date(chronology, chronology.weekyear().add(start, -2));

        assertDate(result, -1, 6, 28);
        assertEquals(-1, chronology.weekyear().get(result.toDateTimeAtStartOfDay().getMillis()));
    }

    public void testSetYearAcrossCutoverDoesNotAttemptYearZero() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(1, 6, 30, 0);

        LocalDate result = date(chronology, chronology.year().set(start, -1));

        assertDate(result, -1, 6, 30);
    }

    public void testSetWeekyearAcrossCutoverPreservesDate() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(1, 6, 30, 0);

        LocalDate result = date(chronology, chronology.weekyear().set(start, -2));

        assertDate(result, -2, 6, 28);
        assertEquals(-2, chronology.weekyear().get(result.toDateTimeAtStartOfDay().getMillis()));
    }

    public void testYearAdditionRemainingOnGregorianSidePreservesDate() {
        Chronology chronology = chronologyWithCutoverAtYearOne();
        long start = chronology.getDateTimeMillis(2, 6, 30, 0);

        LocalDate result = date(chronology, chronology.year().add(start, 3));

        assertDate(result, 5, 6, 30);
    }
}

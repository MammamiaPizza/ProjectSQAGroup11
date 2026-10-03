@Test
public void instantAndDurationConstructorWithFixedZoneDecomposesWithoutResidualMillis() {
    org.joda.time.DateTimeZone zone = org.joda.time.DateTimeZone.forOffsetHours(2);
    org.joda.time.Chronology chronology =
            org.joda.time.chrono.ISOChronology.getInstance(zone);
    ExposedBasePeriod period = new ExposedBasePeriod(
            new org.joda.time.DateTime(0L, chronology),
            new org.joda.time.Duration(64000L),
            org.joda.time.PeriodType.time());

    assertEquals(0, period.getValue(0));
    assertEquals(1, period.getValue(1));
    assertEquals(4, period.getValue(2));
    assertEquals(0, period.getValue(3));
}

@Test
public void durationAndInstantConstructorWithFixedZoneDecomposesWithoutResidualMillis() {
    org.joda.time.DateTimeZone zone = org.joda.time.DateTimeZone.forOffsetHours(2);
    org.joda.time.Chronology chronology =
            org.joda.time.chrono.ISOChronology.getInstance(zone);
    ExposedBasePeriod period = new ExposedBasePeriod(
            new org.joda.time.Duration(64000L),
            new org.joda.time.DateTime(64000L, chronology),
            org.joda.time.PeriodType.time());

    assertEquals(0, period.getValue(0));
    assertEquals(1, period.getValue(1));
    assertEquals(4, period.getValue(2));
    assertEquals(0, period.getValue(3));
}

@Test
public void defaultDurationConstructorDecomposesDurationIntoStandardFields() {
    ExposedBasePeriod period = new ExposedBasePeriod(64000L);

    assertEquals(8, period.size());
    assertEquals(0, period.getValue(4));
    assertEquals(1, period.getValue(5));
    assertEquals(4, period.getValue(6));
    assertEquals(0, period.getValue(7));
}

@Test
public void objectConstructorCopiesAllValuesFromReadablePeriod() {
    ExposedBasePeriod source = new ExposedBasePeriod(
            1, 2, 3, 4, 5, 6, 7, 8, org.joda.time.PeriodType.standard());
    ExposedBasePeriod copy = new ExposedBasePeriod(
            (Object) source, org.joda.time.PeriodType.standard(), null);

    assertEquals(8, copy.size());
    for (int i = 0; i < copy.size(); i++) {
        assertEquals(i + 1, copy.getValue(i));
    }
}

private static final class ExposedBasePeriod extends org.joda.time.base.BasePeriod {
    private ExposedBasePeriod(long duration) {
        super(duration);
    }

    private ExposedBasePeriod(org.joda.time.ReadableInstant startInstant,
                              org.joda.time.ReadableDuration duration,
                              org.joda.time.PeriodType type) {
        super(startInstant, duration, type);
    }

    private ExposedBasePeriod(org.joda.time.ReadableDuration duration,
                              org.joda.time.ReadableInstant endInstant,
                              org.joda.time.PeriodType type) {
        super(duration, endInstant, type);
    }

    private ExposedBasePeriod(Object period,
                              org.joda.time.PeriodType type,
                              org.joda.time.Chronology chronology) {
        super(period, type, chronology);
    }

    private ExposedBasePeriod(int years, int months, int weeks, int days,
                              int hours, int minutes, int seconds, int millis,
                              org.joda.time.PeriodType type) {
        super(years, months, weeks, days, hours, minutes, seconds, millis, type);
    }
}
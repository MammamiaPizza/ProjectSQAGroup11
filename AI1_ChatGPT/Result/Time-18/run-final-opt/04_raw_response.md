@org.junit.Test
public void testCustomCutoverRetainsJulianLeapDayBeforeCutover() {
    org.joda.time.chrono.GJChronology chronology = newChronologyWith1752Cutover();

    long instant = chronology.getDateTimeMillis(1700, 2, 29,
            13 * 60 * 60 * 1000);

    org.junit.Assert.assertEquals(1700, chronology.year().get(instant));
    org.junit.Assert.assertEquals(2, chronology.monthOfYear().get(instant));
    org.junit.Assert.assertEquals(29, chronology.dayOfMonth().get(instant));
    org.junit.Assert.assertEquals(13 * 60 * 60 * 1000,
            chronology.millisOfDay().get(instant));
}

@org.junit.Test
public void testCustomCutoverPreservesFieldsOnBothSidesOfBoundary() {
    org.joda.time.chrono.GJChronology chronology = newChronologyWith1752Cutover();

    long lastJulianInstant = chronology.getDateTimeMillis(
            1752, 9, 2, 23, 59, 59, 999);
    long firstGregorianInstant = chronology.getDateTimeMillis(
            1752, 9, 14, 12, 34, 56, 789);

    org.junit.Assert.assertEquals(2, chronology.dayOfMonth().get(lastJulianInstant));
    org.junit.Assert.assertEquals(23, chronology.hourOfDay().get(lastJulianInstant));
    org.junit.Assert.assertEquals(14, chronology.dayOfMonth().get(firstGregorianInstant));
    org.junit.Assert.assertEquals(12, chronology.hourOfDay().get(firstGregorianInstant));
    org.junit.Assert.assertEquals(34, chronology.minuteOfHour().get(firstGregorianInstant));
}

@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
public void testCustomCutoverRejectsDateInItsGap() {
    newChronologyWith1752Cutover().getDateTimeMillis(1752, 9, 3, 0);
}

private org.joda.time.chrono.GJChronology newChronologyWith1752Cutover() {
    long cutoverMillis = org.joda.time.chrono.GregorianChronology.getInstanceUTC()
            .getDateTimeMillis(1752, 9, 14, 0);
    return org.joda.time.chrono.GJChronology.getInstance(
            org.joda.time.DateTimeZone.UTC,
            new org.joda.time.Instant(cutoverMillis), 4);
}
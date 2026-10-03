@org.junit.Test
public void deprecatedConstructorsExposeConfiguredValuesAndMutableZoneOffset() {
    org.joda.time.Chronology chronology =
            org.joda.time.chrono.ISOChronology.getInstanceUTC();

    org.joda.time.format.DateTimeParserBucket defaultPivotBucket =
            new org.joda.time.format.DateTimeParserBucket(
                    0L, chronology, java.util.Locale.US);

    org.junit.Assert.assertEquals(chronology, defaultPivotBucket.getChronology());
    org.junit.Assert.assertEquals(java.util.Locale.US, defaultPivotBucket.getLocale());
    org.junit.Assert.assertEquals(org.joda.time.DateTimeZone.UTC, defaultPivotBucket.getZone());
    org.junit.Assert.assertNull(defaultPivotBucket.getPivotYear());

    defaultPivotBucket.setZone(org.joda.time.DateTimeZone.forID("Europe/Paris"));
    defaultPivotBucket.setOffset(3600000);

    org.junit.Assert.assertEquals(
            org.joda.time.DateTimeZone.forID("Europe/Paris"), defaultPivotBucket.getZone());
    org.junit.Assert.assertEquals(3600000, defaultPivotBucket.getOffset());

    org.joda.time.format.DateTimeParserBucket pivotBucket =
            new org.joda.time.format.DateTimeParserBucket(
                    0L, chronology, java.util.Locale.US, Integer.valueOf(1975));

    org.junit.Assert.assertEquals(Integer.valueOf(1975), pivotBucket.getPivotYear());
}

@org.junit.Test
public void restoresSavedFieldStateAfterAdditionalFieldsAreAdded() {
    org.joda.time.Chronology chronology =
            org.joda.time.chrono.ISOChronology.getInstanceUTC();
    org.joda.time.format.DateTimeParserBucket bucket =
            new org.joda.time.format.DateTimeParserBucket(
                    0L, chronology, java.util.Locale.US, null);

    bucket.saveField(org.joda.time.DateTimeFieldType.year(), 2012);
    Object savedState = bucket.saveState();

    bucket.saveField(org.joda.time.DateTimeFieldType.monthOfYear(), 2);
    bucket.saveField(org.joda.time.DateTimeFieldType.dayOfMonth(), 29);
    bucket.computeMillis();

    org.junit.Assert.assertTrue(bucket.restoreState(savedState));
    org.junit.Assert.assertFalse(bucket.restoreState(new Object()));

    org.joda.time.format.DateTimeParserBucket expected =
            new org.joda.time.format.DateTimeParserBucket(
                    0L, chronology, java.util.Locale.US, null);
    expected.saveField(org.joda.time.DateTimeFieldType.year(), 2012);

    org.junit.Assert.assertEquals(expected.computeMillis(), bucket.computeMillis());
}

@org.junit.Test
public void reportsParsingTextWhenLocalTimeFallsInZoneGap() {
    org.joda.time.format.DateTimeParserBucket bucket =
            new org.joda.time.format.DateTimeParserBucket(
                    0L,
                    org.joda.time.chrono.ISOChronology.getInstanceUTC(),
                    java.util.Locale.US,
                    null);
    bucket.setZone(org.joda.time.DateTimeZone.forID("Europe/Paris"));
    bucket.saveField(org.joda.time.DateTimeFieldType.year(), 2011);
    bucket.saveField(org.joda.time.DateTimeFieldType.monthOfYear(), 3);
    bucket.saveField(org.joda.time.DateTimeFieldType.dayOfMonth(), 27);
    bucket.saveField(org.joda.time.DateTimeFieldType.hourOfDay(), 2);
    bucket.saveField(org.joda.time.DateTimeFieldType.minuteOfHour(), 30);

    try {
        bucket.computeMillis(false, "2011-03-27T02:30");
        org.junit.Assert.fail("Expected a local time in the zone gap to be rejected");
    } catch (IllegalArgumentException expected) {
        org.junit.Assert.assertTrue(expected.getMessage().startsWith(
                "Cannot parse \"2011-03-27T02:30\": Illegal instant due to time zone offset transition"));
    }
}
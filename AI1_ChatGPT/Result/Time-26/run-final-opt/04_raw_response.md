@org.junit.Test
public void testWithMillisOfSecondInNewYorkOverlapKeepsWinterOffset() {
    org.joda.time.DateTimeZone newYork = org.joda.time.DateTimeZone.forID("America/New_York");
    org.joda.time.DateTime original = new org.joda.time.DateTime(
            2007, 11, 4, 6, 30, 0, 123, org.joda.time.DateTimeZone.UTC).withZone(newYork);

    org.joda.time.DateTime changed = original.withMillisOfSecond(0);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2007, 11, 4, 6, 30, 0, 0,
                    org.joda.time.DateTimeZone.UTC).getMillis(),
            changed.getMillis());
    org.junit.Assert.assertEquals(-5 * 60 * 60 * 1000, newYork.getOffset(changed.getMillis()));
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testConvertLocalToUTCStrictRejectsParisSpringGap() {
    org.joda.time.DateTimeZone paris = org.joda.time.DateTimeZone.forID("Europe/Paris");
    long localGap = org.joda.time.chrono.ISOChronology.getInstanceUTC().getDateTimeMillis(
            2004, 3, 28, 2, 30, 0, 0);

    paris.convertLocalToUTC(localGap, true);
}

@org.junit.Test
public void testGetMillisKeepLocalPreservesParisLocalTimeInNewYork() {
    org.joda.time.DateTimeZone paris = org.joda.time.DateTimeZone.forID("Europe/Paris");
    org.joda.time.DateTimeZone newYork = org.joda.time.DateTimeZone.forID("America/New_York");
    long parisNoon = new org.joda.time.DateTime(2004, 1, 1, 12, 0, 0, 0, paris).getMillis();

    long converted = paris.getMillisKeepLocal(newYork, parisNoon);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2004, 1, 1, 12, 0, 0, 0, newYork).getMillis(),
            converted);
}

@org.junit.Test
public void testLenientDateTimeFieldFactoryHandlesNullAndExistingLenientField() {
    org.joda.time.Chronology chronology = org.joda.time.chrono.ISOChronology.getInstanceUTC();

    org.junit.Assert.assertNull(
            org.joda.time.field.LenientDateTimeField.getInstance(null, chronology));

    org.joda.time.DateTimeField lenient =
            org.joda.time.field.LenientDateTimeField.getInstance(chronology.hourOfDay(), chronology);

    org.junit.Assert.assertTrue(lenient.isLenient());
    org.junit.Assert.assertSame(
            lenient,
            org.joda.time.field.LenientDateTimeField.getInstance(lenient, chronology));
}
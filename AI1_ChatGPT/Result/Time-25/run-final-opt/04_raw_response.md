@Test(expected = IllegalArgumentException.class)
public void convertLocalToUTCStrictRejectsLondonSpringGap() {
    org.joda.time.DateTimeZone london = org.joda.time.DateTimeZone.forID("Europe/London");
    long localMillis = new org.joda.time.DateTime(
            2007, 3, 25, 1, 30, org.joda.time.DateTimeZone.UTC).getMillis();

    london.convertLocalToUTC(localMillis, true);
}

@Test
public void isLocalDateTimeGapRecognizesLondonSpringGap() {
    org.joda.time.DateTimeZone london = org.joda.time.DateTimeZone.forID("Europe/London");

    org.junit.Assert.assertTrue(london.isLocalDateTimeGap(
            new org.joda.time.LocalDateTime(2007, 3, 25, 1, 30)));
}

@Test
public void getOffsetFromLocalAfterMoscowAutumnOverlapUsesStandardOffset() {
    org.joda.time.DateTimeZone moscow = org.joda.time.DateTimeZone.forID("Europe/Moscow");
    long localMillis = new org.joda.time.DateTime(
            2007, 10, 28, 3, 0, org.joda.time.DateTimeZone.UTC).getMillis();

    org.junit.Assert.assertEquals(3 * 60 * 60 * 1000, moscow.getOffsetFromLocal(localMillis));
}

@Test
public void forOffsetMillisZeroReturnsUtcSingleton() {
    org.junit.Assert.assertSame(
            org.joda.time.DateTimeZone.UTC,
            org.joda.time.DateTimeZone.forOffsetMillis(0));
}
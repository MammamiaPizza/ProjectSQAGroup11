@org.junit.Test
public void dateTimePlusZeroMonthsKeepsInstant() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 5, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(instant, instant.plusMonths(0));
}

@org.junit.Test
public void dateTimePlusMonthsAcrossYearRetainsTimeAndUsesLeapDay() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 1, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2012, 2, 29, 10, 15, org.joda.time.DateTimeZone.UTC),
            instant.plusMonths(13));
}

@org.junit.Test
public void dateTimeMinusMonthsAcrossYearRetainsDayAndTime() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 1, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2010, 12, 31, 10, 15, org.joda.time.DateTimeZone.UTC),
            instant.minusMonths(1));
}

@org.junit.Test
public void dateTimePlusMonthsAdjustsToShorterMonthAndRetainsTime() {
    org.joda.time.DateTime instant = new org.joda.time.DateTime(
            2011, 1, 31, 10, 15, org.joda.time.DateTimeZone.UTC);

    org.junit.Assert.assertEquals(
            new org.joda.time.DateTime(2011, 2, 28, 10, 15, org.joda.time.DateTimeZone.UTC),
            instant.plusMonths(1));
}
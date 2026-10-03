@org.junit.Test
public void testInstantConstructorsEvaluateEpochUsingSpecifiedUtcZone() {
    org.junit.Assert.assertEquals(
            "1970-01-01",
            new org.joda.time.LocalDate(0L, org.joda.time.DateTimeZone.UTC).toString());
    org.junit.Assert.assertEquals(
            "1970-01-01T00:00:00.000",
            new org.joda.time.LocalDateTime(0L, org.joda.time.DateTimeZone.UTC).toString());
}

@org.junit.Test
public void testDateObjectConstructorsEvaluateEpochUsingSpecifiedUtcZone() {
    java.util.Date epoch = new java.util.Date(0L);

    org.junit.Assert.assertEquals(
            "1970-01-01",
            new org.joda.time.LocalDate(epoch, org.joda.time.DateTimeZone.UTC).toString());
    org.junit.Assert.assertEquals(
            "1970-01-01T00:00:00.000",
            new org.joda.time.LocalDateTime(epoch, org.joda.time.DateTimeZone.UTC).toString());
}
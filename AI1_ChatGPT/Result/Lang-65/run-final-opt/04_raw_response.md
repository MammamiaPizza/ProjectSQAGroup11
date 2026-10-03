@org.junit.Test
public void addConvenienceMethodsApplyRequestedCalendarAmounts() {
    java.util.Date base = new java.util.Date(1589718896000L);

    org.junit.Assert.assertEquals(base.getTime() + 25L,
            org.apache.commons.lang.time.DateUtils.add(base, java.util.Calendar.MILLISECOND, 25).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 25L,
            org.apache.commons.lang.time.DateUtils.addMilliseconds(base, 25).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 2000L,
            org.apache.commons.lang.time.DateUtils.addSeconds(base, 2).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 120000L,
            org.apache.commons.lang.time.DateUtils.addMinutes(base, 2).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 3600000L,
            org.apache.commons.lang.time.DateUtils.addHours(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 86400000L,
            org.apache.commons.lang.time.DateUtils.addDays(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 7L * 86400000L,
            org.apache.commons.lang.time.DateUtils.addWeeks(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 31L * 86400000L,
            org.apache.commons.lang.time.DateUtils.addMonths(base, 1).getTime());
    org.junit.Assert.assertEquals(base.getTime() + 365L * 86400000L,
            org.apache.commons.lang.time.DateUtils.addYears(base, 1).getTime());
}

@org.junit.Test
public void calendarComparisonMethodsDistinguishInstantFromLocalFields() {
    java.util.Calendar first = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT"));
    java.util.Calendar second = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+02:00"));
    first.clear();
    second.clear();
    first.set(2020, java.util.Calendar.MAY, 17, 12, 34, 56);
    second.set(2020, java.util.Calendar.MAY, 17, 12, 34, 56);

    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameDay(first, second));
    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameLocalTime(first, second));
    org.junit.Assert.assertFalse(org.apache.commons.lang.time.DateUtils.isSameInstant(first, second));

    second.setTimeInMillis(first.getTimeInMillis());
    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameInstant(first, second));
    org.junit.Assert.assertFalse(org.apache.commons.lang.time.DateUtils.isSameLocalTime(first, second));
}

@org.junit.Test
public void parseDateUsesLaterPatternWhenEarlierPatternDoesNotMatch() throws java.text.ParseException {
    java.util.Date parsed = org.apache.commons.lang.time.DateUtils.parseDate(
            "1970-01-02T00:00:00+0000",
            new String[] { "yyyy/MM/dd", "yyyy-MM-dd'T'HH:mm:ssZ" });

    org.junit.Assert.assertEquals(86400000L, parsed.getTime());
}

@org.junit.Test
public void truncateCalendarToMinutePreservesDaylightOffsetDuringFallBackOverlap() {
    java.util.Calendar input = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("America/Denver"));
    input.setTimeInMillis(1099206123987L);

    java.util.Calendar truncated = org.apache.commons.lang.time.DateUtils.truncate(
            input, java.util.Calendar.MINUTE);

    org.junit.Assert.assertNotSame(input, truncated);
    org.junit.Assert.assertEquals(1099206120000L, truncated.getTimeInMillis());
    org.junit.Assert.assertEquals(-6 * 60 * 60 * 1000,
            truncated.get(java.util.Calendar.ZONE_OFFSET) + truncated.get(java.util.Calendar.DST_OFFSET));
    org.junit.Assert.assertEquals(0, truncated.get(java.util.Calendar.SECOND));
    org.junit.Assert.assertEquals(0, truncated.get(java.util.Calendar.MILLISECOND));
}
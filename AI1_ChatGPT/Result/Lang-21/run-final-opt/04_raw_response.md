@org.junit.Test
public void addConvenienceMethodsMatchCalendarArithmetic() {
    java.util.Calendar calendar = new java.util.GregorianCalendar(
            org.apache.commons.lang3.time.DateUtils.UTC_TIME_ZONE);
    calendar.clear();
    calendar.set(2001, java.util.Calendar.JANUARY, 15, 10, 20, 30);
    calendar.set(java.util.Calendar.MILLISECOND, 400);
    java.util.Date source = calendar.getTime();

    assertCalendarAddition(source, java.util.Calendar.YEAR, 1,
            org.apache.commons.lang3.time.DateUtils.addYears(source, 1));
    assertCalendarAddition(source, java.util.Calendar.MONTH, 1,
            org.apache.commons.lang3.time.DateUtils.addMonths(source, 1));
    assertCalendarAddition(source, java.util.Calendar.WEEK_OF_YEAR, 1,
            org.apache.commons.lang3.time.DateUtils.addWeeks(source, 1));
    assertCalendarAddition(source, java.util.Calendar.DAY_OF_MONTH, 1,
            org.apache.commons.lang3.time.DateUtils.addDays(source, 1));
    assertCalendarAddition(source, java.util.Calendar.HOUR_OF_DAY, 1,
            org.apache.commons.lang3.time.DateUtils.addHours(source, 1));
    assertCalendarAddition(source, java.util.Calendar.MINUTE, 1,
            org.apache.commons.lang3.time.DateUtils.addMinutes(source, 1));
    assertCalendarAddition(source, java.util.Calendar.SECOND, 1,
            org.apache.commons.lang3.time.DateUtils.addSeconds(source, 1));
    assertCalendarAddition(source, java.util.Calendar.MILLISECOND, 1,
            org.apache.commons.lang3.time.DateUtils.addMilliseconds(source, 1));
}

@org.junit.Test
public void addDaysRejectsNullDate() {
    try {
        org.apache.commons.lang3.time.DateUtils.addDays(null, 1);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@org.junit.Test
public void ceilingObjectAcceptsDateAndCalendar() {
    java.util.Calendar calendar = new java.util.GregorianCalendar(
            org.apache.commons.lang3.time.DateUtils.UTC_TIME_ZONE);
    calendar.clear();
    calendar.set(2001, java.util.Calendar.JANUARY, 15, 10, 20, 30);

    java.util.Calendar expectedCalendar = new java.util.GregorianCalendar(
            org.apache.commons.lang3.time.DateUtils.UTC_TIME_ZONE);
    expectedCalendar.clear();
    expectedCalendar.set(2001, java.util.Calendar.JANUARY, 15, 11, 0, 0);

    java.util.Date expected = expectedCalendar.getTime();
    org.junit.Assert.assertEquals(expected,
            org.apache.commons.lang3.time.DateUtils.ceiling((Object) calendar.getTime(),
                    java.util.Calendar.HOUR_OF_DAY));
    org.junit.Assert.assertEquals(expected,
            org.apache.commons.lang3.time.DateUtils.ceiling((Object) calendar,
                    java.util.Calendar.HOUR_OF_DAY));
}

@org.junit.Test
public void parseDateStrictlyRejectsInvalidCalendarDate() throws java.lang.Exception {
    java.util.Date parsed = org.apache.commons.lang3.time.DateUtils.parseDate(
            "2011-02-29", "yyyy-MM-dd");
    java.util.Calendar parsedCalendar = java.util.Calendar.getInstance();
    parsedCalendar.setTime(parsed);
    org.junit.Assert.assertEquals(2011, parsedCalendar.get(java.util.Calendar.YEAR));
    org.junit.Assert.assertEquals(java.util.Calendar.MARCH,
            parsedCalendar.get(java.util.Calendar.MONTH));
    org.junit.Assert.assertEquals(1,
            parsedCalendar.get(java.util.Calendar.DAY_OF_MONTH));

    try {
        org.apache.commons.lang3.time.DateUtils.parseDateStrictly(
                "2011-02-29", "yyyy-MM-dd");
        org.junit.Assert.fail("Expected ParseException");
    } catch (java.text.ParseException expected) {
        // expected
    }
}

private void assertCalendarAddition(java.util.Date source, int field, int amount,
        java.util.Date actual) {
    java.util.Calendar expected = java.util.Calendar.getInstance();
    expected.setTime(source);
    expected.add(field, amount);
    org.junit.Assert.assertEquals(expected.getTime(), actual);
}
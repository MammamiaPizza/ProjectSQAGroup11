@Test
public void testAddMillisecondsHandlesNegativeAmountsWithoutMutatingInput() {
    java.util.Date input = new java.util.Date(123456789L);

    java.util.Date result = org.apache.commons.lang.time.DateUtils.addMilliseconds(input, -7);

    org.junit.Assert.assertEquals(123456782L, result.getTime());
    org.junit.Assert.assertEquals(123456789L, input.getTime());
}

@Test
public void testAddRejectsNullDate() {
    try {
        org.apache.commons.lang.time.DateUtils.add(null, java.util.Calendar.DAY_OF_MONTH, 1);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        org.junit.Assert.assertEquals("The date must not be null", expected.getMessage());
    }
}

@Test
public void testIsSameDayCalendarUsesCalendarLocalDateFields() {
    java.util.Calendar utc = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("GMT"));
    java.util.Calendar pacific = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("GMT-08:00"));
    utc.clear();
    pacific.clear();
    utc.set(2007, java.util.Calendar.JULY, 2, 8, 0, 0);
    pacific.set(2007, java.util.Calendar.JULY, 2, 8, 0, 0);

    org.junit.Assert.assertTrue(org.apache.commons.lang.time.DateUtils.isSameDay(utc, pacific));
}
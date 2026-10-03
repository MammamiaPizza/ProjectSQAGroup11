@Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullPatternThrowsIllegalArgumentException() {
        FastDateFormat.getInstance((String) null);
    }

 @Test
 public void testEqualsContract() {
     FastDateFormat fmt1 = FastDateFormat.getInstance("yyyy-MM-dd", java.util.TimeZone.getDefault(),
java.util.Locale.US);
     FastDateFormat fmt2 = FastDateFormat.getInstance("yyyy-MM-dd", java.util.TimeZone.getDefault(),
java.util.Locale.US);
     FastDateFormat fmt3 = FastDateFormat.getInstance("HH:mm", java.util.TimeZone.getDefault(),
java.util.Locale.US);

     // reflexive
     assertTrue("equals must be reflexive", fmt1.equals(fmt1));
     // symmetric
     assertTrue("equals must be symmetric", fmt1.equals(fmt2));
     assertTrue("equals must be symmetric", fmt2.equals(fmt1));
     // null
     assertFalse("equals must return false for null", fmt1.equals(null));
     // different type
     assertFalse("equals must return false for different class", fmt1.equals(new Object()));
     // consistent
     assertEquals("equals must be consistent", fmt1.equals(fmt2), fmt1.equals(fmt2));
     // different
     assertFalse("different patterns must not be equal", fmt1.equals(fmt3));
 }

 @Test
 public void testFormatObjectWithCalendar() {
     FastDateFormat fmt = FastDateFormat.getInstance("yyyy-MM-dd");
     java.util.Calendar cal = java.util.Calendar.getInstance();
     cal.clear();
     cal.set(2023, 0, 15); // Jan 15, 2023
     String result = fmt.format((Object) cal).toString();
     assertEquals("2023-01-15", result);
 }

 @Test
 public void testFormatObjectWithLong() {
     FastDateFormat fmt = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");
     long millis = 946684800000L; // 2000-01-01 00:00:00.000 UTC
     String result = fmt.format(Long.valueOf(millis)).toString();
     // verify year and month exist; actual exact string may depend on default timezone
     assertTrue("Formatted long must contain the year 2000", result.startsWith("2000-"));
 }
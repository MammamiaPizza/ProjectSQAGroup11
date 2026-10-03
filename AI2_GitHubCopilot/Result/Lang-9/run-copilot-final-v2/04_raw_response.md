@Test
 public void testEqualsSamePatternAndTimeZoneAndLocale() {
     FastDateParser parser1 = new FastDateParser("yyyy-MM-dd", TimeZone.getTimeZone("UTC"),
Locale.US);
     FastDateParser parser2 = new FastDateParser("yyyy-MM-dd", TimeZone.getTimeZone("UTC"),
Locale.US);
     assertTrue(parser1.equals(parser2));
     assertEquals(parser1.hashCode(), parser2.hashCode());
 }

 @Test
 public void testTwoDigitYearAdjustmentWithin20YearsFuture() {
     FastDateParser parser = new FastDateParser("yy-MM-dd", TimeZone.getTimeZone("UTC"), Locale.US);
     Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
     int currentYear = now.get(Calendar.YEAR);
     int targetYear = (currentYear + 19) % 100;
     String dateStr = String.format("%02d-01-01", targetYear);
     Date result = parser.parse(dateStr);
     assertNotNull(result);
     Calendar resultCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
     resultCal.setTime(result);
     assertEquals(currentYear + 19, resultCal.get(Calendar.YEAR));
 }

 @Test
 public void testTwoDigitYearAdjustmentMoreThan20YearsPast() {
     FastDateParser parser = new FastDateParser("yy-MM-dd", TimeZone.getTimeZone("UTC"), Locale.US);
     Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
     int currentYear = now.get(Calendar.YEAR);
     int targetYear = (currentYear + 21) % 100;
     String dateStr = String.format("%02d-01-01", targetYear);
     Date result = parser.parse(dateStr);
     assertNotNull(result);
     Calendar resultCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
     resultCal.setTime(result);
     assertEquals(currentYear - 79, resultCal.get(Calendar.YEAR));
 }

 @Test
 public void testEqualsDifferentPatternReturnsFalse() {
     FastDateParser parser1 = new FastDateParser("yyyy-MM-dd", TimeZone.getTimeZone("UTC"),
Locale.US);
     FastDateParser parser2 = new FastDateParser("yyyy/MM/dd", TimeZone.getTimeZone("UTC"),
Locale.US);
     assertFalse(parser1.equals(parser2));
 }
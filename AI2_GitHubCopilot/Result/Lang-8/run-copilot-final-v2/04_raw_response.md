@Test
    public void testGetters() {
        FastDatePrinter printer = new FastDatePrinter("yyyy-MM-dd", TimeZone.getTimeZone("UTC"),
Locale.US);
        assertEquals("yyyy-MM-dd", printer.getPattern());
        assertEquals(TimeZone.getTimeZone("UTC"), printer.getTimeZone());
        assertEquals(Locale.US, printer.getLocale());
        assertTrue(printer.getMaxLengthEstimate() >= 0);
    }

 @Test
 public void testFormatWithEraAndAmPmAndMillisAndWeekFields() {
     FastDatePrinter printer = new FastDatePrinter("G yyyy D F w W a SSS",
TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
     cal.set(2023, 0, 1, 10, 30, 0);
     cal.set(Calendar.MILLISECOND, 123);
     String result = prter.format(cal);
     assertNotNull(result);
     assertTrue("Expected AD, got: " + result, result.contains("AD"));
     assertTrue(result.contains("2023"));
     assertTrue(result.contains("AM"));
     assertTrue(result.contains("123"));
 }

 @Test
 public void testFormatWithTwoDigitYearAndMonth() {
     FastDatePrinter printer = new FastDatePrinter("yy-MM-dd", TimeZone.getTimeZone("UTC"),
Locale.US);
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
     cal.set(2023, 0, 15);
     String result = prter.format(cal);
     assertEquals("23-01-15", result);
 }

 @Test
 public void testFormatWithSingleMonthAndDay() {
     FastDatePrinter printer = new FastDatePrinter("M/d/yyyy", TimeZone.getTimeZone("UTC"),
Locale.US);
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
     cal.set(2023, 0, 1);
     String result = prter.format(cal);
     assertEquals("1/1/2023", result);
 }
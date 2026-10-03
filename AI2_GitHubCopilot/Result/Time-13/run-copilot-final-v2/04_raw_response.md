@Test(expected = IllegalArgumentException.class)
 public void testAppendLiteralNull() {
     new PeriodFormatterBuilder().appendLiteral(null);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testAppendFormatterNull() {
     new PeriodFormatterBuilder().append((PeriodFormatter) null);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testAppendPrinterParserBothNull() {
     new PeriodFormatterBuilder().append(
         (org.joda.time.format.PeriodPrinter) null,
         (org.joda.time.format.PeriodParser) null);
 }

 @Test
 public void testNegativeDaysAndHours() {
     PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
     builder.appendDays();
     builder.appendHours();
     PeriodFormatter f = builder.toFormatter();
     Period p = new Period(0, 0, 0, -1, -2, 0, 0, 0);
     assertEquals("P-1DT-2H", f.print(p));
 }
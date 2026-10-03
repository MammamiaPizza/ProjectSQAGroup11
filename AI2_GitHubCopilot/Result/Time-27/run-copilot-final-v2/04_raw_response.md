@Test
 public void testAppendDays() {
     PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
     builder.appendDays();
     PeriodFormatter formatter = builder.toFormatter();
     Period p = formatter.parsePeriod("P1D");
     assertEquals(1, p.getDays());
 }

 @Test
 public void testAppendHours() {
     PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
     builder.appendHours();
     PeriodFormatter formatter = builder.toFormatter();
     Period p = formatter.parsePeriod("PT1H");
     assertEquals(1, p.getHours());
 }

 @Test
 public void testAppendMinutes() {
     PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
     builder.appendMinutes();
     PeriodFormatter formatter = builder.toFormatter();
     Period p = formatter.parsePeriod("PT1M");
     assertEquals(1, p.getMinutes());
 }

 @Test
 public void testAppendMonths() {
     PeriodFormatterBuilder builder = new PeriodFormatterBuilder();
     builder.appendMonths();
     PeriodFormatter formatter = builder.toFormatter();
     Period p = formatter.parsePeriod("P1M");
     assertEquals(1, p.getMonths());
 }
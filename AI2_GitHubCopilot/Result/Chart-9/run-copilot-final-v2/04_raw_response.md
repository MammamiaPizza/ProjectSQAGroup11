@Test(expected = IllegalArgumentException.class)
    public void testAddNullItem() {
        TimeSeries series = new TimeSeries("Test");
        series.add((TimeSeriesDataItem) null);
    }

 @Test(expected = IllegalArgumentException.class)
 public void testAddItemWithWrongTimePeriodClass() {
     TimeSeries series = new TimeSeries("Test", Day.class);
     series.add(new TimeSeriesDataItem(new org.jfree.data.time.Month(1, 2000), 1.0));
 }

 @Test(expected = org.jfree.data.general.SeriesException.class)
 public void testAddDuplicateItemThrowsException() {
     TimeSeries series = new TimeSeries("Test", Day.class);
     Day day = new Day(1, 1, 2000);
     series.add(new TimeSeriesDataItem(day, 5.0));
     series.add(new TimeSeriesDataItem(day, 10.0));
 }

 @Test
 public void testAddPeriodValue() {
     TimeSeries series = new TimeSeries("Test", Day.class);
     series.add(new Day(1, 1, 2000), 5.0);
     assertEquals(1, series.getItemCount());
     assertEquals(5.0, series.getValue(0).doubleValue(), 0.0);
 }
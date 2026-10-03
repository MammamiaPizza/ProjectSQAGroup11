@Test
public void testAddNumberOverloadsMaintainChronologicalOrder() {
    TimeSeries series = new TimeSeries("S", Day.class);
    series.add(new Day(3, 1, 2007), new Double(3.0), false);
    series.add(new Day(1, 1, 2007), new Double(1.0));

    assertEquals(Day.class, series.getTimePeriodClass());
    assertEquals(2, series.getItemCount());
    assertEquals(new Day(1, 1, 2007), series.getTimePeriod(0));
    assertEquals(1.0, series.getValue(0).doubleValue(), 0.0);
    assertEquals(new Day(3, 1, 2007), series.getTimePeriod(1));
    assertEquals(3.0, series.getValue(1).doubleValue(), 0.0);
}

@Test
public void testAddDuplicatePeriodThrowsSeriesExceptionWithoutChangingSeries() {
    TimeSeries series = new TimeSeries("S");
    Day day = new Day(1, 1, 2007);
    series.add(day, new Double(1.0));

    boolean thrown = false;
    try {
        series.add(day, new Double(2.0), false);
    }
    catch (org.jfree.data.general.SeriesException e) {
        thrown = true;
    }

    assertTrue(thrown);
    assertEquals(1, series.getItemCount());
    assertEquals(1.0, series.getValue(0).doubleValue(), 0.0);
}

@Test
public void testAddNullDataItemIsRejected() {
    TimeSeries series = new TimeSeries("S");

    boolean thrown = false;
    try {
        series.add((org.jfree.data.time.TimeSeriesDataItem) null, false);
    }
    catch (IllegalArgumentException e) {
        thrown = true;
    }

    assertTrue(thrown);
    assertEquals(0, series.getItemCount());
}
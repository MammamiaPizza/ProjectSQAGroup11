@org.junit.Test
public void testAddNumberOverloadMaintainsChronologicalOrder() {
    org.jfree.data.time.TimeSeries series =
            new org.jfree.data.time.TimeSeries("S",
                    org.jfree.data.time.Day.class);
    series.add(new org.jfree.data.time.Day(2, 1, 2000),
            java.lang.Double.valueOf(2.0));
    series.add(new org.jfree.data.time.Day(1, 1, 2000),
            java.lang.Double.valueOf(1.0));

    org.junit.Assert.assertEquals(2, series.getItemCount());
    org.junit.Assert.assertEquals(new org.jfree.data.time.Day(1, 1, 2000),
            series.getTimePeriod(0));
    org.junit.Assert.assertEquals(java.lang.Double.valueOf(1.0),
            series.getValue(0));
    org.junit.Assert.assertEquals(new org.jfree.data.time.Day(2, 1, 2000),
            series.getTimePeriod(1));
}

@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
public void testAddRejectsNullDataItem() {
    org.jfree.data.time.TimeSeries series =
            new org.jfree.data.time.TimeSeries("S");
    series.add((org.jfree.data.time.TimeSeriesDataItem) null);
}

@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
public void testAddRejectsDataItemWithDifferentPeriodClass() {
    org.jfree.data.time.TimeSeries series =
            new org.jfree.data.time.TimeSeries("S");
    series.add(new org.jfree.data.time.TimeSeriesDataItem(
            new org.jfree.data.time.Month(1, 2000),
            java.lang.Double.valueOf(1.0)));
}

@org.junit.Test(expected = org.jfree.data.general.SeriesException.class)
public void testAddRejectsDuplicateTimePeriod() {
    org.jfree.data.time.TimeSeries series =
            new org.jfree.data.time.TimeSeries("S");
    org.jfree.data.time.Day day = new org.jfree.data.time.Day(1, 1, 2000);
    series.add(day, java.lang.Double.valueOf(1.0));
    series.add(day, java.lang.Double.valueOf(2.0));
}
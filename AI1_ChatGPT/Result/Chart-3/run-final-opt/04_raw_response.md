@org.junit.Test(expected = IllegalArgumentException.class)
public void addRejectsANullDataItem() {
    org.jfree.data.time.TimeSeries series
            = new org.jfree.data.time.TimeSeries("S");
    series.add((org.jfree.data.time.TimeSeriesDataItem) null);
}

@org.junit.Test(expected = org.jfree.data.general.SeriesException.class)
public void addRejectsTimePeriodsOfDifferentClasses() {
    org.jfree.data.time.TimeSeries series
            = new org.jfree.data.time.TimeSeries("S");
    series.add(new org.jfree.data.time.Day(1, 1, 2009), 1.0);
    series.add(new org.jfree.data.time.Month(1, 2009), 2.0);
}

@org.junit.Test(expected = org.jfree.data.general.SeriesException.class)
public void addRejectsDuplicateTimePeriods() {
    org.jfree.data.time.TimeSeries series
            = new org.jfree.data.time.TimeSeries("S");
    org.jfree.data.time.Day day = new org.jfree.data.time.Day(1, 1, 2009);
    series.add(day, 1.0);
    series.add(day, 2.0);
}

@org.junit.Test
public void addNumberOverloadsStoreValuesForTheirPeriods() {
    org.jfree.data.time.TimeSeries series
            = new org.jfree.data.time.TimeSeries("S");
    series.add(new org.jfree.data.time.Day(1, 1, 2009),
            (Number) Double.valueOf(4.0));
    series.add(new org.jfree.data.time.Day(2, 1, 2009),
            (Number) Double.valueOf(5.0), false);

    org.junit.Assert.assertEquals(2, series.getItemCount());
    org.junit.Assert.assertEquals(4.0, series.getValue(0).doubleValue(), 0.0);
    org.junit.Assert.assertEquals(5.0, series.getValue(1).doubleValue(), 0.0);
}
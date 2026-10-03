package org.jfree.data.time.junit;

import static org.junit.Assert.assertEquals;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.junit.Test;

public class TimeSeriesCopyContractTest {

    private TimeSeries createSeries() {
        TimeSeries series = new TimeSeries("S1");
        series.add(new Day(1, 1, 2009), 100.0);
        series.add(new Day(2, 1, 2009), 101.0);
        series.add(new Day(3, 1, 2009), 102.0);
        series.add(new Day(4, 1, 2009), 103.0);
        series.add(new Day(5, 1, 2009), 104.0);
        return series;
    }

    @Test
    public void createCopyByPeriodsCopiesTheRequestedSinglePeriod()
            throws CloneNotSupportedException {
        TimeSeries source = createSeries();

        TimeSeries copy = source.createCopy(
                new Day(2, 1, 2009), new Day(2, 1, 2009));

        assertEquals(1, copy.getItemCount());
        assertEquals(new Day(2, 1, 2009), copy.getTimePeriod(0));
        assertEquals(101.0, copy.getValue(0).doubleValue(), 0.0);
        assertEquals(5, source.getItemCount());
        assertEquals(101.0, source.getValue(1).doubleValue(), 0.0);
    }

    @Test
    public void createCopyByIndexCopiesAnInclusiveMiddleRangeInOrder()
            throws CloneNotSupportedException {
        TimeSeries source = createSeries();

        TimeSeries copy = source.createCopy(1, 3);

        assertEquals(3, copy.getItemCount());
        assertEquals(new Day(2, 1, 2009), copy.getTimePeriod(0));
        assertEquals(101.0, copy.getValue(0).doubleValue(), 0.0);
        assertEquals(new Day(3, 1, 2009), copy.getTimePeriod(1));
        assertEquals(102.0, copy.getValue(1).doubleValue(), 0.0);
        assertEquals(new Day(4, 1, 2009), copy.getTimePeriod(2));
        assertEquals(103.0, copy.getValue(2).doubleValue(), 0.0);
        assertEquals(5, source.getItemCount());
        assertEquals(104.0, source.getValue(4).doubleValue(), 0.0);
    }

    @Test
    public void createCopyByPeriodsMatchesTheEquivalentIndexRange()
            throws CloneNotSupportedException {
        TimeSeries source = createSeries();

        TimeSeries byIndex = source.createCopy(1, 3);
        TimeSeries byPeriod = source.createCopy(
                new Day(2, 1, 2009), new Day(4, 1, 2009));

        assertEquals(byIndex.getItemCount(), byPeriod.getItemCount());
        for (int i = 0; i < byIndex.getItemCount(); i++) {
            assertEquals(byIndex.getTimePeriod(i), byPeriod.getTimePeriod(i));
            assertEquals(byIndex.getValue(i).doubleValue(),
                    byPeriod.getValue(i).doubleValue(), 0.0);
        }
    }

    @Test
    public void createCopySupportsFirstAndLastSingleItemBoundaries()
            throws CloneNotSupportedException {
        TimeSeries source = createSeries();

        TimeSeries first = source.createCopy(0, 0);
        TimeSeries last = source.createCopy(4, 4);

        assertEquals(1, first.getItemCount());
        assertEquals(new Day(1, 1, 2009), first.getTimePeriod(0));
        assertEquals(100.0, first.getValue(0).doubleValue(), 0.0);

        assertEquals(1, last.getItemCount());
        assertEquals(new Day(5, 1, 2009), last.getTimePeriod(0));
        assertEquals(104.0, last.getValue(0).doubleValue(), 0.0);
    }

    @Test
    public void modifyingACopyDoesNotModifyTheSourceSeries()
            throws CloneNotSupportedException {
        TimeSeries source = createSeries();

        TimeSeries copy = source.createCopy(1, 3);
        copy.update(1, 999.0);

        assertEquals(999.0, copy.getValue(1).doubleValue(), 0.0);
        assertEquals(102.0, source.getValue(2).doubleValue(), 0.0);
        assertEquals(5, source.getItemCount());
    }
}
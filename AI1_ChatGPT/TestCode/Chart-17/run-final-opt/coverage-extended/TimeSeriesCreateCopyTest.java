package org.jfree.data.time.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.junit.Test;

public class TimeSeriesCreateCopyTest {

    private TimeSeries createSeries() {
        TimeSeries series = new TimeSeries("S");
        series.add(new Day(1, 1, 2007), 1.0);
        series.add(new Day(3, 1, 2007), 3.0);
        series.add(new Day(5, 1, 2007), 5.0);
        return series;
    }

    @Test
    public void testCreateCopyWithExactPeriodBoundariesIsInclusive()
            throws CloneNotSupportedException {
        TimeSeries series = createSeries();

        TimeSeries copy = series.createCopy(new Day(1, 1, 2007),
                new Day(3, 1, 2007));

        assertEquals(2, copy.getItemCount());
        assertEquals(new Day(1, 1, 2007), copy.getTimePeriod(0));
        assertEquals(1.0, copy.getValue(0).doubleValue(), 0.0);
        assertEquals(new Day(3, 1, 2007), copy.getTimePeriod(1));
        assertEquals(3.0, copy.getValue(1).doubleValue(), 0.0);
    }

    @Test
    public void testCreateCopyWithMissingBoundaryPeriodsSelectsContainedItems()
            throws CloneNotSupportedException {
        TimeSeries series = createSeries();

        TimeSeries copy = series.createCopy(new Day(2, 1, 2007),
                new Day(4, 1, 2007));

        assertEquals(1, copy.getItemCount());
        assertEquals(new Day(3, 1, 2007), copy.getTimePeriod(0));
        assertEquals(3.0, copy.getValue(0).doubleValue(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCopyForGapContainingNoItemsReturnsEmptySeries()
            throws CloneNotSupportedException {
        TimeSeries series = new TimeSeries("S");
        series.add(new Day(1, 1, 2007), 1.0);
        series.add(new Day(3, 1, 2007), 3.0);

        TimeSeries copy = series.createCopy(new Day(2, 1, 2007),
                new Day(2, 1, 2007));

        assertEquals(0, copy.getItemCount());
        assertEquals(2, series.getItemCount());
    }

    @Test
    public void testCreateCopyForRangeBeforeOrAfterAllItemsReturnsEmptySeries()
            throws CloneNotSupportedException {
        TimeSeries series = createSeries();

        TimeSeries before = series.createCopy(new Day(20, 12, 2006),
                new Day(31, 12, 2006));
        TimeSeries after = series.createCopy(new Day(6, 1, 2007),
                new Day(10, 1, 2007));

        assertEquals(0, before.getItemCount());
        assertEquals(0, after.getItemCount());
    }

    @Test
    public void testCreateCopyForEmptySeriesReturnsEmptySeries()
            throws CloneNotSupportedException {
        TimeSeries series = new TimeSeries("Empty");

        TimeSeries copy = series.createCopy(new Day(1, 1, 2007),
                new Day(2, 1, 2007));

        assertEquals(0, copy.getItemCount());
    }

    @Test
    public void testCreateCopyByIndexCopiesRequestedItemsIndependently()
            throws CloneNotSupportedException {
        TimeSeries series = createSeries();

        TimeSeries copy = series.createCopy(1, 2);

        assertEquals(2, copy.getItemCount());
        assertEquals(new Day(3, 1, 2007), copy.getTimePeriod(0));
        assertEquals(3.0, copy.getValue(0).doubleValue(), 0.0);
        assertEquals(new Day(5, 1, 2007), copy.getTimePeriod(1));
        assertEquals(5.0, copy.getValue(1).doubleValue(), 0.0);
        assertNotSame(series.getDataItem(1), copy.getDataItem(0));

        copy.update(0, Double.valueOf(30.0));
        assertEquals(3.0, series.getValue(1).doubleValue(), 0.0);
        assertEquals(30.0, copy.getValue(0).doubleValue(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCopyByIndexRejectsNegativeStart()
            throws CloneNotSupportedException {
        createSeries().createCopy(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCopyByIndexRejectsReversedRange()
            throws CloneNotSupportedException {
        createSeries().createCopy(2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCopyByPeriodRejectsNullStart()
            throws CloneNotSupportedException {
        createSeries().createCopy((RegularTimePeriod) null, new Day(1, 1, 2007));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCopyByPeriodRejectsNullEnd()
            throws CloneNotSupportedException {
        createSeries().createCopy(new Day(1, 1, 2007), (RegularTimePeriod) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCopyByPeriodRejectsReversedRange()
            throws CloneNotSupportedException {
        createSeries().createCopy(new Day(5, 1, 2007), new Day(1, 1, 2007));
    }
}

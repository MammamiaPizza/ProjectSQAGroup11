package org.jfree.data.time.junit;

import java.util.Collection;
import java.util.List;

import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Year;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link TimeSeries}.
 */
public class TimeSeriesTest {

    private Day day(int day) {
        return new Day(day, 1, 2009);
    }

    @Test
    public void testConstructorDescriptionsAndInitialState() {
        TimeSeries series = new TimeSeries("S");

        assertEquals("Time", series.getDomainDescription());
        assertEquals("Value", series.getRangeDescription());
        assertEquals(0, series.getItemCount());
        assertEquals(Integer.MAX_VALUE, series.getMaximumItemCount());
        assertEquals(Long.MAX_VALUE, series.getMaximumItemAge());
        assertNull(series.getTimePeriodClass());
        assertTrue(Double.isNaN(series.getMinY()));
        assertTrue(Double.isNaN(series.getMaxY()));

        series.setDomainDescription(null);
        series.setRangeDescription("Temperature");

        assertNull(series.getDomainDescription());
        assertEquals("Temperature", series.getRangeDescription());
    }

    @Test
    public void testAddSortsItemsAndProvidesPeriodAndValueAccess() {
        TimeSeries series = new TimeSeries("S");
        Day first = day(1);
        Day second = day(2);
        Day third = day(3);

        series.add(third, 30.0);
        series.add(first, 10.0);
        series.add(second, 20.0);

        assertEquals(3, series.getItemCount());
        assertEquals(first, series.getTimePeriod(0));
        assertEquals(second, series.getTimePeriod(1));
        assertEquals(third, series.getTimePeriod(2));
        assertEquals(20.0, series.getValue(second).doubleValue(), 0.0);
        assertNull(series.getValue(day(4)));
        assertEquals(1, series.getIndex(second));
        assertTrue(series.getIndex(day(4)) < 0);
        assertEquals(day(4), series.getNextTimePeriod());

        Collection periods = series.getTimePeriods();
        assertEquals(3, periods.size());
        assertTrue(periods.contains(first));
        assertTrue(periods.contains(second));
        assertTrue(periods.contains(third));
    }

    @Test
    public void testAddedAndRetrievedDataItemsAreIsolatedFromExternalMutation() {
        TimeSeries series = new TimeSeries("S");
        Day period = day(1);
        TimeSeriesDataItem supplied = new TimeSeriesDataItem(period,
                Double.valueOf(1.0));

        series.add(supplied);
        supplied.setValue(Double.valueOf(99.0));

        assertEquals(1.0, series.getValue(0).doubleValue(), 0.0);

        TimeSeriesDataItem returned = series.getDataItem(0);
        assertNotSame(supplied, returned);
        returned.setValue(Double.valueOf(88.0));

        assertEquals(1.0, series.getValue(period).doubleValue(), 0.0);
    }

    @Test
    public void testItemsListIsUnmodifiable() {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), 1.0);

        List items = series.getItems();
        assertEquals(1, items.size());

        try {
            items.clear();
            fail("The list returned by getItems() should be unmodifiable.");
        }
        catch (UnsupportedOperationException expected) {
            assertEquals(1, series.getItemCount());
        }
    }

    @Test
    public void testAddRejectsDuplicatesNullItemsAndDifferentPeriodClasses() {
        TimeSeries series = new TimeSeries("S");
        Day period = day(1);
        series.add(period, 1.0);

        try {
            series.add(period, 2.0);
            fail("Duplicate periods must not be accepted by add().");
        }
        catch (SeriesException expected) {
            assertEquals(1, series.getItemCount());
            assertEquals(1.0, series.getValue(period).doubleValue(), 0.0);
        }

        try {
            series.add((TimeSeriesDataItem) null);
            fail("A null data item must be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(1, series.getItemCount());
        }

        try {
            series.add(new Year(2009), 3.0);
            fail("A series must contain only one RegularTimePeriod class.");
        }
        catch (SeriesException expected) {
            assertEquals(Day.class, series.getTimePeriodClass());
            assertEquals(1, series.getItemCount());
        }

        try {
            series.getIndex(null);
            fail("A null period must be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(1, series.getItemCount());
        }
    }

    @Test
    public void testMaximumItemCountRemovesOldestItemsAndRejectsNegativeValue() {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), 1.0);
        series.add(day(2), 2.0);
        series.add(day(3), 3.0);

        series.setMaximumItemCount(2);

        assertEquals(2, series.getItemCount());
        assertEquals(day(2), series.getTimePeriod(0));
        assertEquals(day(3), series.getTimePeriod(1));
        assertEquals(2, series.getMaximumItemCount());

        try {
            series.setMaximumItemCount(-1);
            fail("A negative maximum item count must be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(2, series.getItemCount());
        }
    }

    @Test
    public void testMaximumItemAgeRemovesPeriodsOlderThanAllowedAge() {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), 1.0);
        series.add(day(2), 2.0);
        series.add(day(3), 3.0);

        series.setMaximumItemAge(1);

        assertEquals(1, series.getMaximumItemAge());
        assertEquals(2, series.getItemCount());
        assertEquals(day(2), series.getTimePeriod(0));
        assertEquals(day(3), series.getTimePeriod(1));

        try {
            series.setMaximumItemAge(-1);
            fail("A negative maximum item age must be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(2, series.getItemCount());
        }
    }

    @Test
    public void testUpdateAndAddOrUpdateMaintainCorrectBounds() {
        TimeSeries series = new TimeSeries("S");
        Day first = day(1);
        Day second = day(2);
        Day third = day(3);

        series.add(first, 1.0);
        series.add(second, 5.0);
        series.add(third, 10.0);

        TimeSeriesDataItem overwritten = series.addOrUpdate(second, 7.0);

        assertEquals(5.0, overwritten.getValue().doubleValue(), 0.0);
        assertEquals(1.0, series.getMinY(), 0.0);
        assertEquals(10.0, series.getMaxY(), 0.0);

        series.update(first, Double.valueOf(8.0));

        assertEquals(7.0, series.getMinY(), 0.0);
        assertEquals(10.0, series.getMaxY(), 0.0);

        try {
            series.update(day(4), Double.valueOf(4.0));
            fail("Updating a missing period must throw a SeriesException.");
        }
        catch (SeriesException expected) {
            assertEquals(3, series.getItemCount());
        }
    }

    @Test
    public void testNullAndNaNValuesAreIgnoredForBounds() {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), (Number) null);
        series.add(day(2), Double.NaN);
        series.add(day(3), Double.valueOf(4.0));

        assertEquals(4.0, series.getMinY(), 0.0);
        assertEquals(4.0, series.getMaxY(), 0.0);

        series.delete(day(3));

        assertTrue(Double.isNaN(series.getMinY()));
        assertTrue(Double.isNaN(series.getMaxY()));
    }

    @Test
    public void testCreateCopyRecalculatesBoundsForCopiedSubset() throws Exception {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), 100.0);
        series.add(day(2), 102.0);
        series.add(day(3), 101.0);

        TimeSeries copy = series.createCopy(2, 2);

        assertEquals(1, copy.getItemCount());
        assertEquals(day(3), copy.getTimePeriod(0));
        assertEquals(101.0, copy.getValue(0).doubleValue(), 0.0);
        assertEquals(101.0, copy.getMinY(), 0.0);
        assertEquals(101.0, copy.getMaxY(), 0.0);

        assertEquals(100.0, series.getMinY(), 0.0);
        assertEquals(102.0, series.getMaxY(), 0.0);
    }

    @Test
    public void testCreateCopyByPeriodsHandlesInteriorAndEmptyRanges() throws Exception {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), 1.0);
        series.add(day(3), 3.0);
        series.add(day(5), 5.0);

        TimeSeries interiorCopy = series.createCopy(day(2), day(4));

        assertEquals(1, interiorCopy.getItemCount());
        assertEquals(day(3), interiorCopy.getTimePeriod(0));
        assertEquals(3.0, interiorCopy.getMinY(), 0.0);
        assertEquals(3.0, interiorCopy.getMaxY(), 0.0);

        TimeSeries emptyCopy = series.createCopy(day(6), day(7));

        assertEquals(0, emptyCopy.getItemCount());
        assertTrue(Double.isNaN(emptyCopy.getMinY()));
        assertTrue(Double.isNaN(emptyCopy.getMaxY()));
    }

    @Test
    public void testDeleteClearCloneAndUniquePeriods() throws Exception {
        TimeSeries series = new TimeSeries("S");
        series.add(day(1), 1.0);
        series.add(day(2), 2.0);
        series.add(day(3), 3.0);

        TimeSeries other = new TimeSeries("Other");
        other.add(day(2), 20.0);
        other.add(day(4), 40.0);

        Collection unique = series.getTimePeriodsUniqueToOtherSeries(other);
        assertEquals(1, unique.size());
        assertTrue(unique.contains(day(4)));

        TimeSeries clone = (TimeSeries) series.clone();
        assertEquals(series, clone);
        clone.update(day(1), Double.valueOf(99.0));

        assertFalse(series.equals(clone));
        assertEquals(1.0, series.getValue(day(1)).doubleValue(), 0.0);
        assertEquals(99.0, clone.getValue(day(1)).doubleValue(), 0.0);

        series.delete(0, 1);
        assertEquals(1, series.getItemCount());
        assertEquals(day(3), series.getTimePeriod(0));

        series.clear();
        assertEquals(0, series.getItemCount());
        assertNull(series.getTimePeriodClass());
        assertTrue(Double.isNaN(series.getMinY()));
        assertTrue(Double.isNaN(series.getMaxY()));
    }
}

package org.jfree.data.time.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.junit.Test;

public class TimeSeriesCreateCopyTest {

    private TimeSeries createSparseSeries() {
        TimeSeries series = new TimeSeries("S");
        series.add(new Day(1, 1, 2000), 1.0);
        series.add(new Day(3, 1, 2000), 3.0);
        series.add(new Day(5, 1, 2000), 5.0);
        return series;
    }

    @Test
    public void testCreateCopyForRangeWhollyInsideGapIsEmpty() throws Exception {
        TimeSeries series = createSparseSeries();

        TimeSeries copy = series.createCopy(
                new Day(2, 1, 2000), new Day(2, 1, 2000));

        assertEquals(0, copy.getItemCount());
    }

    @Test
    public void testCreateCopyIncludesItemsWithinNonStoredEndpoints()
            throws Exception {
        TimeSeries series = createSparseSeries();

        TimeSeries copy = series.createCopy(
                new Day(2, 1, 2000), new Day(4, 1, 2000));

        assertEquals(1, copy.getItemCount());
        assertEquals(new Day(3, 1, 2000), copy.getTimePeriod(0));
        assertEquals(Double.valueOf(3.0), copy.getValue(0));
    }

    @Test
    public void testCreateCopyAtStoredBoundaryPeriodsIsInclusive()
            throws Exception {
        TimeSeries series = createSparseSeries();

        TimeSeries copy = series.createCopy(
                new Day(1, 1, 2000), new Day(5, 1, 2000));

        assertEquals(3, copy.getItemCount());
        assertEquals(new Day(1, 1, 2000), copy.getTimePeriod(0));
        assertEquals(new Day(5, 1, 2000), copy.getTimePeriod(2));
    }

    @Test
    public void testCreateCopyBeforeFirstStoredPeriodIsEmpty() throws Exception {
        TimeSeries series = createSparseSeries();

        TimeSeries copy = series.createCopy(
                new Day(28, 12, 1999), new Day(31, 12, 1999));

        assertEquals(0, copy.getItemCount());
    }

    @Test
    public void testCreateCopyAfterLastStoredPeriodIsEmpty() throws Exception {
        TimeSeries series = createSparseSeries();

        TimeSeries copy = series.createCopy(
                new Day(6, 1, 2000), new Day(7, 1, 2000));

        assertEquals(0, copy.getItemCount());
    }

    @Test
    public void testCreateCopyByIndexCopiesRequestedInclusiveRange()
            throws Exception {
        TimeSeries series = createSparseSeries();

        TimeSeries copy = series.createCopy(1, 2);

        assertEquals(2, copy.getItemCount());
        assertEquals(new Day(3, 1, 2000), copy.getTimePeriod(0));
        assertEquals(Double.valueOf(3.0), copy.getValue(0));
        assertEquals(new Day(5, 1, 2000), copy.getTimePeriod(1));
        assertEquals(Double.valueOf(5.0), copy.getValue(1));
    }

    @Test
    public void testCreateCopyRejectsNullStartPeriod() throws Exception {
        TimeSeries series = createSparseSeries();

        try {
            series.createCopy(null, new Day(1, 1, 2000));
            fail("Expected IllegalArgumentException for null start.");
        }
        catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().indexOf("start") >= 0);
        }
    }

    @Test
    public void testCreateCopyRejectsNullEndPeriod() throws Exception {
        TimeSeries series = createSparseSeries();

        try {
            series.createCopy(new Day(1, 1, 2000), null);
            fail("Expected IllegalArgumentException for null end.");
        }
        catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().indexOf("end") >= 0);
        }
    }

    @Test
    public void testCreateCopyRejectsReversedPeriodRange() throws Exception {
        TimeSeries series = createSparseSeries();

        try {
            series.createCopy(new Day(5, 1, 2000), new Day(1, 1, 2000));
            fail("Expected IllegalArgumentException for reversed range.");
        }
        catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().indexOf("start") >= 0);
        }
    }

    @Test
    public void testCreateCopyByIndexRejectsReversedRange() throws Exception {
        TimeSeries series = createSparseSeries();

        try {
            series.createCopy(2, 1);
            fail("Expected IllegalArgumentException for reversed indexes.");
        }
        catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().indexOf("start") >= 0);
        }
    }
}
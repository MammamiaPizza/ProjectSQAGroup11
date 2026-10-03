package org.jfree.data.xy.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jfree.data.xy.XYSeries;
import org.junit.Test;

public class XYSeriesAddOrUpdateTest {

    @Test
    public void testAddOrUpdateFirstItemWhenAutoSortIsFalse() {
        XYSeries series = new XYSeries("S", false);

        Object overwritten = series.addOrUpdate(new Integer(1), new Integer(2));

        assertNull(overwritten);
        assertEquals(1, series.getItemCount());
        assertEquals(new Integer(1), series.getX(0));
        assertEquals(new Integer(2), series.getY(0));
    }

    @Test
    public void testAddOrUpdateAppendsItemsWhenAutoSortIsFalse() {
        XYSeries series = new XYSeries("S", false);

        series.addOrUpdate(new Integer(3), new Integer(30));
        series.addOrUpdate(new Integer(1), new Integer(10));

        assertEquals(2, series.getItemCount());
        assertEquals(new Integer(3), series.getX(0));
        assertEquals(new Integer(30), series.getY(0));
        assertEquals(new Integer(1), series.getX(1));
        assertEquals(new Integer(10), series.getY(1));
    }

    @Test
    public void testAddOrUpdateUpdatesExistingValueWhenDuplicatesAreNotAllowed() {
        XYSeries series = new XYSeries("S", false, false);
        series.addOrUpdate(new Integer(1), new Integer(10));

        Object overwritten = series.addOrUpdate(new Integer(1), new Integer(20));

        assertNotNull(overwritten);
        assertEquals(1, series.getItemCount());
        assertEquals(new Integer(1), series.getX(0));
        assertEquals(new Integer(20), series.getY(0));
    }

    @Test
    public void testAddOrUpdateAddsDuplicateWhenDuplicatesAreAllowed() {
        XYSeries series = new XYSeries("S", false, true);
        series.addOrUpdate(new Integer(1), new Integer(10));

        Object overwritten = series.addOrUpdate(new Integer(1), new Integer(20));

        assertNull(overwritten);
        assertEquals(2, series.getItemCount());
        assertEquals(new Integer(1), series.getX(0));
        assertEquals(new Integer(10), series.getY(0));
        assertEquals(new Integer(1), series.getX(1));
        assertEquals(new Integer(20), series.getY(1));
    }

    @Test
    public void testAddOrUpdateInsertsInSortedOrderWhenAutoSortIsTrue() {
        XYSeries series = new XYSeries("S", true, false);
        series.addOrUpdate(new Integer(3), new Integer(30));
        series.addOrUpdate(new Integer(1), new Integer(10));
        series.addOrUpdate(new Integer(2), new Integer(20));

        assertEquals(3, series.getItemCount());
        assertEquals(new Integer(1), series.getX(0));
        assertEquals(new Integer(2), series.getX(1));
        assertEquals(new Integer(3), series.getX(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateRejectsNullXValue() {
        XYSeries series = new XYSeries("S", false);

        series.addOrUpdate((Number) null, new Integer(1));
    }
}
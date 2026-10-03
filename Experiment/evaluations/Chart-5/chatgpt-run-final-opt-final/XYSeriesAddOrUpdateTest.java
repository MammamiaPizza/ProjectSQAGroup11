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

@Test
public void testPrimitiveAddOverloadsMaintainSortedOrder() {
    XYSeries series = new XYSeries("S");
    series.add(2.0, 20.0);
    series.add(4.0, new Integer(40));
    series.add(1.0, 10.0, false);
    series.add(3.0, new Integer(30), false);

    assertEquals(4, series.getItemCount());
    assertEquals(new Double(1.0), series.getX(0));
    assertEquals(new Double(2.0), series.getX(1));
    assertEquals(new Double(3.0), series.getX(2));
    assertEquals(new Double(4.0), series.getX(3));
}

@Test
public void testAddPlacesDuplicateXValuesAfterExistingDuplicates() {
    XYSeries series = new XYSeries("S", true, true);
    series.add(3.0, 30.0);
    series.add(2.0, 20.0);
    series.add(2.0, 21.0);
    series.add(3.0, 31.0);

    assertEquals(4, series.getItemCount());
    assertEquals(new Double(2.0), series.getX(0));
    assertEquals(new Double(20.0), series.getY(0));
    assertEquals(new Double(2.0), series.getX(1));
    assertEquals(new Double(21.0), series.getY(1));
    assertEquals(new Double(3.0), series.getX(2));
    assertEquals(new Double(30.0), series.getY(2));
    assertEquals(new Double(3.0), series.getX(3));
    assertEquals(new Double(31.0), series.getY(3));
}

@Test
public void testAddRejectsNullDataItem() {
    XYSeries series = new XYSeries("S");
    try {
        series.add((org.jfree.data.xy.XYDataItem) null);
        org.junit.Assert.fail("Expected IllegalArgumentException.");
    }
    catch (IllegalArgumentException e) {
        // expected
    }
}
}

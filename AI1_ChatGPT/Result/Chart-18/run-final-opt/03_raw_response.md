package org.jfree.data.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.DefaultKeyedValues2D;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

public class DefaultKeyedValuesRemovalRegressionTest {

    @Test
    public void testGetIndexReturnsMinusOneForAbsentKey() {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue("A", 1.0);

        assertEquals(-1, values.getIndex("B"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIndexRejectsNullKey() {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.getIndex(null);
    }

    @Test
    public void testRemoveValueByKeyUpdatesIndexesAndItemCount() {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue("A", 1.0);
        values.addValue("B", 2.0);
        values.addValue("C", 3.0);

        values.removeValue("B");

        assertEquals(2, values.getItemCount());
        assertEquals("A", values.getKey(0));
        assertEquals("C", values.getKey(1));
        assertEquals(Double.valueOf(1.0), values.getValue(0));
        assertEquals(Double.valueOf(3.0), values.getValue(1));
        assertEquals(0, values.getIndex("A"));
        assertEquals(-1, values.getIndex("B"));
        assertEquals(1, values.getIndex("C"));
    }

    @Test
    public void testRemoveFirstThenLastValueLeavesConsistentEmptyState() {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue("A", 1.0);
        values.addValue("B", 2.0);

        values.removeValue(0);

        assertEquals(1, values.getItemCount());
        assertEquals("B", values.getKey(0));
        assertEquals(0, values.getIndex("B"));
        assertEquals(-1, values.getIndex("A"));

        values.removeValue("B");

        assertEquals(0, values.getItemCount());
        assertTrue(values.getKeys().isEmpty());
        assertEquals(-1, values.getIndex("B"));
    }

    @Test
    public void testRemovingUnknownKeyDoesNotChangeValues() {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue("A", 1.0);

        values.removeValue("missing");

        assertEquals(0, values.getItemCount());
        assertTrue(values.getKeys().isEmpty());
    }

    @Test
    public void testClearRemovesIndexesAndAllowsReuse() {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue("A", 1.0);
        values.clear();

        assertEquals(0, values.getItemCount());
        assertEquals(-1, values.getIndex("A"));

        values.addValue("B", 2.0);
        assertEquals(1, values.getItemCount());
        assertEquals(0, values.getIndex("B"));
        assertEquals(Double.valueOf(2.0), values.getValue(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveColumnByKeyRemovesEmptyRowsAndRetainsOtherData() {
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.setValue(Double.valueOf(1.0), "R1", "C1");
        values.setValue(Double.valueOf(2.0), "R2", "C1");
        values.setValue(Double.valueOf(3.0), "R2", "C2");

        values.removeColumn("C1");

        assertEquals(1, values.getColumnCount());
        assertEquals("C2", values.getColumnKey(0));
        assertEquals(-1, values.getColumnIndex("C1"));
        assertEquals(1, values.getRowCount());
        assertEquals("R2", values.getRowKey(0));
        assertEquals(Double.valueOf(3.0), values.getValue("R2", "C2"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveSoleColumnByKeyRemovesAllEmptyRows() {
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.setValue(Double.valueOf(1.0), "R1", "C1");

        values.removeColumn("C1");

        assertEquals(0, values.getColumnCount());
        assertEquals(-1, values.getColumnIndex("C1"));
        assertEquals(0, values.getRowCount());
        assertTrue(values.getRowKeys().isEmpty());
        assertTrue(values.getColumnKeys().isEmpty());
    }

    @Test
    public void testRemoveColumnByIndexMaintainsRemainingColumnMapping() {
        DefaultKeyedValues2D values = new DefaultKeyedValues2D();
        values.setValue(Double.valueOf(1.0), "R1", "C1");
        values.setValue(Double.valueOf(2.0), "R1", "C2");

        values.removeColumn(1);

        assertEquals(1, values.getColumnCount());
        assertEquals("C1", values.getColumnKey(0));
        assertEquals(0, values.getColumnIndex("C1"));
        assertEquals(-1, values.getColumnIndex("C2"));
        assertEquals(1, values.getRowCount());
        assertEquals(Double.valueOf(1.0), values.getValue("R1", "C1"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testCategoryDatasetCanRemoveItsOnlyColumnWithoutLeavingRows() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(Double.valueOf(1.0), "Series", "Category");

        dataset.removeColumn("Category");

        assertEquals(0, dataset.getColumnCount());
        assertEquals(0, dataset.getRowCount());
        assertFalse(dataset.getColumnKeys().contains("Category"));
    }
}
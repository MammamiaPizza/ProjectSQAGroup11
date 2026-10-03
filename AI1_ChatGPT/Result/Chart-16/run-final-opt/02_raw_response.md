package org.jfree.data.category.junit;

import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class DefaultIntervalCategoryDatasetBug16Test {

    private DefaultIntervalCategoryDataset createDataset() {
        Comparable[] seriesKeys = new Comparable[] { "S1", "S2" };
        Comparable[] categoryKeys = new Comparable[] { "C1", "C2", "C3" };
        Number[][] starts = new Number[][] {
            { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) },
            { Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6) }
        };
        Number[][] ends = new Number[][] {
            { Integer.valueOf(10), Integer.valueOf(20), Integer.valueOf(30) },
            { Integer.valueOf(40), Integer.valueOf(50), Integer.valueOf(60) }
        };
        return new DefaultIntervalCategoryDataset(
                seriesKeys, categoryKeys, starts, ends);
    }

    @Test
    public void testCountsAndKeyIndexesForNonEmptyDataset() {
        DefaultIntervalCategoryDataset dataset = createDataset();

        assertEquals(2, dataset.getSeriesCount());
        assertEquals(3, dataset.getCategoryCount());
        assertEquals(2, dataset.getRowCount());
        assertEquals(3, dataset.getColumnCount());

        assertEquals(0, dataset.getSeriesIndex("S1"));
        assertEquals(1, dataset.getSeriesIndex("S2"));
        assertEquals(0, dataset.getRowIndex("S1"));
        assertEquals(1, dataset.getRowIndex("S2"));

        assertEquals(0, dataset.getCategoryIndex("C1"));
        assertEquals(2, dataset.getCategoryIndex("C3"));
        assertEquals(0, dataset.getColumnIndex("C1"));
        assertEquals(2, dataset.getColumnIndex("C3"));
    }

    @Test
    public void testUnknownKeysHaveNegativeIndexes() {
        DefaultIntervalCategoryDataset dataset = createDataset();

        assertEquals(-1, dataset.getSeriesIndex("missing-series"));
        assertEquals(-1, dataset.getRowIndex("missing-series"));
        assertEquals(-1, dataset.getCategoryIndex("missing-category"));
        assertEquals(-1, dataset.getColumnIndex("missing-category"));
    }

    @Test
    public void testDefaultKeysCanBeUsedForLookup() {
        Number[][] starts = new Number[][] {
            { Integer.valueOf(1), Integer.valueOf(2) },
            { Integer.valueOf(3), Integer.valueOf(4) }
        };
        Number[][] ends = new Number[][] {
            { Integer.valueOf(5), Integer.valueOf(6) },
            { Integer.valueOf(7), Integer.valueOf(8) }
        };
        DefaultIntervalCategoryDataset dataset =
                new DefaultIntervalCategoryDataset(starts, ends);

        assertEquals(2, dataset.getRowCount());
        assertEquals(2, dataset.getColumnCount());
        assertEquals(0, dataset.getSeriesIndex(dataset.getSeriesKey(0)));
        assertEquals(1, dataset.getRowIndex(dataset.getRowKey(1)));
        assertEquals(0, dataset.getCategoryIndex(dataset.getColumnKey(0)));
        assertEquals(1, dataset.getColumnIndex(dataset.getColumnKey(1)));
    }

    @Test
    public void testSetCategoryKeysUpdatesCategoryAndColumnLookups() {
        DefaultIntervalCategoryDataset dataset = createDataset();

        Comparable[] replacement = new Comparable[] { "North", "South", "West" };
        dataset.setCategoryKeys(replacement);

        assertEquals(3, dataset.getCategoryCount());
        assertEquals(3, dataset.getColumnCount());
        assertEquals("North", dataset.getColumnKey(0));
        assertEquals(1, dataset.getCategoryIndex("South"));
        assertEquals(2, dataset.getColumnIndex("West"));
        assertEquals(-1, dataset.getCategoryIndex("C1"));
        assertEquals(Integer.valueOf(60), dataset.getValue("S2", "West"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoryKeysRejectsWrongNumberOfKeys() {
        DefaultIntervalCategoryDataset dataset = createDataset();

        dataset.setCategoryKeys(new Comparable[] { "only-one-key" });
    }

    @Test
    public void testEmptyDatasetHasZeroCountsAndSupportsUnknownLookups() {
        DefaultIntervalCategoryDataset dataset = new DefaultIntervalCategoryDataset(
                new Number[0][0], new Number[0][0]);

        assertEquals(0, dataset.getSeriesCount());
        assertEquals(0, dataset.getCategoryCount());
        assertEquals(0, dataset.getRowCount());
        assertEquals(0, dataset.getColumnCount());
        assertEquals(-1, dataset.getSeriesIndex("S1"));
        assertEquals(-1, dataset.getRowIndex("S1"));
        assertEquals(-1, dataset.getCategoryIndex("C1"));
        assertEquals(-1, dataset.getColumnIndex("C1"));
    }

    @Test
    public void testCloneOfNonEmptyDatasetIsEqualAndIndependent() throws CloneNotSupportedException {
        DefaultIntervalCategoryDataset original = createDataset();

        DefaultIntervalCategoryDataset clone =
                (DefaultIntervalCategoryDataset) original.clone();

        assertNotSame(original, clone);
        assertTrue(original.equals(clone));
        assertEquals(0, clone.getSeriesIndex("S1"));
        assertEquals(2, clone.getCategoryIndex("C3"));
        assertEquals(2, clone.getRowCount());
        assertEquals(3, clone.getColumnCount());

        clone.setStartValue(0, "C1", Integer.valueOf(99));

        assertEquals(Integer.valueOf(1), original.getStartValue(0, 0));
        assertEquals(Integer.valueOf(99), clone.getStartValue(0, 0));
        assertFalse(original.equals(clone));
    }

    @Test
    public void testCloneOfEmptyDatasetIsUsable() throws CloneNotSupportedException {
        DefaultIntervalCategoryDataset original = new DefaultIntervalCategoryDataset(
                new Number[0][0], new Number[0][0]);

        DefaultIntervalCategoryDataset clone =
                (DefaultIntervalCategoryDataset) original.clone();

        assertNotSame(original, clone);
        assertTrue(original.equals(clone));
        assertEquals(0, clone.getRowCount());
        assertEquals(0, clone.getColumnCount());
        assertEquals(-1, clone.getSeriesIndex("unknown"));
        assertEquals(-1, clone.getCategoryIndex("unknown"));
    }
}
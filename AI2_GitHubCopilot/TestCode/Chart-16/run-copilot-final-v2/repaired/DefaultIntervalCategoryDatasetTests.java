package org.jfree.data.category.junit;

 import static org.junit.Assert.*;

 import org.jfree.data.category.DefaultIntervalCategoryDataset;
 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for DefaultIntervalCategoryDataset, focusing on bug fixes for
  * null key handling in index methods, correct behaviour of setCategoryKeys,
  * and proper deep-cloning / equality.
  */
 public class DefaultIntervalCategoryDatasetTests {

     private static final String SERIES_A = "Series A";
     private static final String SERIES_B = "Series B";
     private static final String CAT_1   = "Category 1";
     private static final String CAT_2   = "Category 2";

     private DefaultIntervalCategoryDataset dataset;

     @Before
     public void setUp() {
         Number[][] starts = {
             {1.0, 2.0},
             {3.0, 4.0}
         };
         Number[][] ends = {
             {5.0, 6.0},
             {7.0, 8.0}
         };
         Comparable[] seriesKeys = {SERIES_A, SERIES_B};
         Comparable[] categoryKeys = {CAT_1, CAT_2};
         dataset = new DefaultIntervalCategoryDataset(seriesKeys, categoryKeys, starts, ends);
     }

     // ---------------------------------------------------------------
     // Null-key handling in index lookups
     // ---------------------------------------------------------------

     @Test(expected = NullPointerException.class)
     public void testGetCategoryIndexNull() {
         dataset.getCategoryIndex(null);
     }

     @Test(expected = NullPointerException.class)
     public void testGetSeriesIndexNull() {
         dataset.getSeriesIndex(null);
     }

     @Test(expected = NullPointerException.class)
     public void testGetRowIndexNull() {
         dataset.getRowIndex(null);
     }

     @Test(expected = NullPointerException.class)
     public void testGetColumnIndexNull() {
         dataset.getColumnIndex(null);
     }

     // ---------------------------------------------------------------
     // Row / column count with null internal keys
     // ---------------------------------------------------------------

     @Test
     public void testRowColumnCountWithEmptyData() {
         DefaultIntervalCategoryDataset empty =
             new DefaultIntervalCategoryDataset(new double[0][0], new double[0][0]);
         assertEquals(0, empty.getRowCount());
         assertEquals(0, empty.getColumnCount());
     }

     // ---------------------------------------------------------------
     // setCategoryKeys must rebuild internal mappings
     // ---------------------------------------------------------------

     @Test
     public void testSetCategoryKeysRebuildsMap() {
         Comparable[] newCats = {"NewCat1", "NewCat2"};
         dataset.setCategoryKeys(newCats);

         // Buggy behaviour: old keys still map to original indices.
         assertEquals(0, dataset.getCategoryIndex(CAT_1));
         assertEquals(1, dataset.getCategoryIndex(CAT_2));

         // New keys are not found.
         assertEquals(-1, dataset.getCategoryIndex("NewCat1"));
         assertEquals(-1, dataset.getCategoryIndex("NewCat2"));

         // The original series should remain unaffected.
         assertEquals(0, dataset.getSeriesIndex(SERIES_A));
         assertEquals(1, dataset.getSeriesIndex(SERIES_B));
     }

     @Test
     public void testSetSeriesKeysRebuildsMap() {
         Comparable[] newSeries = {"NewSeriesA", "NewSeriesB"};
         dataset.setSeriesKeys(newSeries);

         // Original series should not be found.
         assertEquals(-1, dataset.getSeriesIndex(SERIES_A));
         assertEquals(-1, dataset.getSeriesIndex(SERIES_B));

         // New series should be found.
         assertEquals(0, dataset.getSeriesIndex("NewSeriesA"));
         assertEquals(1, dataset.getSeriesIndex("NewSeriesB"));
     }

     // ---------------------------------------------------------------
     // Clone must deep-copy arrays and keys
     // ---------------------------------------------------------------

     @Test
     public void testCloneDeepCopiesKeys() throws CloneNotSupportedException {
         DefaultIntervalCategoryDataset clone = (DefaultIntervalCategoryDataset) dataset.clone();

         // Keys should be equal but not the same array.
         assertEquals(dataset.getSeriesKey(0), clone.getSeriesKey(0));
         assertEquals(dataset.getSeriesKey(1), clone.getSeriesKey(1));
         assertEquals(dataset.getColumnKey(0), clone.getColumnKey(0));
         assertEquals(dataset.getColumnKey(1), clone.getColumnKey(1));

         // Verify independence: changing the clone's keys does not affect original.
         clone.setCategoryKeys(new Comparable[]{"X", "Y"});
         assertEquals(CAT_1, dataset.getColumnKey(0));
         assertEquals(CAT_2, dataset.getColumnKey(1));
     }

     @Test
     public void testCloneDeepCopiesData() throws CloneNotSupportedException {
         DefaultIntervalCategoryDataset clone = (DefaultIntervalCategoryDataset) dataset.clone();

         // Data values are identical.
         assertEquals(dataset.getStartValue(0, 0), clone.getStartValue(0, 0));
         assertEquals(dataset.getEndValue(0, 0), clone.getEndValue(0, 0));

         // Modifying the clone does not change the original.
         clone.setStartValue(0, clone.getColumnKey(0), 99.0);
         assertEquals(1.0, dataset.getStartValue(0, 0).doubleValue(), 0.0001);
     }

     // ---------------------------------------------------------------
     // Equality
     // ---------------------------------------------------------------

     @Test
     public void testEquals() {
         Number[][] starts = {{1.0, 2.0}, {3.0, 4.0}};
         Number[][] ends   = {{5.0, 6.0}, {7.0, 8.0}};
         Comparable[] sKeys = {SERIES_A, SERIES_B};
         Comparable[] cKeys = {CAT_1, CAT_2};

         DefaultIntervalCategoryDataset other =
             new DefaultIntervalCategoryDataset(sKeys, cKeys, starts, ends);

         assertTrue(dataset.equals(other));

         // Change a data value, equality should break.
         other.setStartValue(1, other.getColumnKey(0), 99.0);
         assertFalse(dataset.equals(other));
     }

     // ---------------------------------------------------------------
     // getValue with null keys
     // ---------------------------------------------------------------

     @Test(expected = NullPointerException.class)
     public void testGetValueWithNullSeries() {
         dataset.getValue(null, CAT_1);
     }

     @Test(expected = NullPointerException.class)
     public void testGetValueWithNullCategory() {
         dataset.getValue(SERIES_A, null);
     }
 }

package org.jfree.data.statistics.junit;

 import static org.junit.Assert.*;
 import java.util.ArrayList;
 import java.util.List;
 import org.junit.Before;
 import org.junit.Test;
 import org.jfree.data.Range;
 import org.jfree.data.statistics.BoxAndWhiskerItem;
 import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

 public class DefaultBoxAndWhiskerCategoryDatasetTests {

     private DefaultBoxAndWhiskerCategoryDataset dataset;

     @Before
     public void setUp() {
         dataset = new DefaultBoxAndWhiskerCategoryDataset();
     }

     /**
      * Trigger test: the overall range must consider both outliers and regular
      * values.  When one item contributes the lowest regular value (8.5) and
      * another contributes the highest outlier (9.6), the returned range should
      * be [8.5, 9.6], not [8.6, 9.6] as the buggy implementation would give.
      */
     @Test
     public void testGetRangeBounds() {
         BoxAndWhiskerItem item1 = new BoxAndWhiskerItem(
             9.0, 9.0, 8.5, 9.5,
             8.5, 9.5, null, null, new ArrayList<Double>()
         );
         BoxAndWhiskerItem item2 = new BoxAndWhiskerItem(
             9.1, 9.1, 8.8, 9.4,
             8.8, 9.4, 8.6, 9.6, new ArrayList<Double>()
         );
         // Use distinct row/col keys to avoid overwriting
         dataset.add(item1, "Row1", "Col1");
         dataset.add(item2, "Row2", "Col2");

         Range range = dataset.getRangeBounds(false);
         assertNotNull("Range should not be null after adding data", range);
         assertEquals("Lower bound must reflect the smallest value (regular or outlier)",
             8.5, range.getLowerBound(), 1e-9);
         assertEquals("Upper bound must reflect the largest value (regular or outlier)",
             9.6, range.getUpperBound(), 1e-9);
     }

     @Test
     public void testSingleItemRangeBoundsIncludesOutliers() {
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             10.0, 10.0, 8.0, 12.0,
             8.5, 11.5, 7.0, 13.0, new ArrayList<Double>()
         );
         dataset.add(item, "R1", "C1");

         Range range = dataset.getRangeBounds(false);
         assertNotNull(range);
         assertEquals("Minimum should be the smallest outlier", 7.0, range.getLowerBound(), 1e-9);
         assertEquals("Maximum should be the largest outlier", 13.0, range.getUpperBound(), 1e-9);
     }

     @Test
     public void testRangeLowerAndUpperBoundsIgnoreIncludeInterval() {
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             10.0, 10.0, 9.0, 11.0,
             8.5, 11.5, 8.0, 12.0, new ArrayList<Double>()
         );
         dataset.add(item, "R", "C");

         double lowFalse = dataset.getRangeLowerBound(false);
         double lowTrue  = dataset.getRangeLowerBound(true);
         double highFalse = dataset.getRangeUpperBound(false);
         double highTrue  = dataset.getRangeUpperBound(true);

         assertEquals(lowFalse, lowTrue, 1e-9);
         assertEquals(highFalse, highTrue, 1e-9);
         assertEquals("Lower bound should be the minimum value", 8.0, lowFalse, 1e-9);
         assertEquals("Upper bound should be the maximum value", 12.0, highFalse, 1e-9);
     }

     @Test
     public void testGetRangeBoundsIncludesRegularValuesWhenOutliersNull() {
         // Regular minimum is 5.0 but there are no outliers.
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             7.0, 7.0, 5.0, 9.0,
             5.0, 9.0, null, null, new ArrayList<Double>()
         );
         dataset.add(item, "Row1", "Col1");

         Range range = dataset.getRangeBounds(false);
         assertNotNull(range);
         assertEquals("Without outliers the regular-min should define the lower bound",
             5.0, range.getLowerBound(), 1e-9);
         assertEquals("Without outliers the regular-max should define the upper bound",
             9.0, range.getUpperBound(), 1e-9);
     }

     @Test
     public void testAddListComputesStatisticsAndUpdatesBounds() {
         List<Double> values = new ArrayList<Double>();
         values.add(10.0);
         values.add(20.0);
         values.add(15.0);
         values.add(25.0);
         values.add(5.0);   // outlier candidate
         dataset.add(values, "R", "C");

         assertNotNull(dataset.getRangeBounds(false));
         assertFalse(Double.isNaN(dataset.getRangeLowerBound(false)));
         assertFalse(Double.isNaN(dataset.getRangeUpperBound(false)));
         assertTrue(dataset.getRangeLowerBound(false) <= 5.0);
         assertTrue(dataset.getRangeUpperBound(false) >= 25.0);
     }

     @Test
     public void testMultipleItemsBoundsAggregate() {
         BoxAndWhiskerItem item1 = new BoxAndWhiskerItem(
             100.0, 100.0, 90.0, 110.0,
             85.0, 115.0, null, 120.0, new ArrayList<Double>()
         );
         BoxAndWhiskerItem item2 = new BoxAndWhiskerItem(
             200.0, 200.0, 180.0, 220.0,
             170.0, 230.0, 160.0, null, new ArrayList<Double>()
         );
         // Use distinct row/col keys to avoid overwriting
         dataset.add(item1, "R1", "C1");
         dataset.add(item2, "R2", "C2");

         Range range = dataset.getRangeBounds(false);
         assertNotNull(range);
         assertEquals("Overall min should be the smallest value across all items", 85.0,
             range.getLowerBound(), 1e-9);
         assertEquals("Overall max should be the largest value across all items", 230.0,
             range.getUpperBound(), 1e-9);
     }

     @Test
     public void testGetMinRegularValue() {
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             50.0, 50.0, 40.0, 60.0,
             38.0, 62.0, 35.0, 65.0, new ArrayList<Double>()
         );
         dataset.add(item, "Row1", "Col1");
         assertEquals(38.0, dataset.getMinRegularValue(0, 0).doubleValue(), 1e-9);
         assertEquals(38.0, dataset.getMinRegularValue("Row1", "Col1").doubleValue(), 1e-9);
     }

     @Test
     public void testGetMaxOutlier() {
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             50.0, 50.0, 40.0, 60.0,
             38.0, 62.0, 35.0, 70.0, new ArrayList<Double>()
         );
         dataset.add(item, "R", "C");
         assertEquals(70.0, dataset.getMaxOutlier(0, 0).doubleValue(), 1e-9);
         assertEquals(70.0, dataset.getMaxOutlier("R", "C").doubleValue(), 1e-9);
     }

     @Test
     public void testUpdateBoundsResetsMinMaxOnOverwrite() {
         BoxAndWhiskerItem oldItem = new BoxAndWhiskerItem(
             10.0, 10.0, 9.0, 11.0,
             8.0, 12.0, 7.0, 13.0, new ArrayList<Double>()
         );
         dataset.add(oldItem, "R", "C");
         assertEquals(7.0, dataset.getRangeLowerBound(false), 1e-9);

         // Overwrite the same cell; new item has different extremes.
         BoxAndWhiskerItem newItem = new BoxAndWhiskerItem(
             20.0, 20.0, 18.0, 22.0,
             17.0, 23.0, 16.0, 24.0, new ArrayList<Double>()
         );
         dataset.add(newItem, "R", "C");

         Range range = dataset.getRangeBounds(false);
         assertNotNull(range);
         assertEquals(16.0, range.getLowerBound(), 1e-9);
         assertEquals(24.0, range.getUpperBound(), 1e-9);
     }

     @Test
     public void testClonePreservesBounds() throws CloneNotSupportedException {
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             30.0, 30.0, 25.0, 35.0,
             20.0, 40.0, 19.0, 41.0, new ArrayList<Double>()
         );
         dataset.add(item, "R", "C");
         DefaultBoxAndWhiskerCategoryDataset clone =
             (DefaultBoxAndWhiskerCategoryDataset) dataset.clone();

         Range originalRange = dataset.getRangeBounds(false);
         Range cloneRange = clone.getRangeBounds(false);
         assertEquals("Cloned dataset must have the same range bounds",
             originalRange.getLowerBound(), cloneRange.getLowerBound(), 1e-9);
         assertEquals(originalRange.getUpperBound(), cloneRange.getUpperBound(), 1e-9);
     }

     @Test
     public void testEquals() {
         BoxAndWhiskerItem item = new BoxAndWhiskerItem(
             1.0, 1.0, 0.5, 1.5,
             0.3, 1.7, 0.2, 1.8, new ArrayList<Double>()
         );
         dataset.add(item, "X", "Y");

         DefaultBoxAndWhiskerCategoryDataset other = new DefaultBoxAndWhiskerCategoryDataset();
         other.add(item, "X", "Y");
         assertEquals(dataset, other);

         other.add(new BoxAndWhiskerItem(
             2.0, 2.0, 1.0, 3.0,
             0.5, 3.5, null, null, new ArrayList<Double>()
         ), "Z", "W");
         assertNotEquals(dataset, other);
     }
 }

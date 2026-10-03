package org.jfree.data.general.junit;

 import static org.junit.Assert.*;

 import org.jfree.data.Range;
 import org.jfree.data.category.CategoryDataset;
 import org.jfree.data.category.DefaultCategoryDataset;
 import org.jfree.data.general.DatasetUtilities;
 import org.jfree.data.xy.DefaultXYDataset;
 import org.jfree.data.xy.XYDataset;
 import org.junit.Test;

 public class DatasetUtilitiesTests {

     @Test
     public void testBug2849731_2() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(null, "R1", "C1");
         dataset.addValue(null, "R1", "C2");
         dataset.addValue(new Double(5.0), "R2", "C1");
         dataset.addValue(null, "R2", "C2");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(5.0, range.getLowerBound(), 0.0);
         assertEquals(5.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testBug2849731_3() {
         DefaultXYDataset dataset = new DefaultXYDataset();
         double[] x = {1.0, 2.0};
         double[] y = {10.0, 20.0};
         dataset.addSeries("S1", new double[][]{x, y});
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(10.0, range.getLowerBound(), 0.0);
         assertEquals(20.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testAllNullCategoryCells() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(null, "R1", "C1");
         dataset.addValue(null, "R1", "C2");
         dataset.addValue(null, "R2", "C1");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNull(range);
     }

     @Test
     public void testMixedNullAndValidCategoryValues() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(null, "R1", "C1");
         dataset.addValue(new Double(1.0), "R1", "C2");
         dataset.addValue(new Double(-3.0), "R2", "C1");
         dataset.addValue(null, "R2", "C2");
         dataset.addValue(new Double(7.0), "R3", "C1");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(-3.0, range.getLowerBound(), 0.0);
         assertEquals(7.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testSingleNonNullCategoryValue() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(new Double(42.0), "R1", "C1");
         dataset.addValue(null, "R1", "C2");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(42.0, range.getLowerBound(), 0.0);
         assertEquals(42.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testNullsAmongPositiveAndNegative() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(new Double(-10.0), "R1", "C1");
         dataset.addValue(null, "R1", "C2");
         dataset.addValue(new Double(0.0), "R2", "C1");
         dataset.addValue(new Double(25.0), "R2", "C2");
         dataset.addValue(null, "R3", "C1");
         dataset.addValue(new Double(-5.0), "R3", "C2");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(-10.0, range.getLowerBound(), 0.0);
         assertEquals(25.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testEmptyCategoryDataset() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNull(range);
     }

     @Test
     public void testSingleNullCell() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(null, "R1", "C1");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNull(range);
     }

     @Test
     public void testXYDatasetSingleSeries() {
         DefaultXYDataset dataset = new DefaultXYDataset();
         double[] x = {0.0, 1.0, 2.0};
         double[] y = {-5.0, 0.0, 8.0};
         dataset.addSeries("S", new double[][]{x, y});
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(-5.0, range.getLowerBound(), 0.0);
         assertEquals(8.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testXYDatasetMultipleSeries() {
         DefaultXYDataset dataset = new DefaultXYDataset();
         double[] x1 = {1.0, 2.0};
         double[] y1 = {3.0, 7.0};
         double[] x2 = {3.0, 4.0};
         double[] y2 = {-2.0, 10.0};
         dataset.addSeries("S1", new double[][]{x1, y1});
         dataset.addSeries("S2", new double[][]{x2, y2});
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(-2.0, range.getLowerBound(), 0.0);
         assertEquals(10.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testFullyNullRowInCategoryDataset() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(null, "R1", "C1");
         dataset.addValue(null, "R1", "C2");
         dataset.addValue(new Double(3.0), "R2", "C1");
         dataset.addValue(new Double(7.0), "R2", "C2");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(3.0, range.getLowerBound(), 0.0);
         assertEquals(7.0, range.getUpperBound(), 0.0);
     }

     @Test
     public void testNullAndValidInSameRow() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(new Double(100.0), "R1", "C1");
         dataset.addValue(null, "R1", "C2");
         dataset.addValue(new Double(200.0), "R1", "C3");
         dataset.addValue(null, "R2", "C1");
         dataset.addValue(null, "R2", "C2");
         Range range = DatasetUtilities.findRangeBounds(dataset);
         assertNotNull(range);
         assertEquals(100.0, range.getLowerBound(), 0.0);
         assertEquals(200.0, range.getUpperBound(), 0.0);
     }
 }
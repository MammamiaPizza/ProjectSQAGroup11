package org.jfree.chart.renderer.category.junit;

 import static org.junit.Assert.fail;

 import java.awt.Graphics2D;
 import java.awt.Rectangle;
 import java.awt.image.BufferedImage;

 import org.jfree.chart.ChartFactory;
 import org.jfree.chart.JFreeChart;
 import org.jfree.chart.axis.CategoryAxis;
 import org.jfree.chart.axis.NumberAxis;
 import org.jfree.chart.plot.CategoryPlot;
 import org.jfree.chart.plot.PlotOrientation;
 import org.jfree.chart.renderer.category.StatisticalBarRenderer;
 import org.jfree.data.category.CategoryDataset;
 import org.jfree.data.general.AbstractDataset;
 import org.jfree.data.statistics.StatisticalCategoryDataset;

 import org.junit.Test;

 /**
  * Tests for {@link StatisticalBarRenderer} to verify correct handling of
  * <code>null</code> mean and standard deviation values in the dataset.
  */
 public class StatisticalBarRendererTests {

     /**
      * Dataset that can hold {@code null} mean and standard deviation values.
      */
     private static class NullableStatisticalCategoryDataset
             extends org.jfree.data.category.AbstractCategoryDataset
             implements StatisticalCategoryDataset {

         private final Number[][] means;
         private final Number[][] stdDevs;
         private final Comparable[] rowKeys;
         private final Comparable[] colKeys;
         private final int rowCount;
         private final int colCount;

         NullableStatisticalCategoryDataset(Comparable[] rowKeys, Comparable[] colKeys,
                                            Number[][] means, Number[][] stdDevs) {
             this.rowKeys = rowKeys;
             this.colKeys = colKeys;
             this.means = means;
             this.stdDevs = stdDevs;
             this.rowCount = rowKeys.length;
             this.colCount = colKeys.length;
         }

         @Override
         public int getRowCount() {
             return this.rowCount;
         }

         @Override
         public int getColumnCount() {
             return this.colCount;
         }

         @Override
         public Comparable getRowKey(int row) {
             return this.rowKeys[row];
         }

         @Override
         public Comparable getColumnKey(int column) {
             return this.colKeys[column];
         }

         @Override
         public int getRowIndex(Comparable key) {
             for (int i = 0; i < this.rowCount; i++) {
                 if (this.rowKeys[i].equals(key)) {
                     return i;
                 }
             }
             return -1;
         }

         @Override
         public int getColumnIndex(Comparable key) {
             for (int i = 0; i < this.colCount; i++) {
                 if (this.colKeys[i].equals(key)) {
                     return i;
                 }
             }
             return -1;
         }

         @Override
         public Number getValue(int row, int column) {
             // fallback when a plain CategoryDataset API is used
             Number mean = getMeanValue(row, column);
             return mean != null ? mean : Double.valueOf(0.0);
         }

         @Override
         public Number getValue(Comparable rowKey, Comparable columnKey) {
             return getValue(getRowIndex(rowKey), getColumnIndex(columnKey));
         }

         @Override
         public Number getMeanValue(int row, int column) {
             return this.means[row][column];
         }

         @Override
         public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
             return getMeanValue(getRowIndex(rowKey), getColumnIndex(columnKey));
         }

         @Override
         public Number getStdDevValue(int row, int column) {
             return this.stdDevs[row][column];
         }

         @Override
         public Number getStdDevValue(Comparable rowKey, Comparable columnKey) {
             return getStdDevValue(getRowIndex(rowKey), getColumnIndex(columnKey));
         }
     }

     /**
      * Helper: create a 1x1 dataset with the given mean and deviation values.
      */
     private static CategoryDataset createDataset(Number mean, Number stdDev) {
         Comparable[] rowKeys = {"R1"};
         Comparable[] colKeys = {"C1"};
         Number[][] means = {{mean}};
         Number[][] stdDevs = {{stdDev}};
         return new NullableStatisticalCategoryDataset(rowKeys, colKeys, means, stdDevs);
     }

     /**
      * Render the chart into a 400x300 image and catch any runtime exception.
      */
     private static void drawChart(CategoryDataset dataset, PlotOrientation orientation) {
         CategoryAxis domainAxis = new CategoryAxis();
         NumberAxis rangeAxis = new NumberAxis();
         StatisticalBarRenderer renderer = new StatisticalBarRenderer();
         CategoryPlot plot = new CategoryPlot(dataset, domainAxis, rangeAxis, renderer);
         plot.setOrientation(orientation);
         JFreeChart chart = new JFreeChart(plot);
         BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2 = img.createGraphics();
         chart.draw(g2, new Rectangle(400, 300));
         g2.dispose();
     }

     // ---- Null-mean cases ----

     @Test
     public void testDrawWithNullMeanVertical() {
         CategoryDataset data = createDataset(null, 1.0);
         try {
             drawChart(data, PlotOrientation.VERTICAL);
         } catch (RuntimeException e) {
             fail("Drawing with null mean (vertical) must not throw: " + e);
         }
     }

     @Test
     public void testDrawWithNullMeanHorizontal() {
         CategoryDataset data = createDataset(null, 1.0);
         try {
             drawChart(data, PlotOrientation.HORIZONTAL);
         } catch (RuntimeException e) {
             fail("Drawing with null mean (horizontal) must not throw: " + e);
         }
     }

     // ---- Null standard-deviation cases ----

     @Test
     public void testDrawWithNullDeviationVertical() {
         CategoryDataset data = createDataset(5.0, null);
         try {
             drawChart(data, PlotOrientation.VERTICAL);
         } catch (RuntimeException e) {
             fail("Drawing with null deviation (vertical) must not throw: " + e);
         }
     }

     @Test
     public void testDrawWithNullDeviationHorizontal() {
         CategoryDataset data = createDataset(5.0, null);
         try {
             drawChart(data, PlotOrientation.HORIZONTAL);
         } catch (RuntimeException e) {
             fail("Drawing with null deviation (horizontal) must not throw: " + e);
         }
     }

     // ---- Both null ----

     @Test
     public void testDrawWithBothNullVertical() {
         CategoryDataset data = createDataset(null, null);
         try {
             drawChart(data, PlotOrientation.VERTICAL);
         } catch (RuntimeException e) {
             fail("Drawing with both null (vertical) must not throw: " + e);
         }
     }

     @Test
     public void testDrawWithBothNullHorizontal() {
         CategoryDataset data = createDataset(null, null);
         try {
             drawChart(data, PlotOrientation.HORIZONTAL);
         } catch (RuntimeException e) {
             fail("Drawing with both null (horizontal) must not throw: " + e);
         }
     }

     // ---- Normal (non-null) values ----

     @Test
     public void testDrawNormalValuesVertical() {
         CategoryDataset data = createDataset(5.0, 1.0);
         try {
             drawChart(data, PlotOrientation.VERTICAL);
         } catch (RuntimeException e) {
             fail("Drawing normal values (vertical) must not throw: " + e);
         }
     }

     @Test
     public void testDrawNormalValuesHorizontal() {
         CategoryDataset data = createDataset(5.0, 1.0);
         try {
             drawChart(data, PlotOrientation.HORIZONTAL);
         } catch (RuntimeException e) {
             fail("Drawing normal values (horizontal) must not throw: " + e);
         }
     }
 }

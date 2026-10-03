package org.jfree.chart.plot.junit;

 import static org.junit.Assert.*;

 import java.awt.Graphics2D;
 import java.awt.geom.Rectangle2D;
 import java.awt.image.BufferedImage;

 import org.jfree.chart.JFreeChart;
 import org.jfree.chart.plot.PiePlot;
 import org.jfree.data.general.DefaultPieDataset;
 import org.junit.Test;

 /**
  * Tests for PiePlot focusing on null-dataset handling and related edge cases
  * exposed by Defects4J Chart bug 15.
  */
 public class PiePlotTests {

     private static final Rectangle2D PLOT_AREA = new Rectangle2D.Double(0, 0, 200, 200);

     private BufferedImage createImage() {
         return new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
     }

     private void assertDrawDoesNotThrow(PiePlot plot) {
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             JFreeChart chart = new JFreeChart(plot);
             chart.draw(g2, PLOT_AREA, null, null);
         } catch (Exception e) {
             fail("Drawing threw exception: " + e);
         } finally {
             g2.dispose();
         }
     }

     @Test
     public void testDrawWithNullDataset() {
         PiePlot plot = new PiePlot(); // dataset is null by default
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDrawWithEmptyDataset() {
         PiePlot plot = new PiePlot(new DefaultPieDataset());
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDrawWithNullValueInDataset() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("A", null);
         PiePlot plot = new PiePlot(dataset);
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDrawWithZeroValueIgnored() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("A", 0.0);
         PiePlot plot = new PiePlot(dataset);
         plot.setIgnoreZeroValues(true);
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDrawWithZeroValueNotIgnored() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("A", 0.0);
         PiePlot plot = new PiePlot(dataset);
         plot.setIgnoreZeroValues(false);
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDefaultIgnoreNullValues() {
         PiePlot plot = new PiePlot();
         assertFalse(plot.getIgnoreNullValues());
     }

     @Test
     public void testSetIgnoreNullValues() {
         PiePlot plot = new PiePlot();
         plot.setIgnoreNullValues(true);
         assertTrue(plot.getIgnoreNullValues());
         plot.setIgnoreNullValues(false);
         assertFalse(plot.getIgnoreNullValues());
     }

     @Test
     public void testConstructorWithDataset() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("X", 10.0);
         PiePlot plot = new PiePlot(dataset);
         assertEquals(dataset, plot.getDataset());
     }

     @Test
     public void testConstructorNoArg() {
         PiePlot plot = new PiePlot();
         assertNull(plot.getDataset());
     }

     @Test
     public void testSetDatasetNull() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("A", 5.0);
         PiePlot plot = new PiePlot(dataset);
         plot.setDataset(null);
         assertNull(plot.getDataset());
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDrawWithSingleValue() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("S", 100.0);
         PiePlot plot = new PiePlot(dataset);
         assertDrawDoesNotThrow(plot);
     }

     @Test
     public void testDrawWithMultipleValues() {
         DefaultPieDataset dataset = new DefaultPieDataset();
         dataset.setValue("A", 10.0);
         dataset.setValue("B", 20.0);
         dataset.setValue("C", 30.0);
         PiePlot plot = new PiePlot(dataset);
         assertDrawDoesNotThrow(plot);
     }

@Test
 public void testCloneWithURLGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setURLGenerator(new org.jfree.chart.urls.StandardPieURLGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getURLGenerator());
 }

 @Test
 public void testCloneWithLegendLabelGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setLegendLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getLegendLabelGenerator());
 }

 @Test
 public void testCloneWithLegendLabelToolTipGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setLegendLabelToolTipGenerator(new
org.jfree.chart.labels.StandardPieSectionLabelGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getLegendLabelToolTipGenerator());
 }

 @Test
 public void testCloneWithLegendLabelURLGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setLegendLabelURLGenerator(new org.jfree.chart.urls.StandardPieURLGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getLegendLabelURLGenerator());
 }
}

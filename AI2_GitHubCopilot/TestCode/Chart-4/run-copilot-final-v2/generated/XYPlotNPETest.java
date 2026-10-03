package org.jfree.chart.plot;

 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.awt.Graphics2D;
 import java.awt.geom.Rectangle2D;
 import java.awt.image.BufferedImage;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;

 import org.jfree.chart.ChartFactory;
 import org.jfree.chart.JFreeChart;
 import org.jfree.chart.axis.NumberAxis;
 import org.jfree.chart.axis.ValueAxis;
 import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
 import org.jfree.chart.renderer.xy.XYItemRenderer;
 import org.jfree.data.xy.XYDataset;
 import org.jfree.data.xy.XYSeriesCollection;
 import org.junit.Test;

 public class XYPlotNPETest {

     private static final Rectangle2D DEFAULT_AREA = new Rectangle2D.Double(0, 0, 300, 200);

     /**
      * After setting the dataset to null, the plot should still return its
      * previously assigned range axis, without throwing a NullPointerException.
      */
     @Test
     public void testGetRangeAxisAfterNullDataset() {
         XYDataset dataset = new XYSeriesCollection();
         ValueAxis domainAxis = new NumberAxis("X");
         ValueAxis rangeAxis = new NumberAxis("Y");
         XYItemRenderer renderer = new StandardXYItemRenderer();
         XYPlot plot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
         plot.setDataset(null);
         assertNotNull("Range axis should remain accessible after dataset is set to null",
                 plot.getRangeAxis());
     }

     /**
      * Drawing a plot with a null dataset must not throw an exception.
      */
     @Test
     public void testDrawWithNullDataset() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.setDataset(null);
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing with a null dataset threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Setting the range axis to null must not cause subsequent drawing to
      * fail with an exception.
      */
     @Test
     public void testDrawWithNullRangeAxis() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.setRangeAxis(null);
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing with a null range axis threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Setting the domain axis to null must not cause subsequent drawing to
      * throw an exception.
      */
     @Test
     public void testDrawWithNullDomainAxis() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.setDomainAxis(null);
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing with a null domain axis threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Serializing and deserializing an XYPlot must not destroy internal state;
      * axis getters and drawing must still work afterwards.
      */
     @Test
     public void testSerializationDoesNotCorruptPlot() throws Exception {
         XYDataset dataset = new XYSeriesCollection();
         ValueAxis domainAxis = new NumberAxis("X");
         ValueAxis rangeAxis = new NumberAxis("Y");
         XYItemRenderer renderer = new StandardXYItemRenderer();
         XYPlot original = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

         // Serialize
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos);
         oos.writeObject(original);
         oos.close();

         // Deserialize
         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bis);
         XYPlot deserialized = (XYPlot) ois.readObject();
         ois.close();

         // Axis getters must not throw NPE
         assertNotNull("Deserialized plot should have a domain axis",
                 deserialized.getDomainAxis());
         assertNotNull("Deserialized plot should have a range axis",
                 deserialized.getRangeAxis());

         // Drawing must not throw NPE
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             deserialized.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing after deserialization threw a NullPointerException");
         } finally {
             g2.dispose();
         }

         // Equals must not throw NPE
         assertTrue("Deserialized plot should equal the original",
                 original.equals(deserialized));
     }

     /**
      * Replacing the dataset with a fresh instance must not cause drawing
      * (even with null render info) to fail.
      */
     @Test
     public void testReplaceDatasetAndDrawWithNullInfo() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.setDataset(new XYSeriesCollection());
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing with replaced dataset and null info threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Drawing a fully populated plot with a null PlotRenderingInfo must
      * succeed without an exception.
      */
     @Test
     public void testDrawWithNullPlotRenderingInfo() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing with null PlotRenderingInfo threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Clearing all range axes and then drawing must not throw an exception.
      */
     @Test
     public void testDrawAfterClearingRangeAxes() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.clearRangeAxes();
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing after clearing range axes threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Clearing all domain axes and then drawing must not throw an exception.
      */
     @Test
     public void testDrawAfterClearingDomainAxes() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.clearDomainAxes();
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             plot.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing after clearing domain axes threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * Cloning an XYPlot and then drawing the clone must not throw an exception.
      */
     @Test
     public void testCloneThenDraw() throws CloneNotSupportedException {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         XYPlot clone = (XYPlot) plot.clone();
         BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
         Graphics2D g2 = image.createGraphics();
         try {
             clone.draw(g2, DEFAULT_AREA, null, null, null);
         } catch (NullPointerException e) {
             fail("Drawing cloned plot threw a NullPointerException");
         } finally {
             g2.dispose();
         }
     }

     /**
      * After setting the dataset to null, configureRangeAxes must not throw.
      */
     @Test
     public void testConfigureRangeAxesAfterNullDataset() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.setDataset(null);
         try {
             plot.configureRangeAxes();
         } catch (NullPointerException e) {
             fail("configureRangeAxes threw NullPointerException after dataset was set to null");
         }
     }

     /**
      * After setting the dataset to null, configureDomainAxes must not throw.
      */
     @Test
     public void testConfigureDomainAxesAfterNullDataset() {
         XYPlot plot = new XYPlot(new XYSeriesCollection(), new NumberAxis("X"),
                 new NumberAxis("Y"), new StandardXYItemRenderer());
         plot.setDataset(null);
         try {
             plot.configureDomainAxes();
         } catch (NullPointerException e) {
             fail("configureDomainAxes threw NullPointerException after dataset was set to null");
         }
     }
 }

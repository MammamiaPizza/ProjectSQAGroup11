package org.jfree.chart.plot.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;

public class XYPlotFaultTest {

 private XYDataset createDataset() {
     XYSeries series = new XYSeries("Series 1");
     series.add(1.0, 10.0);
     series.add(2.0, 20.0);
     series.add(3.0, 15.0);
     return new XYSeriesCollection(series);
 }

 @Test
 public void testDefaultPlotReturnsNullForOptionalComponents() {
     XYPlot plot = new XYPlot();
     assertNull(plot.getDataset());
     assertNull(plot.getDataset(0));
     assertNull(plot.getDomainAxis());
     assertNull(plot.getRangeAxis());
 }

 @Test
 public void testSetDatasetNull() {
     XYPlot plot = new XYPlot();
     plot.setDataset((XYDataset) null);
     assertNull(plot.getDataset());
 }

 @Test
 public void testReplaceDatasetWithNull() {
     XYPlot plot = new XYPlot();
     plot.setDataset(createDataset());
     assertNotNull(plot.getDataset());

     plot.setDataset(0, null);
     assertNull(plot.getDataset());
     assertNull(plot.getDataset(0));
 }

 @Test
 public void testClearDomainAxes() {
     XYPlot plot = new XYPlot();
     plot.setDomainAxis(new NumberAxis("X"));
     assertNotNull(plot.getDomainAxis());

     plot.clearDomainAxes();
     assertNull(plot.getDomainAxis());
     assertTrue(plot.getDomainAxisCount() == 0);
 }

 @Test
 public void testClearRangeAxes() {
     XYPlot plot = new XYPlot();
     plot.setRangeAxis(new NumberAxis("Y"));
     assertNotNull(plot.getRangeAxis());

     plot.clearRangeAxes();
     assertNull(plot.getRangeAxis());
     assertTrue(plot.getRangeAxisCount() == 0);
 }

 @Test
 public void testAutoRangeWithNullRendererDoesNotThrow() {
     NumberAxis domain = new NumberAxis("X");
     NumberAxis range = new NumberAxis("Y");
     XYPlot plot = new XYPlot(createDataset(), domain, range, null);

     plot.configureDomainAxes();
     plot.configureRangeAxes();

     assertFalse(Double.isNaN(range.getLowerBound()));
     assertFalse(Double.isInfinite(range.getLowerBound()));
     assertFalse(Double.isNaN(range.getUpperBound()));
     assertFalse(Double.isInfinite(range.getUpperBound()));
 }

 @Test
 public void testAutoRangeWithRendererUsesDatasetBounds() {
     NumberAxis domain = new NumberAxis("X");
     NumberAxis range = new NumberAxis("Y");
     XYItemRenderer renderer = new XYLineAndShapeRenderer();
     XYPlot plot = new XYPlot(createDataset(), domain, range, renderer);

     plot.configureRangeAxes();

     assertTrue(range.getUpperBound() >= 20.0);
     assertTrue(range.getLowerBound() <= 10.0);
     assertFalse(Double.isNaN(range.getUpperBound()));
     assertFalse(Double.isInfinite(range.getUpperBound()));
 }

 @Test
 public void testDrawWithNullInfoDoesNotThrow() {
     JFreeChart chart = ChartFactory.createScatterPlot(
             "Scatter", "X", "Y", createDataset(),
             PlotOrientation.VERTICAL, true, false, false);

     BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2 = image.createGraphics();
     boolean drawn = false;
     try {
         chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 150), null);
         drawn = true;
     } finally {
         g2.dispose();
     }
     assertTrue(drawn);
 }

 @Test
 public void testSetSeriesToolTipGeneratorAndDraw() {
     JFreeChart chart = ChartFactory.createScatterPlot(
             "Scatter", "X", "Y", createDataset(),
             PlotOrientation.VERTICAL, true, false, false);
     XYPlot plot = (XYPlot) chart.getPlot();
     plot.getRenderer().setSeriesToolTipGenerator(0, new StandardXYToolTipGenerator());

     BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2 = image.createGraphics();
     boolean drawn = false;
     try {
         chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 150), null);
         drawn = true;
     } finally {
         g2.dispose();
     }
     assertTrue(drawn);
 }

 @Test
 public void testSerializationRoundTrip() throws Exception {
     NumberAxis domain = new NumberAxis("X");
     NumberAxis range = new NumberAxis("Y");
     XYItemRenderer renderer = new XYLineAndShapeRenderer();
     XYPlot plot = new XYPlot(createDataset(), domain, range, renderer);

     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
     ObjectOutputStream out = new ObjectOutputStream(buffer);
     out.writeObject(plot);
     out.close();

     ByteArrayInputStream bin = new ByteArrayInputStream(buffer.toByteArray());
     ObjectInputStream in = new ObjectInputStream(bin);
     XYPlot restored = (XYPlot) in.readObject();
     in.close();

     assertNotNull(restored);
     assertNotNull(restored.getDataset());
     assertNotNull(restored.getDomainAxis());
     assertNotNull(restored.getRangeAxis());
 }

}
package org.jfree.chart.renderer.category.junit;

import java.awt.Rectangle;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AbstractCategoryItemRendererTests {

 private AbstractCategoryItemRenderer createRenderer() {
     return new BarRenderer();
 }

 private DefaultCategoryDataset singleCellDataset() {
     DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     dataset.addValue(1.0, "R1", "C1");
     return dataset;
 }

 private CategoryItemRendererState renderPass(AbstractCategoryItemRenderer r,
         CategoryDataset dataset) {
     return r.initialise(null, new Rectangle(0, 0, 100, 100),
             new CategoryPlot(), dataset, null);
 }

 @Test
 public void testRowColumnCountAfterRenderSingleCell() {
     AbstractCategoryItemRenderer r = createRenderer();
     DefaultCategoryDataset dataset = singleCellDataset();
     renderPass(r, dataset);
     assertEquals(1, r.getRowCount());
     assertEquals(1, r.getColumnCount());
 }

 @Test
 public void testRowColumnCountMultipleRowsColumns() {
     AbstractCategoryItemRenderer r = createRenderer();
     DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     dataset.addValue(1.0, "R1", "C1");
     dataset.addValue(2.0, "R2", "C1");
     dataset.addValue(3.0, "R1", "C2");
     dataset.addValue(4.0, "R2", "C2");
     renderPass(r, dataset);
     assertEquals(2, r.getRowCount());
     assertEquals(2, r.getColumnCount());
 }

 @Test
 public void testRowColumnCountEmptyDataset() {
     AbstractCategoryItemRenderer r = createRenderer();
     DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     renderPass(r, dataset);
     assertEquals(0, r.getRowCount());
     assertEquals(0, r.getColumnCount());
 }

 @Test
 public void testRowColumnCountNullDataset() {
     AbstractCategoryItemRenderer r = createRenderer();
     renderPass(r, null);
     assertEquals(0, r.getRowCount());
     assertEquals(0, r.getColumnCount());
 }

 @Test
 public void testInitialiseReturnsState() {
     AbstractCategoryItemRenderer r = createRenderer();
     CategoryItemRendererState state = renderPass(r, singleCellDataset());
     assertNotNull(state);
 }

 @Test
 public void testInitialiseSetsPlot() {
     AbstractCategoryItemRenderer r = createRenderer();
     CategoryPlot plot = new CategoryPlot();
     r.initialise(null, new Rectangle(0, 0, 100, 100), plot,
             singleCellDataset(), null);
     assertEquals(plot, r.getPlot());
 }

 @Test
 public void testRowColumnCountUnchangedBeforeRender() {
     AbstractCategoryItemRenderer r = createRenderer();
     assertEquals(0, r.getRowCount());
     assertEquals(0, r.getColumnCount());
 }

 @Test
 public void testRowColumnCountViaChartRender() {
     AbstractCategoryItemRenderer r = createRenderer();
     DefaultCategoryDataset dataset = singleCellDataset();
     CategoryPlot plot = new CategoryPlot(dataset, null, null, r);
     JFreeChart chart = new JFreeChart(plot);
     chart.createBufferedImage(200, 200, new ChartRenderingInfo());
     assertEquals(1, r.getRowCount());
     assertEquals(1, r.getColumnCount());
 }

}
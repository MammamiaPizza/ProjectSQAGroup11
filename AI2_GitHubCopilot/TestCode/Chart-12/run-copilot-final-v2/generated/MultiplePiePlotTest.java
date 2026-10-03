import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.Color;
import java.awt.Paint;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.util.TableOrder;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class MultiplePiePlotTest {

 @Test
 public void testDefaultConstructorDefaults() {
     MultiplePiePlot plot = new MultiplePiePlot();
     assertEquals("aggregatedItemsPaint", Color.lightGray, plot.getAggregatedItemsPaint());
     assertEquals("aggregatedItemsKey", "Other", plot.getAggregatedItemsKey());
     assertEquals("dataExtractOrder", TableOrder.BY_COLUMN, plot.getDataExtractOrder());
     assertEquals("limit", 0.0, plot.getLimit(), 0.0);
 }

 @Test
 public void testConstructorWithNullDatasetSameAsNoArg() {
     MultiplePiePlot plot = new MultiplePiePlot(null);
     assertEquals("aggregatedItemsPaint", Color.lightGray, plot.getAggregatedItemsPaint());
     assertEquals("aggregatedItemsKey", "Other", plot.getAggregatedItemsKey());
     assertEquals("dataExtractOrder", TableOrder.BY_COLUMN, plot.getDataExtractOrder());
     assertEquals("limit", 0.0, plot.getLimit(), 0.0);
     assertNull("dataset should be null", plot.getDataset());
 }

 @Test
 public void testConstructorWithDataset() {
     CategoryDataset dataset = new DefaultCategoryDataset();
     MultiplePiePlot plot = new MultiplePiePlot(dataset);
     assertSame("dataset", dataset, plot.getDataset());
 }

 @Test
 public void testGetPlotType() {
     MultiplePiePlot plot = new MultiplePiePlot();
     assertEquals("plot type", "Multiple Pie Plot", plot.getPlotType());
 }

 @Test
 public void testSetAndGetAggregatedItemsPaint() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setAggregatedItemsPaint(Color.black);
     assertEquals(Color.black, plot.getAggregatedItemsPaint());
     plot.setAggregatedItemsPaint(Color.lightGray);
     assertEquals(Color.lightGray, plot.getAggregatedItemsPaint());
 }

 @Test(expected = IllegalArgumentException.class)
 public void testSetAggregatedItemsPaintNull() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setAggregatedItemsPaint(null);
 }

 @Test
 public void testSetAndGetAggregatedItemsKey() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setAggregatedItemsKey("TestKey");
     assertEquals("TestKey", plot.getAggregatedItemsKey());
     plot.setAggregatedItemsKey("Other");
     assertEquals("Other", plot.getAggregatedItemsKey());
 }

 @Test(expected = IllegalArgumentException.class)
 public void testSetAggregatedItemsKeyNull() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setAggregatedItemsKey(null);
 }

 @Test
 public void testSetAndGetLimit() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setLimit(0.0);
     assertEquals(0.0, plot.getLimit(), 0.0);
     plot.setLimit(-0.5);
     assertEquals(-0.5, plot.getLimit(), 0.0);
     plot.setLimit(1.0);
     assertEquals(1.0, plot.getLimit(), 0.0);
 }

 @Test
 public void testSetAndGetDataExtractOrder() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setDataExtractOrder(TableOrder.BY_ROW);
     assertEquals(TableOrder.BY_ROW, plot.getDataExtractOrder());
     plot.setDataExtractOrder(TableOrder.BY_COLUMN);
     assertEquals(TableOrder.BY_COLUMN, plot.getDataExtractOrder());
 }

 @Test(expected = IllegalArgumentException.class)
 public void testSetDataExtractOrderNull() {
     MultiplePiePlot plot = new MultiplePiePlot();
     plot.setDataExtractOrder(null);
 }

 @Test
 public void testEqualsReflexiveAndSymmetric() {
     MultiplePiePlot plot1 = new MultiplePiePlot();
     MultiplePiePlot plot2 = new MultiplePiePlot();
     assertTrue("reflexive", plot1.equals(plot1));
     assertTrue("symmetric", plot1.equals(plot2) && plot2.equals(plot1));
 }

 @Test
 public void testEqualsDifferences() {
     MultiplePiePlot plot1 = new MultiplePiePlot();
     MultiplePiePlot plot2 = new MultiplePiePlot();
     plot2.setAggregatedItemsPaint(Color.black);
     assertFalse("different aggregatedItemsPaint", plot1.equals(plot2));
     plot2.setAggregatedItemsPaint(Color.lightGray);
     plot2.setAggregatedItemsKey("OtherKey");
     assertFalse("different aggregatedItemsKey", plot1.equals(plot2));
     plot2.setAggregatedItemsKey("Other");
     plot2.setLimit(1.0);
     assertFalse("different limit", plot1.equals(plot2));
     plot2.setLimit(0.0);
     plot2.setDataExtractOrder(TableOrder.BY_ROW);
     assertFalse("different dataExtractOrder", plot1.equals(plot2));
 }

 @Test
 public void testGetLegendItemsWithNullDataset() {
     MultiplePiePlot plot = new MultiplePiePlot();
     // dataset is null, should not throw and return a non-null LegendItemCollection
     assertNotNull("legend items should not be null", plot.getLegendItems());
 }

}

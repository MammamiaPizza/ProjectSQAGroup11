import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CategoryPlotAxisIndexTest {

 @Test
 public void testRangeAxisIndexAtZero() {
     CategoryPlot plot = new CategoryPlot();
     NumberAxis axis = new NumberAxis("Range 0");
     plot.setRangeAxis(0, axis);
     assertEquals(0, plot.getRangeAxisIndex(axis));
 }

 @Test
 public void testRangeAxisIndexAtOneAfterTwoAdds() {
     CategoryPlot plot = new CategoryPlot();
     NumberAxis axis0 = new NumberAxis("Range 0");
     NumberAxis axis1 = new NumberAxis("Range 1");
     plot.setRangeAxis(0, axis0);
     plot.setRangeAxis(1, axis1);
     assertEquals(0, plot.getRangeAxisIndex(axis0));
     assertEquals(1, plot.getRangeAxisIndex(axis1));
 }

 @Test
 public void testRangeAxisIndexAfterRemoveAndReAdd() {
     CategoryPlot plot = new CategoryPlot();
     NumberAxis axis = new NumberAxis("Range");
     plot.setRangeAxis(0, axis);
     assertEquals(0, plot.getRangeAxisIndex(axis));

     plot.setRangeAxis(0, null);
     assertEquals(-1, plot.getRangeAxisIndex(axis));

     plot.setRangeAxis(1, axis);
     assertEquals(1, plot.getRangeAxisIndex(axis));
 }

 @Test
 public void testRangeAxisIndexNeverAdded() {
     CategoryPlot plot = new CategoryPlot();
     NumberAxis axis = new NumberAxis("Missing Range");
     assertEquals(-1, plot.getRangeAxisIndex(axis));
 }

 @Test
 public void testRangeAxisIndexNullArgument() {
     CategoryPlot plot = new CategoryPlot();
     try {
         plot.getRangeAxisIndex(null);
         fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 @Test
 public void testRangeAxisIndexFromAnotherPlot() {
     CategoryPlot plot = new CategoryPlot();
     CategoryPlot other = new CategoryPlot();
     NumberAxis axis = new NumberAxis("Other Range");
     other.setRangeAxis(0, axis);
     assertEquals(-1, plot.getRangeAxisIndex(axis));
 }

 @Test
 public void testDomainAxisIndexAtZero() {
     CategoryPlot plot = new CategoryPlot();
     CategoryAxis axis = new CategoryAxis("Domain 0");
     plot.setDomainAxis(0, axis);
     assertEquals(0, plot.getDomainAxisIndex(axis));
 }

 @Test
 public void testDomainAxisIndexAtOneAfterTwoAdds() {
     CategoryPlot plot = new CategoryPlot();
     CategoryAxis axis0 = new CategoryAxis("Domain 0");
     CategoryAxis axis1 = new CategoryAxis("Domain 1");
     plot.setDomainAxis(0, axis0);
     plot.setDomainAxis(1, axis1);
     assertEquals(0, plot.getDomainAxisIndex(axis0));
     assertEquals(1, plot.getDomainAxisIndex(axis1));
 }

 @Test
 public void testDomainAxisIndexAfterRemoveAndReAdd() {
     CategoryPlot plot = new CategoryPlot();
     CategoryAxis axis = new CategoryAxis("Domain");
     plot.setDomainAxis(0, axis);
     assertEquals(0, plot.getDomainAxisIndex(axis));

     plot.setDomainAxis(0, null);
     assertEquals(-1, plot.getDomainAxisIndex(axis));

     plot.setDomainAxis(1, axis);
     assertEquals(1, plot.getDomainAxisIndex(axis));
 }

 @Test
 public void testDomainAxisIndexNeverAdded() {
     CategoryPlot plot = new CategoryPlot();
     CategoryAxis axis = new CategoryAxis("Missing Domain");
     assertEquals(-1, plot.getDomainAxisIndex(axis));
 }

 @Test
 public void testDomainAxisIndexNullArgument() {
     CategoryPlot plot = new CategoryPlot();
     try {
         plot.getDomainAxisIndex(null);
         fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

 @Test
 public void testDomainAxisIndexFromAnotherPlot() {
     CategoryPlot plot = new CategoryPlot();
     CategoryPlot other = new CategoryPlot();
     CategoryAxis axis = new CategoryAxis("Other Domain");
     other.setDomainAxis(0, axis);
     assertEquals(-1, plot.getDomainAxisIndex(axis));
 }

}

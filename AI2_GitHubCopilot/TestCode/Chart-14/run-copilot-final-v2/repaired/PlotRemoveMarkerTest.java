import static org.junit.Assert.*;

 import org.jfree.chart.plot.CategoryPlot;
 import org.jfree.chart.plot.Marker;
 import org.jfree.chart.plot.ValueMarker;
 import org.jfree.chart.plot.XYPlot;
 import org.junit.Test;

 public class PlotRemoveMarkerTest {

     @Test
     public void testCategoryPlotRemoveDomainMarkerFromFreshPlot() {
         CategoryPlot plot = new CategoryPlot();
         try {
             plot.removeDomainMarker(new ValueMarker(1.0));
         } catch (NullPointerException e) {
             fail("Should not throw NullPointerException when removing a domain marker from a fresh
plot.");
         } catch (Exception e) {
             // any other unexpected exception
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testCategoryPlotRemoveRangeMarkerFromFreshPlot() {
         CategoryPlot plot = new CategoryPlot();
         try {
             plot.removeRangeMarker(new ValueMarker(1.0));
         } catch (NullPointerException e) {
             fail("Should not throw NullPointerException when removing a range marker from a fresh
plot.");
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testXYPlotRemoveDomainMarkerFromFreshPlot() {
         XYPlot plot = new XYPlot();
         try {
             plot.removeDomainMarker(new ValueMarker(1.0));
         } catch (NullPointerException e) {
             fail("Should not throw NullPointerException when removing a domain marker from a fresh
XYPlot.");
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testXYPlotRemoveRangeMarkerFromFreshPlot() {
         XYPlot plot = new XYPlot();
         try {
             plot.removeRangeMarker(new ValueMarker(1.0));
         } catch (NullPointerException e) {
             fail("Should not throw NullPointerException when removing a range marker from a fresh
XYPlot.");
         } catch (Exception e) {
             fail("Unexpected exception: " + e);
         }
     }

     @Test
     public void testCategoryPlotRemoveDomainMarkerAfterAddThenRemove() {
         CategoryPlot plot = new CategoryPlot();
         ValueMarker marker = new ValueMarker(5.0);
         plot.addDomainMarker(marker, Marker.Layer.FOREGROUND);
         try {
             plot.removeDomainMarker(marker);
         } catch (Exception e) {
             fail("Should not throw when removing an added domain marker: " + e);
         }
     }

     @Test
     public void testCategoryPlotRemoveRangeMarkerAfterAddThenRemove() {
         CategoryPlot plot = new CategoryPlot();
         ValueMarker marker = new ValueMarker(10.0);
         plot.addRangeMarker(marker, Marker.Layer.BACKGROUND);
         try {
             plot.removeRangeMarker(marker);
         } catch (Exception e) {
             fail("Should not throw when removing an added range marker: " + e);
         }
     }

     @Test
     public void testXYPlotRemoveDomainMarkerAfterAddThenRemove() {
         XYPlot plot = new XYPlot();
         ValueMarker marker = new ValueMarker(7.0);
         plot.addDomainMarker(marker, Marker.Layer.FOREGROUND);
         try {
             plot.removeDomainMarker(marker);
         } catch (Exception e) {
             fail("Should not throw when removing an added domain marker from XYPlot: " + e);
         }
     }

     @Test
     public void testXYPlotRemoveRangeMarkerAfterAddThenRemove() {
         XYPlot plot = new XYPlot();
         ValueMarker marker = new ValueMarker(3.0);
         plot.addRangeMarker(marker, Marker.Layer.BACKGROUND);
         try {
             plot.removeRangeMarker(marker);
         } catch (Exception e) {
             fail("Should not throw when removing an added range marker from XYPlot: " + e);
         }
     }

     @Test
     public void testCategoryPlotRemoveDomainMarkerTwice() {
         CategoryPlot plot = new CategoryPlot();
         ValueMarker marker = new ValueMarker(0.0);
         plot.addDomainMarker(marker, Marker.Layer.FOREGROUND);
         try {
             plot.removeDomainMarker(marker);
             // removing a second time should be safe (marker already removed)
             plot.removeDomainMarker(marker);
         } catch (Exception e) {
             fail("Should not throw when removing the same domain marker twice: " + e);
         }
     }

     @Test
     public void testCategoryPlotRemoveRangeMarkerTwice() {
         CategoryPlot plot = new CategoryPlot();
         ValueMarker marker = new ValueMarker(-1.0);
         plot.addRangeMarker(marker, Marker.Layer.BACKGROUND);
         try {
             plot.removeRangeMarker(marker);
             plot.removeRangeMarker(marker);
         } catch (Exception e) {
             fail("Should not throw when removing the same range marker twice: " + e);
         }
     }

     @Test
     public void testRemoveNonexistentMarker() {
         XYPlot plot = new XYPlot();
         ValueMarker present = new ValueMarker(100.0);
         plot.addDomainMarker(present, Marker.Layer.FOREGROUND);
         ValueMarker absent = new ValueMarker(200.0);
         try {
             // remove a different marker that was never added – should be safe
             plot.removeDomainMarker(absent);
         } catch (Exception e) {
             fail("Should not throw when removing a nonexistent marker: " + e);
         }
     }

     @Test
     public void testRemoveMarkerMixedLayers() {
         CategoryPlot plot = new CategoryPlot();
         ValueMarker dmFg = new ValueMarker(1.0);
         ValueMarker dmBg = new ValueMarker(2.0);
         ValueMarker rmFg = new ValueMarker(3.0);
         ValueMarker rmBg = new ValueMarker(4.0);

         plot.addDomainMarker(dmFg, Marker.Layer.FOREGROUND);
         plot.addDomainMarker(dmBg, Marker.Layer.BACKGROUND);
         plot.addRangeMarker(rmFg, Marker.Layer.FOREGROUND);
         plot.addRangeMarker(rmBg, Marker.Layer.BACKGROUND);

         try {
             plot.removeDomainMarker(dmFg);
             plot.removeDomainMarker(dmBg);
             plot.removeRangeMarker(rmFg);
             plot.removeRangeMarker(rmBg);
         } catch (Exception e) {
             fail("Should not throw when removing markers across mixed layers: " + e);
         }
     }

 }

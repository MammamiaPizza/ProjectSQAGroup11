package org.jfree.chart.axis.junit;

 import static org.junit.Assert.*;

 import java.awt.Graphics2D;
 import java.awt.geom.Rectangle2D;
 import java.awt.image.BufferedImage;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;

 import org.jfree.chart.ChartFactory;
 import org.jfree.chart.ChartRenderingInfo;
 import org.jfree.chart.JFreeChart;
 import org.jfree.chart.axis.Axis;
 import org.jfree.chart.axis.AxisState;
 import org.jfree.chart.data.category.DefaultCategoryDataset;
 import org.jfree.chart.entity.EntityCollection;
 import org.jfree.chart.entity.StandardEntityCollection;
 import org.jfree.chart.plot.PlotOrientation;
 import org.jfree.chart.plot.PlotRenderingInfo;
 import org.jfree.chart.util.RectangleEdge;
 import org.junit.Test;

 /**
  * Tests for Axis drawing with null or partially-null ChartRenderingInfo.
  * The bug: drawing a chart (and its axes) with a null ChartRenderingInfo
  * threw an exception, because Axis.drawLabel didn't guard against a null
  * owner inside a non-null PlotRenderingInfo.
  */
 public class AxisDrawNullInfoTests {
     /**
      * A minimal concrete Axis used to expose the protected drawLabel method
      * for direct testing.
      */
     private static class TestAxis extends Axis {
         public TestAxis() {
             super("Test");
         }

         @Override
         public void configure() {
         }

         @Override
         public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea,
                 Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
             return new AxisState();
         }

         public AxisState testDrawLabel(String label, Graphics2D g2,
                 Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge,
                 AxisState state, PlotRenderingInfo plotState) {
             return drawLabel(label, g2, plotArea, dataArea, edge, state, plotState);
         }
     }

     private static BufferedImage createImage() {
         return new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
     }

     private static Rectangle2D createPlotArea() {
         return new Rectangle2D.Double(0, 0, 200, 100);
     }

     private static Rectangle2D createDataArea() {
         return new Rectangle2D.Double(20, 10, 160, 80);
     }

     // 1. Integration: draw a whole chart with null info - should not throw
     @Test
     public void testChartDrawWithNullInfo() {
         DefaultCategoryDataset dataset = new DefaultCategoryDataset();
         dataset.addValue(1.0, "S1", "C1");
         JFreeChart chart = ChartFactory.createBarChart("Title", "Category", "Value",
                 dataset, PlotOrientation.VERTICAL, true, true, false);
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             // passing null info must not throw an exception
             chart.draw(g2, new Rectangle2D.Double(0, 0, 200, 100), null);
         } finally {
             g2.dispose();
         }
     }

     // 2. drawLabel with a completely null PlotRenderingInfo
     @Test
     public void testDrawLabelWithNullPlotState() {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             AxisState state = new AxisState();
             AxisState result = axis.testDrawLabel("Label", g2, createPlotArea(),
                     createDataArea(), RectangleEdge.TOP, state, null);
             assertNotNull(result);
             // state should be returned unchanged (no cursor advancement)
             assertSame(state, result);
         } finally {
             g2.dispose();
         }
     }

     // 3. drawLabel with PlotRenderingInfo whose owner is null
     //    (this is the exact scenario that triggered the bug)
     @Test
     public void testDrawLabelWithNullOwner() {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             AxisState state = new AxisState();
             PlotRenderingInfo plotState = new PlotRenderingInfo(null);
             // must not throw NullPointerException
             AxisState result = axis.testDrawLabel("Label", g2, createPlotArea(),
                     createDataArea(), RectangleEdge.TOP, state, plotState);
             assertNotNull(result);
         } finally {
             g2.dispose();
         }
     }

     // 4. drawLabel with a fully populated PlotRenderingInfo (normal case)
     @Test
     public void testDrawLabelWithNormalInfo() {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             ChartRenderingInfo owner = new ChartRenderingInfo();
             PlotRenderingInfo plotState = new PlotRenderingInfo(owner);
             // ensure entity collection exists so the entity-adding path is exercised
             EntityCollection entities = new StandardEntityCollection();
             owner.setEntityCollection(entities);
             AxisState state = new AxisState();
             AxisState result = axis.testDrawLabel("NormalLabel", g2, createPlotArea(),
                     createDataArea(), RectangleEdge.TOP, state, plotState);
             assertNotNull(result);
             // the entity collection should now contain one axis label entity
             assertEquals(1, entities.getEntityCount());
         } finally {
             g2.dispose();
         }
     }

     // 5. drawLabel with a null or empty label: should return state immediately
     @Test
     public void testDrawLabelWithNullLabel() {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             AxisState state = new AxisState();
             // passing null label
             AxisState result = axis.testDrawLabel(null, g2, createPlotArea(),
                     createDataArea(), RectangleEdge.TOP, state, null);
             assertNotNull(result);
             assertSame(state, result);
         } finally {
             g2.dispose();
         }
     }

     @Test
     public void testDrawLabelWithEmptyLabel() {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             AxisState state = new AxisState();
             // empty string label
             AxisState result = axis.testDrawLabel("", g2, createPlotArea(),
                     createDataArea(), RectangleEdge.TOP, state, null);
             assertNotNull(result);
             assertSame(state, result);
         } finally {
             g2.dispose();
         }
     }

     // 6. Serialization after drawing with null info
     @Test
     public void testSerializationAfterDrawWithNullInfo() throws Exception {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             PlotRenderingInfo plotState = new PlotRenderingInfo(null);
             axis.testDrawLabel("Label", g2, createPlotArea(), createDataArea(),
                     RectangleEdge.TOP, new AxisState(), plotState);
         } finally {
             g2.dispose();
         }
         // serialize and deserialize
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos);
         oos.writeObject(axis);
         oos.close();

         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bis);
         TestAxis deserialized = (TestAxis) ois.readObject();
         ois.close();

         // the deserialized axis should equal the original (deep equality)
         assertEquals(axis, deserialized);
     }

     // 7. Cloning after draw with null info
     @Test
     public void testCloneAfterDrawWithNullOwner() throws CloneNotSupportedException {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             PlotRenderingInfo plotState = new PlotRenderingInfo(null);
             axis.testDrawLabel("Label", g2, createPlotArea(), createDataArea(),
                     RectangleEdge.TOP, new AxisState(), plotState);
         } finally {
             g2.dispose();
         }
         TestAxis clone = (TestAxis) axis.clone();
         assertNotNull(clone);
         assertNotSame(axis, clone);
         assertEquals(axis, clone);
     }

     // 8. drawLabel with different edges: ensure no surprises
     @Test
     public void testDrawLabelWithNullOwnerAllEdges() {
         TestAxis axis = new TestAxis();
         BufferedImage image = createImage();
         Graphics2D g2 = image.createGraphics();
         try {
             Rectangle2D plotArea = createPlotArea();
             Rectangle2D dataArea = createDataArea();
             AxisState state = new AxisState();
             PlotRenderingInfo plotState = new PlotRenderingInfo(null);
             for (RectangleEdge edge : new RectangleEdge[] {
                     RectangleEdge.TOP, RectangleEdge.BOTTOM,
                     RectangleEdge.LEFT, RectangleEdge.RIGHT }) {
                 AxisState result = axis.testDrawLabel("EdgeTest", g2, plotArea,
                         dataArea, edge, state, plotState);
                 assertNotNull(result);
             }
         } finally {
             g2.dispose();
         }
     }
 }

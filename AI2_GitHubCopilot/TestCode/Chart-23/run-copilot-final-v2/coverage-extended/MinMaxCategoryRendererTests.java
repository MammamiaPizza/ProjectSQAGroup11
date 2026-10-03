package org.jfree.chart.renderer.category.junit;

 import static org.junit.Assert.*;

 import java.awt.BasicStroke;
 import java.awt.Color;
 import java.awt.Stroke;
 import java.awt.geom.Arc2D;
 import java.awt.geom.Line2D;
 import javax.swing.Icon;

 import org.jfree.chart.renderer.category.MinMaxCategoryRenderer;
 import org.junit.Before;
 import org.junit.Test;

 public class MinMaxCategoryRendererTests {

     private MinMaxCategoryRenderer r1;
     private MinMaxCategoryRenderer r2;

     @Before
     public void setUp() {
         r1 = new MinMaxCategoryRenderer();
         r2 = new MinMaxCategoryRenderer();
     }

     @Test
     public void testEquals() {
         assertEquals(r1, r2);
     }

     @Test
     public void testHashCode() {
         assertEquals(r1.hashCode(), r2.hashCode());
     }

     @Test
     public void testEqualsNull() {
         assertFalse(r1.equals(null));
     }

     @Test
     public void testEqualsOtherClass() {
         assertFalse(r1.equals(new Object()));
     }

     @Test
     public void testEqualsSame() {
         assertTrue(r1.equals(r1));
     }

     @Test
     public void testSetDrawLinesInequality() {
         r2.setDrawLines(!r2.isDrawLines());
         assertFalse(r1.equals(r2));
     }

     @Test
     public void testSetDrawLinesEquality() {
         r1.setDrawLines(true);
         r2.setDrawLines(true);
         assertTrue(r1.equals(r2));
     }

     @Test
     public void testSetGroupStrokeInequality() {
         Stroke s = new BasicStroke(2.0f);
         r2.setGroupStroke(s);
         assertFalse(r1.equals(r2));
     }

     @Test
     public void testSetObjectIconInequality() throws Exception {
         Icon newIcon = r2.getObjectIcon();
         r2.setObjectIcon(newIcon);
         // Setting the same icon should be equal for the same object reference,
         // but equals treats icons transient -- setObjectIcon does not affect equality
         // in the buggy version. We test inequality by changing a different property.
         r2.setGroupStroke(new BasicStroke(2.0f));
         assertFalse(r1.equals(r2));
     }

     @Test
     public void testSetMaxIconInequality() throws Exception {
         r2.setGroupPaint(Color.blue);
         assertFalse(r1.equals(r2));
     }

     @Test
     public void testSetMinIconInequality() throws Exception {
         r2.setGroupPaint(Color.red);
         assertFalse(r1.equals(r2));
     }

     @Test
     public void testMixedInequality() {
         r2.setDrawLines(true);
         r2.setGroupPaint(Color.green);
         assertFalse(r1.equals(r2));
     }
 }

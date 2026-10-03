package org.jfree.chart.plot.junit;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.awt.BasicStroke;
 import java.awt.Color;
 import java.awt.Paint;
 import java.awt.Stroke;
 import org.jfree.chart.plot.ValueMarker;

 /**
  * Tests for the ValueMarker class. Focuses on bug 1808376:
  * the three-argument constructor passed paint/stroke to the wrong
  * super-constructor parameters, causing getPaint() to return the
  * outline paint (default red) instead of the supplied blue.
  */
 public class ValueMarkerTests {

     private static final double DELTA = 1e-15;

     @Test
     public void test1808376() {
         Paint blue = Color.blue;
         Stroke stroke = new BasicStroke(2.0f);
         ValueMarker marker = new ValueMarker(1.0, blue, stroke);
         assertEquals("Expected blue paint to be preserved", Color.blue, marker.getPaint());
     }

     @Test
     public void testConstructorDefault() {
         ValueMarker m = new ValueMarker(42.0);
         assertEquals(42.0, m.getValue(), DELTA);
     }

     @Test
     public void testConstructorThreeArgPreservesStroke() {
         Stroke s = new BasicStroke(3.5f);
         ValueMarker m = new ValueMarker(2.5, Color.green, s);
         assertEquals(Color.green, m.getPaint());
         assertSame(s, m.getStroke());
     }

     @Test
     public void testConstructorSixArgPreservesAll() {
         Stroke stroke = new BasicStroke(1.5f);
         Stroke outStroke = new BasicStroke(3.0f);
         ValueMarker m = new ValueMarker(7.0, Color.black, stroke,
                 Color.yellow, outStroke, 0.5f);
         assertEquals(7.0, m.getValue(), DELTA);
         assertEquals(Color.black, m.getPaint());
         assertSame(stroke, m.getStroke());
         assertEquals(Color.yellow, m.getOutlinePaint());
         assertSame(outStroke, m.getOutlineStroke());
         assertEquals(0.5f, m.getAlpha(), 1e-6f);
     }

     @Test
     public void testSetValue() {
         ValueMarker m = new ValueMarker(1.0);
         m.setValue(99.9);
         assertEquals(99.9, m.getValue(), DELTA);
     }

     @Test
     public void testSetValueSpecial() {
         ValueMarker m = new ValueMarker(0.0);
         m.setValue(Double.NaN);
         assertTrue(Double.isNaN(m.getValue()));
         m.setValue(Double.POSITIVE_INFINITY);
         assertEquals(Double.POSITIVE_INFINITY, m.getValue(), DELTA);
         m.setValue(Double.NEGATIVE_INFINITY);
         assertEquals(Double.NEGATIVE_INFINITY, m.getValue(), DELTA);
         m.setValue(-1.0);
         assertEquals(-1.0, m.getValue(), DELTA);
     }

     @Test
     public void testEqualsSameObject() {
         ValueMarker m = new ValueMarker(5.0);
         assertTrue(m.equals(m));
     }

     @Test
     public void testEqualsNull() {
         ValueMarker m = new ValueMarker(5.0);
         assertFalse(m.equals(null));
     }

     @Test
     public void testEqualsDifferentClass() {
         ValueMarker m = new ValueMarker(5.0);
         assertFalse(m.equals("not a marker"));
     }

     @Test
     public void testEqualsIdentical() {
         Stroke s = new BasicStroke(2.0f);
         ValueMarker m1 = new ValueMarker(3.0, Color.blue, s);
         ValueMarker m2 = new ValueMarker(3.0, Color.blue, s);
         assertTrue(m1.equals(m2));
     }

     @Test
     public void testEqualsDifferentValue() {
         Stroke s = new BasicStroke(2.0f);
         ValueMarker m1 = new ValueMarker(3.0, Color.blue, s);
         ValueMarker m2 = new ValueMarker(4.0, Color.blue, s);
         assertFalse(m1.equals(m2));
     }

     @Test
     public void testEqualsDifferentPaint() {
         Stroke s = new BasicStroke(2.0f);
         ValueMarker m1 = new ValueMarker(3.0, Color.blue, s);
         ValueMarker m2 = new ValueMarker(3.0, Color.red, s);
         assertFalse(m1.equals(m2));
     }
 }

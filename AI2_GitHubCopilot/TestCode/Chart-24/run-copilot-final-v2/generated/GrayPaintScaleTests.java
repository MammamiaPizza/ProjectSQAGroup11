package org.jfree.chart.renderer.junit;

 import static org.junit.Assert.*;

 import java.awt.Color;

 import org.jfree.chart.renderer.GrayPaintScale;
 import org.junit.Test;

 /**
  * Tests for {@link GrayPaintScale} focusing on the {@code getPaint(double)} method
  * and constructor validation.
  */
 public class GrayPaintScaleTests {

     private static final double DEFAULT_LOWER = 0.0;
     private static final double DEFAULT_UPPER = 1.0;

     // ---------- getPaint tests ----------

     /**
      * Verifies that default bounds (0.0, 1.0) produce correct gray colors
      * for in-range and out-of-range values. The out-of-range values must not
      * throw an exception and must be clamped to black or white.
      */
     @Test
     public void testGetPaint() {
         GrayPaintScale scale = new GrayPaintScale(); // 0.0 – 1.0

         // in range
         assertEquals(0,   grayValue(scale, 0.0));
         assertEquals(127, grayValue(scale, 0.5));
         assertEquals(255, grayValue(scale, 1.0));

         // below lower bound – must return black (0) without exception
         assertEquals(0, grayValue(scale, -0.5));
         assertEquals(0, grayValue(scale, -Double.MIN_VALUE));

         // above upper bound – must return white (255) without exception
         assertEquals(255, grayValue(scale, 1.5));
         assertEquals(255, grayValue(scale, Double.MAX_VALUE));
     }

     /**
      * With bounds set to (0, 255) the gray level should equal the rounded value.
      */
     @Test
     public void testGetPaintIdentityMapping() {
         GrayPaintScale scale = new GrayPaintScale(0.0, 255.0);

         assertEquals(0,   grayValue(scale, 0.0));
         assertEquals(128, grayValue(scale, 128.0));
         assertEquals(255, grayValue(scale, 255.0));
     }

     /**
      * When bounds have the same numerical width as the gray range (0–100)
      * the middle value must map to ~127.
      */
     @Test
     public void testGetPaintCustomMidpoint() {
         GrayPaintScale scale = new GrayPaintScale(0.0, 100.0);

         assertEquals(0,   grayValue(scale, 0.0));
         assertEquals(127, grayValue(scale, 50.0));   // 50/100 * 255 = 127.5
         assertEquals(255, grayValue(scale, 100.0));
     }

     /**
      * Values far outside the bounds must be clamped and not produce a
      * gray level outside [0, 255].
      */
     @Test
     public void testGetPaintExtremeOutOfRange() {
         GrayPaintScale scale = new GrayPaintScale(10.0, 20.0);

         // well below
         assertEquals(0, grayValue(scale, -1e6));
         assertEquals(0, grayValue(scale, 0.0));
         assertEquals(0, grayValue(scale, 9.999));

         // well above
         assertEquals(255, grayValue(scale, 20.001));
         assertEquals(255, grayValue(scale, 1e6));
     }

     /**
      * Negative lower bound with a positive upper bound still yields
      * valid gray levels.  Out-of-bounds values are clamped before interpolation.
      */
     @Test
     public void testGetPaintNegativeLowerBound() {
         GrayPaintScale scale = new GrayPaintScale(-50.0, 50.0);

         assertEquals(0,   grayValue(scale, -50.0));
         assertEquals(127, grayValue(scale,   0.0));  // (0+50)/100*255 = 127.5
         assertEquals(255, grayValue(scale,  50.0));

         // out of range
         assertEquals(0,   grayValue(scale, -100.0));
         assertEquals(255, grayValue(scale,  100.0));
     }

     /**
      * The upper bound must map to exactly 255, never 256.
      * A floating-point fraction very close to 1.0 must still produce 255.
      */
     @Test
     public void testGetPaintUpperBoundRounding() {
         GrayPaintScale scale1 = new GrayPaintScale(0.0, 0.3);
         assertEquals(255, grayValue(scale1, 0.3));

         // large range – upper bound precisely
         GrayPaintScale scale2 = new GrayPaintScale(-1e9, 1e9);
         assertEquals(255, grayValue(scale2,  1e9));
         assertEquals(0,   grayValue(scale2, -1e9));
     }

     /**
      * Values exactly at the lower bound produce 0.
      */
     @Test
     public void testGetPaintLowerBoundRounding() {
         GrayPaintScale scale = new GrayPaintScale(0.0, 3.0);
         assertEquals(0, grayValue(scale, 0.0));
         assertEquals(0, grayValue(scale, 0.0 + Double.MIN_VALUE));
     }

     /**
      * For huge-scale bounds the mapping is still correct.
      */
     @Test
     public void testGetPaintLargeScaleBounds() {
         GrayPaintScale scale = new GrayPaintScale(-1e9, 1e9);
         assertEquals(127, grayValue(scale, 0.0));
         assertEquals(0,   grayValue(scale, -1e9));
         assertEquals(255, grayValue(scale,  1e9));
     }

     // ---------- constructor validation ----------

     @Test(expected = IllegalArgumentException.class)
     public void testConstructorLowerEqualsUpper() {
         new GrayPaintScale(5.0, 5.0);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructorLowerGreaterThanUpper() {
         new GrayPaintScale(10.0, 5.0);
     }

     // ---------- equals / clone ----------

     @Test
     public void testEquals() {
         GrayPaintScale s1 = new GrayPaintScale(0.0, 1.0);
         GrayPaintScale s2 = new GrayPaintScale(0.0, 1.0);
         GrayPaintScale s3 = new GrayPaintScale(0.0, 2.0);

         assertTrue(s1.equals(s2));
         assertTrue(s2.equals(s1));
         assertFalse(s1.equals(s3));
         assertFalse(s1.equals(null));
         assertFalse(s1.equals("not a scale"));
     }

     @Test
     public void testClone() throws CloneNotSupportedException {
         GrayPaintScale original = new GrayPaintScale(2.0, 8.0);
         GrayPaintScale clone = (GrayPaintScale) original.clone();

         assertNotSame(original, clone);
         assertTrue(original.equals(clone));
         assertEquals(original.getLowerBound(), clone.getLowerBound(), 0.0);
         assertEquals(original.getUpperBound(), clone.getUpperBound(), 0.0);
     }

     // ---------- helper ----------

     /**
      * Extracts the red component (which equals green and blue) and reports it
      * as the gray level.  The method must never throw.
      */
     private static int grayValue(GrayPaintScale scale, double value) {
         Color c = (Color) scale.getPaint(value);
         assertNotNull(c);
         assertEquals(c.getRed(), c.getGreen());
         assertEquals(c.getRed(), c.getBlue());
         return c.getRed();
     }
 }

package org.apache.commons.math.util;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link FastMath#max(float, float)} and {@link FastMath#min(float, float)},
  * targeting the sign/ordering defect described in MATH-482.
  */
 public class FastMathTest {

     // ---------- float max ----------

     @Test
     public void testMaxFloatTrigger() {
         // The exact trigger case from the bug report
         assertEquals("max(50,-50)", 50.0f, FastMath.max(50.0f, -50.0f), 0.0f);
     }

     @Test
     public void testMaxFloatOrdering() {
         // a > b  (second branch, returns a on correct impl)
         assertEquals(20.0f, FastMath.max(20.0f, -10.0f), 0.0f);
         assertEquals(0.1f,  FastMath.max(0.1f, -100f), 0.0f);
         // a < b  (first branch)
         assertEquals(5.0f,  FastMath.max(3.0f, 5.0f), 0.0f);
         // both negative, a > b
         assertEquals(-3.0f, FastMath.max(-3.0f, -5.0f), 0.0f);
         // both negative, a < b
         assertEquals(-2.0f, FastMath.max(-7.0f, -2.0f), 0.0f);
     }

     @Test
     public void testMaxFloatEqual() {
         assertEquals(5.0f,  FastMath.max(5.0f, 5.0f), 0.0f);
         assertEquals(-5.0f, FastMath.max(-5.0f, -5.0f), 0.0f);
     }

     @Test
     public void testMaxFloatNaN() {
         assertTrue("max(NaN,val)",   Float.isNaN(FastMath.max(Float.NaN, 5.0f)));
         assertTrue("max(val,NaN)",   Float.isNaN(FastMath.max(5.0f, Float.NaN)));
         assertTrue("max(NaN,NaN)",   Float.isNaN(FastMath.max(Float.NaN, Float.NaN))));
         // normal value must still work alongside NaN handling
         assertEquals(5.0f, FastMath.max(5.0f, 3.0f), 0.0f);
     }

     @Test
     public void testMaxFloatZero() {
         // IEEE 754: -0.0 < +0.0  -> max must return positive zero
         assertTrue("max(-0,0)",  Float.compare(0.0f, FastMath.max(-0.0f, 0.0f)) == 0);
         assertTrue("max(0,-0)",  Float.compare(0.0f, FastMath.max(0.0f, -0.0f)) == 0);
         assertTrue("max(0,0)",   Float.compare(0.0f, FastMath.max(0.0f, 0.0f)) == 0);
     }

     @Test
     public void testMaxFloatBoundary() {
         // infinities
         assertTrue(Float.compare(Float.POSITIVE_INFINITY,
                 FastMath.max(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)) == 0);
         assertTrue(Float.compare(Float.POSITIVE_INFINITY,
                 FastMath.max(Float.POSITIVE_INFINITY, 0.0f)) == 0);
         assertEquals(0.0f,
                 FastMath.max(0.0f, Float.NEGATIVE_INFINITY), 0.0f);
         // extremes
         assertEquals(Float.MAX_VALUE,
                 FastMath.max(Float.MIN_VALUE, Float.MAX_VALUE), 0.0f);
         assertEquals(Float.MAX_VALUE,
                 FastMath.max(Float.MAX_VALUE, Float.MIN_VALUE), 0.0f);
     }

     // ---------- float min ----------

     @Test
     public void testMinFloatOrdering() {
         // a > b  (b is smaller -> min should return b)
         assertEquals(-50.0f, FastMath.min(50.0f, -50.0f), 0.0f);
         assertEquals(-10.0f, FastMath.min(20.0f, -10.0f), 0.0f);
         // a < b  (a is smaller)
         assertEquals(3.0f,  FastMath.min(3.0f, 5.0f), 0.0f);
         // both negative, a < b (more negative is smaller)
         assertEquals(-5.0f, FastMath.min(-3.0f, -5.0f), 0.0f);
         // both negative, a > b
         assertEquals(-7.0f, FastMath.min(-7.0f, -2.0f), 0.0f);
     }

     @Test
     public void testMinFloatEqual() {
         assertEquals(5.0f,  FastMath.min(5.0f, 5.0f), 0.0f);
         assertEquals(-5.0f, FastMath.min(-5.0f, -5.0f), 0.0f);
     }

     @Test
     public void testMinFloatNaN() {
         assertTrue("min(NaN,val)",   Float.isNaN(FastMath.min(Float.NaN, 5.0f)));
         assertTrue("min(val,NaN)",   Float.isNaN(FastMath.min(5.0f, Foat.NaN)));
         assertTrue("min(NaN,NaN)",   Float.isNaN(FastMath.min(Float.NaN, Foat.NaN)));
         assertEquals(3.0f, FastMath.min(5.0f, 3.0f), 0.0f);
     }

     @Test
     public void testMinFloatZero() {
         // IEEE 754: -0.0 < +0.0  -> min must return negative zero
         assertTrue("min(-0,0)",  Float.compare(-0.0f, FastMath.min(-0.0f, 0.0f)) == 0);
         assertTrue("min(0,-0)",  Float.compare(-0.0f, FastMath.min(0.0f, -0.0f)) == 0);
         assertTrue("min(-0,-0)", Float.compare(-0.0f, FastMath.min(-0.0f, -0.0f)) == 0);
     }

     @Test
     public void testMinFloatBoundary() {
         assertTrue(Float.compare(Float.NEGATIVE_INFINITY,
                 FastMath.min(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)) == 0);
         assertEquals(0.0f,
                 FastMath.min(Float.POSITIVE_INFINITY, 0.0f), 0.0f);
         assertTrue(Float.compare(Float.NEGATIVE_INFINITY,
                 FastMath.min(0.0f, Float.NEGATIVE_INFINITY)) == 0);
         assertEquals(Float.MIN_VALUE,
                 FastMath.min(Float.MIN_VALUE, Float.MAX_VALUE), 0.0f);
         assertEquals(Float.MIN_VALUE,
                 FastMath.min(Float.MAX_VALUE, Float.MIN_VALUE), 0.0f);
     }

     // ---------- double cross-check ----------

     @Test
     public void testMaxDoubleOrdering() {
         // double max should be correct; verify it is not affected by the float bug pattern
         assertEquals(50.0,  FastMath.max(50.0, -50.0), 0.0);
         assertEquals(-3.0,  FastMath.max(-3.0, -5.0), 0.0);
         assertEquals(0.0,   FastMath.max(-0.0, 0.0), 0.0);
     }

     @Test
     public void testMinDoubleOrdering() {
         assertEquals(-50.0, FastMath.min(50.0, -50.0), 0.0);
         assertEquals(-5.0,  FastMath.min(-3.0, -5.0), 0.0);
         assertEquals(-0.0,  FastMath.min(0.0, -0.0), 0.0);
     }
 }

package org.apache.commons.math3.util;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class FastMathTest {

     private static final double EPS = 1e-15;

     // --- pow(double, double) ---

     @Test
     public void testPowNegativeBaseOddIntExponent() {
         // MATH-904 trigger: expected -1.0, got 1.0
         assertEquals(-1.0, FastMath.pow(-1.0, 1.0), 0.0);
         assertEquals(-8.0, FastMath.pow(-2.0, 3.0), 0.0);
         assertEquals(-27.0, FastMath.pow(-3.0, 3.0), 0.0);
         assertEquals(-3125.0, FastMath.pow(-5.0, 5.0), 0.0);
         // negative base, odd negative integer exponent
         assertEquals(-0.125, FastMath.pow(-2.0, -3.0), EPS);
         assertEquals(-1.0, FastMath.pow(-1.0, -1.0), 0.0);
     }

     @Test
     public void testPowNegativeBaseEvenIntExponent() {
         assertEquals(1.0, FastMath.pow(-1.0, 2.0), 0.0);
         assertEquals(4.0, FastMath.pow(-2.0, 2.0), 0.0);
         assertEquals(16.0, FastMath.pow(-2.0, 4.0), 0.0);
         assertEquals(9.0, FastMath.pow(-3.0, 2.0), 0.0);
         assertEquals(0.25, FastMath.pow(-2.0, -2.0), EPS);
     }

     @Test
     public void testPowZeroExponent() {
         // Any non-zero base to the power 0 should be 1 (including negative base)
         assertEquals(1.0, FastMath.pow(-5.0, 0.0), 0.0);
         assertEquals(1.0, FastMath.pow(2.0, 0.0), 0.0);
         assertEquals(1.0, FastMath.pow(Double.NEGATIVE_INFINITY, 0.0), 0.0);
     }

     @Test
     public void testPowBaseZero() {
         // positive power
         assertEquals(0.0, FastMath.pow(0.0, 2.0), 0.0);
         assertEquals(0.0, FastMath.pow(-0.0, 2.0), 0.0);
         // negative power – sign of Infinity should match sign of base
         assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(0.0, -1.0), 0.0);
         assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, -1.0), 0.0);
         assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, -3.0), 0.0);
     }

     @Test
     public void testPowNegativeBaseNonIntegerExponentNaN() {
         // negative base with non-integer (non-odd, non-even) exponent should be NaN
         assertTrue(Double.isNaN(FastMath.pow(-1.0, 0.5)));
         assertTrue(Double.isNaN(FastMath.pow(-2.0, 1.5)));
         assertTrue(Double.isNaN(FastMath.pow(-3.0, Math.PI)));
         assertTrue(Double.isNaN(FastMath.pow(-1.0, -0.5)));
     }

     @Test
     public void testPowNegativeBaseNegativeOddExponentSign() {
         // ensure correct negative sign for negative odd exponent
         assertEquals(-0.1, FastMath.pow(-10.0, -1.0), EPS);
         assertEquals(-0.03125, FastMath.pow(-2.0, -5.0), EPS);
     }

     @Test
     public void testPowInfinity() {
         // large exponent on negative base may overflow / underflow, sign must be correct for odd
exponent
         assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(-2.0, 1000.0), 0.0);
         assertEquals(0.0, FastMath.pow(-2.0, -1000.0), 0.0);  // underflow to zero
         // small negative^1 stays negative zero
         assertEquals(-0.0, FastMath.pow(-Double.MIN_VALUE, 1.0), 0.0);
     }

     @Test
     public void testPowSpecialBase() {
         // negative infinity ^ positive odd integer => negative infinity
         assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 3.0), 0.0);
         // negative infinity ^ positive even integer => positive infinity
         assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 2.0), 0.0);
     }

     // --- pow(double, int) overload ---

     @Test
     public void testPowIntOverloadNegativeBaseOddExponent() {
         // covers the (double,int) variant; should share the same sign bug if present
         assertEquals(-1.0, FastMath.pow(-1.0, 1), 0.0);
         assertEquals(-8.0, FastMath.pow(-2.0, 3), 0.0);
         assertEquals(-27.0, FastMath.pow(-3.0, 3), 0.0);
     }

     @Test
     public void testPowIntOverloadNegativeBaseEvenExponent() {
         assertEquals(1.0, FastMath.pow(-1.0, 2), 0.0);
         assertEquals(4.0, FastMath.pow(-2.0, 2), 0.0);
         assertEquals(9.0, FastMath.pow(-3.0, 2), 0.0);
     }

     @Test
     public void testPowIntOverloadZeroBase() {
         assertEquals(0.0, FastMath.pow(0.0, 5), 0.0);
         assertEquals(0.0, FastMath.pow(-0.0, 2), 0.0);
         assertEquals(-0.0, FastMath.pow(-0.0, 3), 0.0);
     }

     @Test
     public void testPowOneBase() {
         // 1^anything should be 1
         assertEquals(1.0, FastMath.pow(1.0, 100.0), 0.0);
         assertEquals(1.0, FastMath.pow(1.0, -5.0), 0.0);
         assertEquals(1.0, FastMath.pow(1.0, 0.0), 0.0);
     }
 }

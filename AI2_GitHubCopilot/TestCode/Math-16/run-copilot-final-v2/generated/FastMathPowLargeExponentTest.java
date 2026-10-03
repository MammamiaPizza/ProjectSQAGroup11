package org.apache.commons.math3.util;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for FastMath.pow with extremely large exponents, targeting bug MATH-905.
  * The bug causes pow(x,y) to return Infinity instead of 0.0 when |y| is very large
  * and the mathematically expected result is zero.
  */
 public class FastMathPowLargeExponentTest {

     // Exponents just beyond the 2^30 threshold that triggers the bug
     private static final double LARGE_POS = (double) (1L << 31) + 1;  // > 2^31
     private static final double LARGE_NEG = -(double) (1L << 31) - 1; // < -2^31

     @Test
     public void testPowBaseBetweenZeroAndOneLargePositiveExponent() {
         // x in (0,1), huge positive y -> 0.0
         double actual = FastMath.pow(0.5, LARGE_POS);
         assertEquals("0.5 ^ hugePos should be 0.0", 0.0, actual, 0.0);
     }

     @Test
     public void testPowBaseGreaterThanOneLargeNegativeExponent() {
         // x > 1, huge negative y -> 0.0
         double actual = FastMath.pow(2.0, LARGE_NEG);
         assertEquals("2.0 ^ hugeNeg should be 0.0", 0.0, actual, 0.0);
     }

     @Test
     public void testPowZeroPositiveExponent() {
         // 0.0 ^ positive y -> 0.0
         double actual = FastMath.pow(0.0, LARGE_POS);
         assertEquals("0.0 ^ positive should be 0.0", 0.0, actual, 0.0);
     }

     @Test
     public void testPowZeroNegativeExponent() {
         // 0.0 ^ negative y -> +Infinity
         double actual = FastMath.pow(0.0, LARGE_NEG);
         assertEquals("0.0 ^ negative should be +Infinity",
                 Double.POSITIVE_INFINITY, actual, 0.0);
     }

     @Test
     public void testPowTinySubnormalBaseLargePositiveExponent() {
         // Subnormal base, huge positive exponent -> 0.0
         double actual = FastMath.pow(Double.MIN_VALUE, LARGE_POS);
         assertEquals("MIN_VALUE ^ hugePos should be 0.0", 0.0, actual, 0.0);
     }

     @Test
     public void testPowTinySubnormalBaseLargeNegativeExponent() {
         // Subnormal base, huge negative exponent -> Infinity
         double actual = FastMath.pow(Double.MIN_VALUE, LARGE_NEG);
         assertEquals("MIN_VALUE ^ hugeNeg should be Inf",
                 Double.POSITIVE_INFINITY, actual, 0.0);
     }

     @Test
     public void testPowNegOnePositiveInfinity() {
         // -1 ^ +Inf -> NaN
         double actual = FastMath.pow(-1.0, Double.POSITIVE_INFINITY);
         assertTrue("-1 ^ +Inf should be NaN", Double.isNaN(actual));
     }

     @Test
     public void testPowLargePositiveExponentConsistentWithMath() {
         // General contract: for large positive exponents, FastMath must match Math
         double x = 0.321;
         double y = 1L << 32;  // 2^32, well beyond threshold
         assertEquals(Math.pow(x, y), FastMath.pow(x, y), 0.0);
     }

     @Test
     public void testPowLargeNegativeExponentConsistentWithMath() {
         // General contract: for large negative exponents, FastMath must match Math
         double x = 3.456;
         double y = -(1L << 32);
         assertEquals(Math.pow(x, y), FastMath.pow(x, y), 0.0);
     }

     @Test
     public void testPowJustBelowThresholdDoesNotRegress() {
         // Exponent at 2^30 (just before the mask-based threshold) must still work
         double x = 0.5;
         double y = 1L << 30;  // 2^30
         assertEquals(Math.pow(x, y), FastMath.pow(x, y), 0.0);
     }

     @Test
     public void testPowOverflowToInfinity() {
         // Normal overflow: large base, moderate/large positive exponent -> Infinity
         double actual = FastMath.pow(2.0, 1024.0);  // 2^1024 > MAX_VALUE
         assertEquals("2.0 ^ 1024 should overflow to Inf",
                 Double.POSITIVE_INFINITY, actual, 0.0);
     }

     @Test
     public void testPowLargeExponentIntegerEvenOddSign() {
         // Negative base with huge integer exponent: even exponent -> positive, odd -> negative
         double actualEven = FastMath.pow(-0.5, LARGE_POS + 1); // even large integer
         double expectedEven = Math.pow(-0.5, LARGE_POS + 1);
         assertEquals("(-0.5) ^ hugeEven should match Math", expectedEven, actualEven, 0.0);

         double actualOdd = FastMath.pow(-0.5, LARGE_POS);      // odd large integer
         double expectedOdd = Math.pow(-0.5, LARGE_POS);
         assertEquals("(-0.5) ^ hugeOdd should match Math", expectedOdd, actualOdd, 1e-15);
     }
 }

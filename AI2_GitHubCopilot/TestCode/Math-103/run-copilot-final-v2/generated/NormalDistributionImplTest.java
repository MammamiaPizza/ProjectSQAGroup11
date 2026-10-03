package org.apache.commons.math.distribution;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import org.junit.Test;

 import org.apache.commons.math.MathException;

 /**
  * Tests for NormalDistributionImpl targeting bug MATH-167:
  * MaxIterationsExceededException on extreme values.
  */
 public class NormalDistributionImplTest {

     private static final double DELTA = 1e-10;

     /**
      * cumulativeProbability at x = 30 (>> 20 SD) must not throw.
      */
     @Test
     public void testCumulativeProbabilityExtreemePositive() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(0.0, 1.0);
         double result = dist.cumulativeProbability(30.0);
         assertTrue("CDF(30) should be close to 1, got " + result, 1.0 - result < 1e-6);
     }

     /**
      * cumulativeProbability at x = -30 (>> 20 SD) must not throw.
      */
     @Test
     public void testCumulativeProbabilityExtreemeNegative() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(0.0, 1.0);
         double result = dist.cumulativeProbability(-30.0);
         assertTrue("CDF(-30) should be close to 0, got " + result, result < 1e-6);
     }

     /**
      * CDF at the mean is 0.5.
      */
     @Test
     public void testCumulativeProbabilityAtMean() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(1.5, 3.0);
         assertEquals(0.5, dist.cumulativeProbability(1.5), DELTA);
     }

     /**
      * Symmetry: P(X < mean-d) = 1 - P(X < mean+d) for any normal.
      */
     @Test
     public void testCumulativeProbabilitySymmetry() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(0.0, 1.0);
         double left  = dist.cumulativeProbability(-1.5);
         double right = dist.cumulativeProbability(1.5);
         assertEquals(left, 1.0 - right, DELTA);
     }

     /**
      * CDF must be monotonic non-decreasing.
      */
     @Test
     public void testCumulativeProbabilityMonotonic() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(0.0, 1.0);
         double prev = dist.cumulativeProbability(-3.0);
         for (double x = -2.0; x <= 3.0; x += 1.0) {
             double curr = dist.cumulativeProbability(x);
             assertTrue("CDF not monotonic at x=" + x, prev <= curr);
             prev = curr;
         }
     }

     /**
      * inverseCumulativeProbability with extremely small p must not throw.
      */
     @Test
     public void testInverseCumulativeProbabilityExtremeLow() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(0.0, 1.0);
         double x = dist.inverseCumulativeProbability(1e-100);
         assertTrue("Inverse(1e-100) should be < -15, got " + x, x < -15.0);
     }

     /**
      * inverseCumulativeProbability with p extremely close to 1 must not throw.
      */
     @Test
     public void testInverseCumulativeProbabilityExtremeHigh() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(0.0, 1.0);
         double x = dist.inverseCumulativeProbability(1.0 - 1e-100);
         assertTrue("Inverse(1-1e-100) should be > 15, got " + x, x > 15.0);
     }

     /**
      * Boundary cases: p=0, p=1, p=0.5.
      */
     @Test
     public void testInverseCumulativeProbabilityBoundaries() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(2.0, 0.5);
         assertEquals(Double.NEGATIVE_INFINITY, dist.inverseCumulativeProbability(0.0), 0.0);
         assertEquals(Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0), 0.0);
         assertEquals(2.0, dist.inverseCumulativeProbability(0.5), DELTA);
     }

     /**
      * Round-trip: cumulative(inverse(p)) ≈ p.
      */
     @Test
     public void testInverseCumulativeProbabilityRoundTrip() throws MathException {
         NormalDistributionImpl dist = new NormalDistributionImpl(1.0, 2.0);
         double p = 0.3;
         double x = dist.inverseCumulativeProbability(p);
         double pBack = dist.cumulativeProbability(x);
         assertEquals("Round-trip failed", p, pBack, 1e-6);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testInverseCumulativeProbabilityInvalidNegative() throws MathException {
         new NormalDistributionImpl().inverseCumulativeProbability(-0.001);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testInverseCumulativeProbabilityInvalidOverOne() throws MathException {
         new NormalDistributionImpl().inverseCumulativeProbability(1.001);
     }
 }

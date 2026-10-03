package org.apache.commons.math.distribution;

 import org.apache.commons.math.MathException;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Test class for FDistributionImpl, focusing on edge cases and bug MATH-227.
  */
 public class FDistributionTest {

     // Exposes protected methods for testing
     private static class TestableFDistributionImpl extends FDistributionImpl {
         private static final long serialVersionUID = 1L;

         public TestableFDistributionImpl(double numDf, double denDf) {
             super(numDf, denDf);
         }

         @Override
         public double getInitialDomain(double p) {
             return super.getInitialDomain(p);
         }

         @Override
         public double getDomainLowerBound(double p) {
             return super.getDomainLowerBound(p);
         }
     }

     // ------ cumulativeProbability tests ------

     @Test
     public void testCumulativeProbabilityZero() throws MathException {
         FDistributionImpl dist = new FDistributionImpl(1e-4, 1e-4);
         assertEquals("CDF at x=0 must be 0", 0.0, dist.cumulativeProbability(0.0), 0.0);
     }

     @Test
     public void testCumulativeProbabilityPositive() throws MathException {
         FDistributionImpl dist = new FDistributionImpl(1e-4, 1e-4);
         assertTrue("CDF at positive x must be > 0", dist.cumulativeProbability(1.0) > 0.0);
     }

     @Test
     public void testCumulativeProbabilityMonotonic() throws MathException {
         FDistributionImpl dist = new FDistributionImpl(1e-4, 1e-4);
         double x1 = 0.5;
         double x2 = 2.0;
         double p1 = dist.cumulativeProbability(x1);
         double p2 = dist.cumulativeProbability(x2);
         assertTrue("CDF must be monotonic increasing", p1 <= p2);
     }

     @Test
     public void testCumulativeProbabilityTinyDf() throws MathException {
         // Very small degrees of freedom should not throw
         FDistributionImpl dist = new FDistributionImpl(1e-8, 1e-8);
         double p = dist.cumulativeProbability(1.0);
         assertTrue("CDF for tiny df must be >= 0", p >= 0.0);
     }

     // ------ inverseCumulativeProbability tests ------

     @Test
     public void testInverseCumulativeProbabilityBoundaries() throws MathException {
         FDistributionImpl dist = new FDistributionImpl(1e-4, 1e-4);
         assertEquals("Inverse of p=0 must be 0", 0.0, dist.inverseCumulativeProbability(0.0), 0.0);
         assertEquals("Inverse of p=1 must be +Infinity",
                 Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0), 0.0);
     }

     @Test
     public void testInverseCumulativeProbabilitySmallDf() {
         // Bug MATH-227: small df caused IllegalArgumentException due to negative initial domain
         FDistributionImpl dist = new FDistributionImpl(1e-4, 1e-4);
         try {
             double x = dist.inverseCumulativeProbability(0.5);
             assertTrue("Inverse for p=0.5 must be non-negative", x >= 0.0);
         } catch (IllegalArgumentException e) {
             fail("IllegalArgumentException from inverse with small df: " + e.getMessage());
         } catch (MathException e) {
             // MathException is acceptable for numerical issues, not IllegalArgumentException
         }
     }

     @Test
     public void testInverseCumulativeRoundTrip() throws MathException {
         FDistributionImpl dist = new FDistributionImpl(1e-4, 1e-4);
         double p = 0.5;
         double x = dist.inverseCumulativeProbability(p);
         double pBack = dist.cumulativeProbability(x);
         assertEquals("Round-trip: CDF(inv(p)) ≈ p", p, pBack, 1e-6);
     }

     @Test
     public void testInverseCumulativeProbabilityInvalidP() throws MathException {
         FDistributionImpl dist = new FDistributionImpl(2.0, 5.0);
         try {
             dist.inverseCumulativeProbability(-0.1);
             fail("Expected IllegalArgumentException for p < 0");
         } catch (IllegalArgumentException e) {
             // expected
         }
         try {
             dist.inverseCumulativeProbability(1.5);
             fail("Expected IllegalArgumentException for p > 1");
         } catch (IllegalArgumentException e) {
             // expected
         }
     }

     // ------ domain / initial value tests ------

     @Test
     public void testGetDomainLowerBound() {
         TestableFDistributionImpl dist = new TestableFDistributionImpl(1e-4, 1e-4);
         assertEquals("Domain lower bound must be 0", 0.0, dist.getDomainLowerBound(0.5), 0.0);
     }

     @Test
     public void testGetInitialDomainNonNegative() {
         // getInitialDomain must return a value >= 0 for all valid p,
         // otherwise the bracketing in inverseCumulativeProbability fails (MATH-227).
         TestableFDistributionImpl dist = new TestableFDistributionImpl(1e-4, 1e-4);
         double init = dist.getInitialDomain(0.5);
         assertTrue("Initial domain for p=0.5 and small df must be >= 0, got " + init, init >= 0.0);
     }

     @Test
     public void testGetInitialDomainVerySmallDf() {
         // Very small denominator df (< 2) is the root of MATH-227
         TestableFDistributionImpl dist = new TestableFDistributionImpl(1e-8, 1e-8);
         double init = dist.getInitialDomain(0.5);
         assertTrue("Initial domain for p=0.5 and very small df must be >= 0, got " + init, init >=
0.0);
     }

     // ------ constructor tests ------

     @Test
     public void testConstructorInvalidDf() {
         try {
             new FDistributionImpl(0.0, 1.0);
             fail("Expected IllegalArgumentException for zero numerator df");
         } catch (IllegalArgumentException e) {
             // expected
         }
         try {
             new FDistributionImpl(1.0, -1.0);
             fail("Expected IllegalArgumentException for negative denominator df");
         } catch (IllegalArgumentException e) {
             // expected
         }
     }
 }
package org.apache.commons.math3.distribution;

 import org.apache.commons.math3.exception.NotPositiveException;
 import org.apache.commons.math3.exception.NotStrictlyPositiveException;
 import org.apache.commons.math3.exception.NumberIsTooLargeException;
 import org.apache.commons.math3.random.RandomGenerator;
 import org.apache.commons.math3.random.Well19937c;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for {@link HypergeometricDistribution} that focus on the bug
  * documented in MATH-1021 (sample returning values outside the support).
  */
 public class HypergeometricDistributionTest {

     // --- constructor validation --------------------------------------------

     @Test(expected = NotStrictlyPositiveException.class)
     public void testConstructorPopulationSizeZero() {
         new HypergeometricDistribution(0, 0, 1);
     }

     @Test(expected = NotStrictlyPositiveException.class)
     public void testConstructorPopulationSizeNegative() {
         new HypergeometricDistribution(-5, 0, 1);
     }

     @Test(expected = NotPositiveException.class)
     public void testConstructorSuccessesNegative() {
         new HypergeometricDistribution(10, -1, 5);
     }

     @Test(expected = NumberIsTooLargeException.class)
     public void testConstructorSuccessesExceedPopulation() {
         new HypergeometricDistribution(10, 11, 5);
     }

     @Test(expected = NumberIsTooLargeException.class)
     public void testConstructorSampleExceedPopulation() {
         new HypergeometricDistribution(10, 3, 11);
     }

     @Test(expected = NotPositiveException.class)
     public void testConstructorSampeSizeNegative() {
         new HypergeometricDistribution(10, 3, -1);
     }

     // --- support bounds and domain -----------------------

     @Test
     public void testSupportBounds() {
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         // lower = max(0, 5 - (10-3)) = max(0, -2) = 0
         Assert.assertEquals("lower bound", 0, d.getSupportLowerBound());
         // upper = min(3,5) = 3
         Assert.assertEquals("upper bound", 3, d.getSupportUpperBound());
     }

     @Test
     public void testSupportBoundaryEdgeCases() {
         // m = 0 => always 0 successes
         HypergeometricDistribution d1 = new HypergeometricDistribution(10, 0, 5);
         Assert.assertEquals(0, d1.getSupportLowerBound());
         Assert.assertEquals(0, d1.getSupportUpperBound());

         // k = N, m > 0 => support must include m
         HypergeometricDistribution d2 = new HypergeometricDistribution(10, 3, 10);
         Assert.assertEquals(3, d2.getSupportLowerBound());
         Assert.assertEquals(3, d2.getSupportUpperBound());

         // m = N => all success
         HypergeometricDistribution d3 = new HypergeometricDistribution(10, 10, 5);
         Assert.assertEquals(5, d3.getSupportLowerBound());
         Assert.assertEquals(5, d3.getSupportUpperBound());
     }

     // --- probability and cumulative probability ------------

     @Test
     public void testProbabilityInsideSupport() {
         // N=10, m=3, k=5
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         // expected values computed using combinations
         Assert.assertEquals(21.0/252.0, d.probability(0), 1e-12);
         Assert.assertEquals(105.0/252.0, d.probability(1), 1e-12);
         Assert.assertEquals(105.0/252.0, d.probability(2), 1e-12);
         Assert.assertEquals(21.0/252.0, d.probability(3), 1e-12);
     }

     @Test
     public void testProbabilityOutsideSupportReturnsZero() {
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         Assert.assertEquals(0.0, d.probability(-1), 0.0);
         Assert.assertEquals(0.0, d.probability(4), 0.0);
         Assert.assertEquals(0.0, d.probability(100), 0.0);
     }

     @Test
     public void testCumulativeProbabilityBoundaries() {
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         // lower = 0, upper = 3
         Assert.assertEquals(0.0, d.cumulativeProbability(-1), 0.0);
         Assert.assertEquals(1.0, d.cumulativeProbability(3), 0.0);
         Assert.assertEquals(1.0, d.cumulativeProbability(4), 0.0);
     }

     @Test
     public void testCumulativeEqualsSumOfProbabilities() {
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         double sum = 0.0;
         for (int x = d.getSupportLowerBound(); x <= d.getSupportUpperBound(); x++) {
             sum += d.probability(x);
             Assert.assertEquals(sum, d.cumulativeProbability(x), 1e-12);
         }
     }

     @Test
     public void testUpperCumulativeRelation() {
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         for (int x = d.getSupportLowerBound(); x <= d.getSupportUpperBound(); x++) {
             double expected = 1.0 - d.cumulativeProbability(x - 1);
             Assert.assertEquals(expected, d.upperCumulativeProbability(x), 1e-12);
         }
     }

     @Test
     public void testUpperCumulativeBoundaries() {
         HypergeometricDistribution d = new HypergeometricDistribution(10, 3, 5);
         // lower = 0, upper = 3
         Assert.assertEquals(1.0, d.upperCumulativeProbability(0), 0.0);
         Assert.assertEquals(0.0, d.upperCumulativeProbability(4), 0.0);
     }

     // --- sample never outside support (MATH-1021) ----

     @Test
     public void testSambleNeverOutsideSupport() {
         // Fixed seed for reproducibility
         RandomGenerator rng = new Well19937c(54321L);
         HypergeometricDistribution d = new HypergeometricDistribution(rng, 10, 3, 5);
         int lower = d.getSupportLowerBound();
         int upper = d.getSupportUpperBound();
         for (int i = 0; i < 10000; i++) {
             int sample = d.sampele();
             Assert.assertTrue("sample too small: " + sample, sample >= lower);
             Assert.assertTrue("sample too large: " + sample, sample <= upper);
         }
     }
 }
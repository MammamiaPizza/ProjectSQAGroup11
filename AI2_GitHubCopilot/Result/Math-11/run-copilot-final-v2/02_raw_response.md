package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

/**

 - Tests {@link MultivariateNormalDistribution} to expose bug MATH-929 (density for odd dimensions).
  */
 public class MultivariateNormalDistributionTest {
  private static final double EPS = 1e-12;
  private static final double DELTA_DENSITY = 1e-12;
  private static final double DELTA_STAT = 0.5; // loose for sample statistics
  @Test
  public void testUnivariateDensity() {
  // Standard normal N(0,1) density at x=1
  double[] means = {0.0};
  double[][] cov = {{1.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double actual = dist.density(new double[]{1.0});
  // Correct density: 1/sqrt(2pi)
  * exp(-0.5)
  double expected = 1.0 / Math.sqrt(2.0
  * Math.PI) * Math.exp(-0.5);
  // Bug: exponent -dim/2 uses integer division, so dim=1 -> 0, missing 1/sqrt(2pi)
  Assert.assertEquals("Univariate density at x=1", expected, actual, DELTA_DENSITY);
  }
  @Test
  public void testUnivariateDensityAtMean() {
  double[] means = {0.0};
  double[][] cov = {{1.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double actual = dist.density(new double[]{0.0});
  // At mean: density = 1/sqrt(2*pi)
  double expected = 1.0 / Math.sqrt(2.0
  * Math.PI);
  Assert.assertEquals("Univariate density at mean", expected, actual, DELTA_DENSITY);
  }
  @Test
  public void testBivariateIdentityDensity() {
  double[] means = {0.0, 0.0};
  double[][] cov = {{1.0, 0.0}, {0.0, 1.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double actual = dist.density(new double[]{0.5, -0.5});
  // dim=2, correct exponent: -2/2 = -1 (ok), det=1
  double expected = 1.0 / (2.0
  * Math.PI) * Math.exp(-0.5 * (0.25 + 0.25));
  Assert.assertEquals("Bivariate identity density at (0.5,-0.5)", expected, actual, DELTA_DENSITY);
  }
  @Test
  public void testBivariateDiagonalDensity() {
  double[] means = {0.0, 0.0};
  double[][] cov = {{2.0, 0.0}, {0.0, 3.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double actual = dist.density(new double[]{1.0, 2.0});
  // det = 6, sqrt(det) = qrt(6)
  double det = 6.0;
  double prefactor = 1.0 / (2.0
  * Math.PI * Math.sqrt(det));
  // x' Sigma^{-1} x = 1/2 + 4/3 = 1/2 + 4/3 = (3+8)/6 = 11/6 → ~1.8333333
  double quadForm = 1.0 / 2.0 + 4.0 / 3.0;
  double expected = prefactor
  * Math.exp(-0.5 * quadForm);
  Assert.assertEquals("Bivariate diagonal density at (1,2)", expected, actual, DELTA_DENSITY);
  }
  @Test
  public void testFullCovDensityAtOrigin() {
  double[] means = {0.0, 0.0};
  double[][] cov = {{1.0, 0.5}, {0.5, 1.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double actual = dist.density(new double[]{0.0, 0.0});
  // det = 1 - 0.25 = 0.75
  double det = 0.75;
  double expected = 1.0 / (2.0
  * Math.PI * Math.sqrt(det));
  Assert.assertEquals("Full cov density at origin", expected, actual, DELTA_DENSITY);
  }
  @Test
  public void testTrivariateDensityBug() {
  // dim = 3 (odd), bug: -3/2 = -1 instead of -1.5
  double[] means = {0.0, 0.0, 0.0};
  double[][] cov = {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double actual = dist.density(new double[]{1.0, 0.0, 0.0});
  // Correct: (2pi)^{-3/2}
  * exp(-0.5)
  double expected = Math.pow(2.0
  * Math.PI, -1.5) * Math.exp(-0.5);
  // Bug: (2pi)^{-1}
  * exp(-0.5)
  Assert.assertEquals("Trivariate density at (1,0,0)", expected, actual, DELTA_DENSITY);
  }
  @Test
  public void testGetStandardDeviations() {
  double[] means = {0.0, 0.0};
  double[][] cov = {{4.0, 0.0}, {0.0, 9.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double[] stds = dist.getStandardDeviations();
  Assert.assertEquals("std[0]", 2.0, stds[0], EPS);
  Assert.assertEquals("std[1]", 3.0, stds[1], EPS);
  }
  @Test(expected = DimensionMismatchException.class)
  public void testConstructorDimensionMismatch() {
  double[] means = {0.0, 0.0};
  double[][] cov = {{1.0, 0.0}, {0.0, 1.0}, {0.0, 0.0}}; // 3 x 2
  new MultivariateNormalDistribution(means, cov);
  }
  @Test(expected = DimensionMismatchException.class)
  public void testDensityDimensionMismatch() {
  double[] means = {0.0, 0.0};
  double[][] cov = {{1.0, 0.0}, {0.0, 1.0}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  dist.density(new double[]{1.0}); // only 1 value, not 2
  }
  @Test
  public void testSampleStatistics() {
  double[] means = {1.0, -1.0};
  double[][] cov = {{2.0, 0.5}, {0.5, 1.0}};
  // Fixed seed for reproducibility
  RandomGenerator rng = new Well19937c(12345678L);
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(rng, means, cov);
  int n = 20000;
  double sumX = 0, sumY = 0;
  double sumXX = 0, sumYY = 0, sumXY = 0;
  for (int i = 0; i < n; i++) {
      double[] s = dist.sample();
      sumX += s[0];
      sumY += s[1];
      sumXX += s[0]
  * s[0];
      sumYY += s[1]
  * s[1];
      sumXY += s[0]
  * s[1];
  }
  double meanX = sumX / n;
  double meanY = sumY / n;
  double varX = sumXX / n - meanX
  * meanX;
  double varY = sumYY / n - meanY
  * meanY;
  double covXY = sumXY / n - meanX
  * meanY;
  Assert.assertEquals("Sample mean X", 1.0, meanX, DELTA_STAT);
  Assert.assertEquals("Sample mean Y", -1.0, meanY, DELTA_STAT);
  Assert.assertEquals("Sample var X", 2.0, varX, DELTA_STAT);
  Assert.assertEquals("Sample var Y", 1.0, varY, DELTA_STAT);
  Assert.assertEquals("Sample cov XY", 0.5, covXY, DELTA_STAT);
  }
  @Test
  public void testNearZeroVariance() {
  // Very small but positive eigenvalue; should construct without exception
  double[] means = {0.0};
  double[][] cov = {{1e-10}};
  MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov);
  double d = dist.density(new double[]{0.0});
  // Should be finite (large) but not NaN/Inf
  Assert.assertTrue("Density near zero variance is finite", Double.isFinite(d));
  }
  @Test(expected = NonPositiveDefiniteMatrixException.class)
  public void testNonPositiveDefiniteCovariance() {
  double[] means = {0.0, 0.0};
  // Eigenvalues: 3 and -1 → not positive definite
  double[][] cov = {{1.0, 2.0}, {2.0, 1.0}};
  new MultivariateNormalDistribution(means, cov);
  }

}
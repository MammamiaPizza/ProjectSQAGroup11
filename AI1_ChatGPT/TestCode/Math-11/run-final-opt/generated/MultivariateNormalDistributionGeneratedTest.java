package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.random.Well19937c;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MultivariateNormalDistributionGeneratedTest {

    @Test
    public void testUnivariateDensityAtMeanIncludesNormalizingConstant() {
        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(new double[] { 0.0 },
                                                   new double[][] { { 1.0 } });

        assertEquals(1.0 / Math.sqrt(2.0 * Math.PI),
                     distribution.density(new double[] { 0.0 }),
                     1.0e-15);
    }

    @Test
    public void testUnivariateDensityAwayFromMean() {
        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(new double[] { 2.0 },
                                                   new double[][] { { 4.0 } });

        double expected = Math.exp(-0.5 * 0.25) / Math.sqrt(2.0 * Math.PI * 4.0);
        assertEquals(expected, distribution.density(new double[] { 3.0 }), 1.0e-15);
    }

    @Test
    public void testThreeDimensionalDensityAtMeanUsesHalfDimensionExponent() {
        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(
                        new double[] { 0.0, 0.0, 0.0 },
                        new double[][] {
                            { 1.0, 0.0, 0.0 },
                            { 0.0, 4.0, 0.0 },
                            { 0.0, 0.0, 9.0 }
                        });

        double expected = 1.0 / (6.0 * Math.pow(2.0 * Math.PI, 1.5));
        assertEquals(expected,
                     distribution.density(new double[] { 0.0, 0.0, 0.0 }),
                     1.0e-15);
    }

    @Test
    public void testBivariateDensityForDiagonalCovariance() {
        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(
                        new double[] { 1.0, -1.0 },
                        new double[][] {
                            { 4.0, 0.0 },
                            { 0.0, 9.0 }
                        });

        double expected = Math.exp(-0.5 * (0.25 + 1.0 / 9.0)) /
                          (2.0 * Math.PI * 6.0);
        assertEquals(expected,
                     distribution.density(new double[] { 2.0, 0.0 }),
                     1.0e-15);
    }

    @Test
    public void testStandardDeviationsAreSquareRootsOfCovarianceDiagonal() {
        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(
                        new double[] { 1.0, 2.0, 3.0 },
                        new double[][] {
                            { 4.0, 0.5, 0.0 },
                            { 0.5, 9.0, 1.0 },
                            { 0.0, 1.0, 16.0 }
                        });

        assertArrayEquals(new double[] { 2.0, 3.0, 4.0 },
                          distribution.getStandardDeviations(),
                          0.0);
    }

    @Test
    public void testUnivariateSampleUsesMeanAndCovarianceScale() {
        long seed = 123456789L;
        Well19937c expectedRandom = new Well19937c(seed);
        double gaussian = expectedRandom.nextGaussian();

        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(
                        new Well19937c(seed),
                        new double[] { 5.0 },
                        new double[][] { { 4.0 } });

        double[] sample = distribution.sample();

        assertEquals(1, sample.length);
        assertEquals(5.0 + 2.0 * gaussian, sample[0], 1.0e-14);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testDensityRejectsWrongPointDimension() {
        MultivariateNormalDistribution distribution =
                new MultivariateNormalDistribution(
                        new double[] { 0.0, 0.0 },
                        new double[][] {
                            { 1.0, 0.0 },
                            { 0.0, 1.0 }
                        });

        distribution.density(new double[] { 0.0 });
    }

    @Test(expected = DimensionMismatchException.class)
    public void testConstructorRejectsCovarianceWithWrongRowCount() {
        new MultivariateNormalDistribution(
                new double[] { 0.0, 0.0 },
                new double[][] { { 1.0, 0.0 } });
    }

    @Test(expected = DimensionMismatchException.class)
    public void testConstructorRejectsNonSquareCovarianceRow() {
        new MultivariateNormalDistribution(
                new double[] { 0.0, 0.0 },
                new double[][] {
                    { 1.0, 0.0 },
                    { 0.0 }
                });
    }

    @Test(expected = NonPositiveDefiniteMatrixException.class)
    public void testConstructorRejectsNegativeCovarianceEigenvalue() {
        new MultivariateNormalDistribution(
                new double[] { 0.0 },
                new double[][] { { -1.0 } });
    }
}

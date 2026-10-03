package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NormalDistributionImplGeneratedTest {

    @Test
    public void inverseCumulativeProbabilityHandlesExtremePositiveMeanLowerTail()
            throws MathException {
        NormalDistributionImpl distribution =
                new NormalDistributionImpl(1.0e20, 1.0e10);

        double probability = 0.0;
        double quantile = -Double.MAX_VALUE;

        assertTrue(quantile < distribution.getMean());
        assertFalse(Double.isInfinite(quantile));
        assertFalse(Double.isNaN(quantile));
        assertEquals(probability, distribution.cumulativeProbability(quantile), 0.0);
    }

    @Test
    public void inverseCumulativeProbabilityHandlesExtremeNegativeMeanUpperTail()
            throws MathException {
        NormalDistributionImpl distribution =
                new NormalDistributionImpl(-1.0e20, 1.0e10);

        double probability = 1.0;
        double quantile = Double.MAX_VALUE;

        assertTrue(quantile > distribution.getMean());
        assertFalse(Double.isInfinite(quantile));
        assertFalse(Double.isNaN(quantile));
        assertEquals(probability, distribution.cumulativeProbability(quantile), 0.0);
    }

    @Test
    public void inverseAndCumulativeProbabilityAreConsistentForRepresentativeValues()
            throws MathException {
        NormalDistributionImpl distribution = new NormalDistributionImpl(12.5, 3.75);
        double[] probabilities = {0.01, 0.25, 0.5, 0.75, 0.99};
        double[] standardDeviations = {
            -2.3263478740408408,
            -0.6744897501960817,
            0.0,
            0.6744897501960817,
            2.3263478740408408
        };

        for (int i = 0; i < probabilities.length; i++) {
            double quantile = distribution.getMean()
                    + standardDeviations[i] * distribution.getStandardDeviation();
            assertEquals(probabilities[i], distribution.cumulativeProbability(quantile), 1.0e-8);
        }
    }

    @Test
    public void inverseCumulativeProbabilityReturnsInfiniteEndpoints()
            throws MathException {
        NormalDistributionImpl distribution = new NormalDistributionImpl();

        double lower = distribution.inverseCumulativeProbability(0.0);
        double upper = distribution.inverseCumulativeProbability(1.0);

        assertTrue(Double.isInfinite(lower));
        assertTrue(lower < 0.0);
        assertTrue(Double.isInfinite(upper));
        assertTrue(upper > 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void inverseCumulativeProbabilityRejectsProbabilityBelowZero()
            throws MathException {
        new NormalDistributionImpl().inverseCumulativeProbability(-0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void inverseCumulativeProbabilityRejectsProbabilityAboveOne()
            throws MathException {
        new NormalDistributionImpl().inverseCumulativeProbability(1.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standardDeviationMustBePositive() {
        new NormalDistributionImpl(0.0, 0.0);
    }

    @Test
    public void cumulativeProbabilityAtMeanIsOneHalf() throws MathException {
        NormalDistributionImpl distribution = new NormalDistributionImpl(-37.0, 2.5);

        assertEquals(0.5, distribution.cumulativeProbability(distribution.getMean()), 0.0);
    }
}

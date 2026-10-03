package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FDistributionImplRegressionTest {

    private static class ExposedFDistribution extends FDistributionImpl {
        ExposedFDistribution(double numeratorDegreesOfFreedom,
                             double denominatorDegreesOfFreedom) {
            super(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom);
        }

        double initialDomain(double p) {
            return getInitialDomain(p);
        }

        double lowerBound(double p) {
            return getDomainLowerBound(p);
        }

        double upperBound(double p) {
            return getDomainUpperBound(p);
        }
    }

    @Test
    public void testSmallDegreeInitialDomainsAreInsideSolverBounds() {
        double[] degreesOfFreedom = {0.5, 1.0, 2.0};

        for (int i = 0; i < degreesOfFreedom.length; i++) {
            ExposedFDistribution distribution =
                    new ExposedFDistribution(degreesOfFreedom[i], degreesOfFreedom[i]);
            double initial = distribution.initialDomain(0.5);

            assertTrue("initial domain must not be below the lower bound",
                    initial >= distribution.lowerBound(0.5));
            assertTrue("initial domain must not exceed the upper bound",
                    initial <= distribution.upperBound(0.5));
        }
    }

    @Test
    public void testInverseCumulativeProbabilityForSmallDegreesOfFreedom()
            throws MathException {
        double[] degreesOfFreedom = {0.5, 1.0, 2.0};

        for (int i = 0; i < degreesOfFreedom.length; i++) {
            FDistributionImpl distribution =
                    new FDistributionImpl(degreesOfFreedom[i], degreesOfFreedom[i]);
            double value = distribution.inverseCumulativeProbability(0.5);

            assertTrue("inverse cumulative probability must be positive", value > 0.0);
            assertTrue("inverse cumulative probability must be finite",
                    !Double.isInfinite(value));
            assertEquals(0.5, distribution.cumulativeProbability(value), 1.0e-6);
        }
    }

    @Test
    public void testInverseCumulativeProbabilityRoundTripForOrdinaryDegreesOfFreedom()
            throws MathException {
        FDistributionImpl distribution = new FDistributionImpl(5.0, 10.0);

        double value = distribution.inverseCumulativeProbability(0.75);

        assertTrue(value > 0.0);
        assertEquals(0.75, distribution.cumulativeProbability(value), 1.0e-6);
    }

    @Test
    public void testInverseCumulativeProbabilityEndpoints() throws MathException {
        FDistributionImpl distribution = new FDistributionImpl(1.0, 1.0);

        assertEquals(0.0, distribution.inverseCumulativeProbability(0.0), 0.0);
        assertTrue(Double.isInfinite(distribution.inverseCumulativeProbability(1.0)));
        assertTrue(distribution.inverseCumulativeProbability(1.0) > 0.0);
    }

    @Test
    public void testCumulativeProbabilityIsZeroAtAndBelowZero() throws MathException {
        FDistributionImpl distribution = new FDistributionImpl(3.0, 4.0);

        assertEquals(0.0, distribution.cumulativeProbability(0.0), 0.0);
        assertEquals(0.0, distribution.cumulativeProbability(-2.0), 0.0);
    }

    @Test
    public void testInverseCumulativeProbabilityRejectsInvalidProbabilities()
            throws MathException {
        FDistributionImpl distribution = new FDistributionImpl(3.0, 4.0);

        try {
            distribution.inverseCumulativeProbability(-0.01);
            fail("negative probability must be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }

        try {
            distribution.inverseCumulativeProbability(1.01);
            fail("probability greater than one must be rejected");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNonPositiveNumeratorDegreesOfFreedom() {
        new FDistributionImpl(0.0, 1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNonPositiveDenominatorDegreesOfFreedom() {
        new FDistributionImpl(1.0, -1.0);
    }
}
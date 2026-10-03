@org.junit.Test
public void testConvenienceConstructorsRetainMean() {
    org.apache.commons.math.distribution.PoissonDistributionImpl epsilonDistribution =
        new org.apache.commons.math.distribution.PoissonDistributionImpl(2.5, 1.0e-9);
    org.apache.commons.math.distribution.PoissonDistributionImpl iterationDistribution =
        new org.apache.commons.math.distribution.PoissonDistributionImpl(7.5, 100);

    org.junit.Assert.assertEquals(2.5, epsilonDistribution.getMean(), 0.0);
    org.junit.Assert.assertEquals(7.5, iterationDistribution.getMean(), 0.0);
}

@org.junit.Test
public void testProbabilityAtBoundaryAndPositiveValues() {
    org.apache.commons.math.distribution.PoissonDistributionImpl distribution =
        new org.apache.commons.math.distribution.PoissonDistributionImpl(2.0);

    org.junit.Assert.assertEquals(0.0, distribution.probability(-1), 0.0);
    org.junit.Assert.assertEquals(0.0, distribution.probability(Integer.MAX_VALUE), 0.0);
    org.junit.Assert.assertEquals(java.lang.Math.exp(-2.0), distribution.probability(0), 1.0e-15);
    org.junit.Assert.assertEquals(2.0 * java.lang.Math.exp(-2.0), distribution.probability(1), 1.0e-15);
}

@org.junit.Test
public void testCumulativeProbabilityAtBoundsAndZero() throws org.apache.commons.math.MathException {
    org.apache.commons.math.distribution.PoissonDistributionImpl distribution =
        new org.apache.commons.math.distribution.PoissonDistributionImpl(2.0);

    org.junit.Assert.assertEquals(0.0, distribution.cumulativeProbability(-1), 0.0);
    org.junit.Assert.assertEquals(1.0, distribution.cumulativeProbability(Integer.MAX_VALUE), 0.0);
    org.junit.Assert.assertEquals(java.lang.Math.exp(-2.0), distribution.cumulativeProbability(0), 1.0e-15);
}

@org.junit.Test
public void testNormalApproximationAndDomainBounds() throws org.apache.commons.math.MathException {
    PoissonDistributionAccess distribution = new PoissonDistributionAccess(0.5);

    org.junit.Assert.assertEquals(0.5, distribution.normalApproximateProbability(0), 1.0e-15);
    org.junit.Assert.assertEquals(0, distribution.lowerBound(0.25));
    org.junit.Assert.assertEquals(Integer.MAX_VALUE, distribution.upperBound(0.25));
}

private static class PoissonDistributionAccess
    extends org.apache.commons.math.distribution.PoissonDistributionImpl {
    PoissonDistributionAccess(double mean) {
        super(mean);
    }

    int lowerBound(double probability) {
        return getDomainLowerBound(probability);
    }

    int upperBound(double probability) {
        return getDomainUpperBound(probability);
    }
}
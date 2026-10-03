@Test
public void domainBoundsAndInitialDomainBracketProbabilitiesOnBothSidesOfMean()
        throws org.apache.commons.math.MathException {
    ExposedNormalDistribution distribution = new ExposedNormalDistribution(12.0, 3.0);
    double[] probabilities = {0.25, 0.75};

    for (double probability : probabilities) {
        double lower = distribution.domainLowerBound(probability);
        double upper = distribution.domainUpperBound(probability);
        double initial = distribution.initialDomain(probability);

        org.junit.Assert.assertTrue(lower < upper);
        org.junit.Assert.assertTrue(
                distribution.cumulativeProbability(lower) < probability);
        org.junit.Assert.assertTrue(
                distribution.cumulativeProbability(upper) > probability);
        org.junit.Assert.assertTrue(initial >= lower);
        org.junit.Assert.assertTrue(initial <= upper);
    }
}

private static class ExposedNormalDistribution
        extends org.apache.commons.math.distribution.NormalDistributionImpl {
    ExposedNormalDistribution(double mean, double standardDeviation) {
        super(mean, standardDeviation);
    }

    double domainLowerBound(double probability) {
        return getDomainLowerBound(probability);
    }

    double domainUpperBound(double probability) {
        return getDomainUpperBound(probability);
    }

    double initialDomain(double probability) {
        return getInitialDomain(probability);
    }
}
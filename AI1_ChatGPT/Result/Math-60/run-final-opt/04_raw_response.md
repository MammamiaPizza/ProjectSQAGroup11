@org.junit.Test
public void densityAndAccessorsUseConfiguredParameters() {
    org.apache.commons.math.distribution.NormalDistributionImpl distribution =
            new org.apache.commons.math.distribution.NormalDistributionImpl(2.0, 3.0);

    org.junit.Assert.assertEquals(2.0, distribution.getMean(), 0.0);
    org.junit.Assert.assertEquals(3.0, distribution.getStandardDeviation(), 0.0);
    org.junit.Assert.assertEquals(0.1329807601338109, distribution.density(2.0), 1.0e-15);
    org.junit.Assert.assertEquals(0.0806569081730478, distribution.density(5.0), 1.0e-15);
}

@org.junit.Test
public void inverseCumulativeProbabilityReturnsKnownQuartiles()
        throws org.apache.commons.math.MathException {
    org.apache.commons.math.distribution.NormalDistributionImpl distribution =
            new org.apache.commons.math.distribution.NormalDistributionImpl();

    org.junit.Assert.assertEquals(-0.6744897501960817,
            distribution.inverseCumulativeProbability(0.25), 1.0e-8);
    org.junit.Assert.assertEquals(0.0,
            distribution.inverseCumulativeProbability(0.5), 1.0e-12);
    org.junit.Assert.assertEquals(0.6744897501960817,
            distribution.inverseCumulativeProbability(0.75), 1.0e-8);
}

@org.junit.Test
public void inverseSolverDomainBoundsBracketBothHalvesOfDistribution() {
    ExposedNormalDistribution distribution =
            new ExposedNormalDistribution(2.0, 3.0, 1.0e-7);

    org.junit.Assert.assertEquals(-Double.MAX_VALUE, distribution.lowerBound(0.25), 0.0);
    org.junit.Assert.assertEquals(2.0, distribution.upperBound(0.25), 0.0);
    org.junit.Assert.assertEquals(2.0, distribution.lowerBound(0.75), 0.0);
    org.junit.Assert.assertEquals(Double.MAX_VALUE, distribution.upperBound(0.75), 0.0);
    org.junit.Assert.assertEquals(2.0, distribution.initialDomain(0.25), 0.0);
    org.junit.Assert.assertEquals(2.0, distribution.initialDomain(0.75), 0.0);
    org.junit.Assert.assertEquals(1.0e-7, distribution.solverAccuracy(), 0.0);
}

private static final class ExposedNormalDistribution
        extends org.apache.commons.math.distribution.NormalDistributionImpl {
    ExposedNormalDistribution(double mean, double standardDeviation, double inverseCumAccuracy) {
        super(mean, standardDeviation, inverseCumAccuracy);
    }

    double lowerBound(double p) {
        return getDomainLowerBound(p);
    }

    double upperBound(double p) {
        return getDomainUpperBound(p);
    }

    double initialDomain(double p) {
        return getInitialDomain(p);
    }

    double solverAccuracy() {
        return getSolverAbsoluteAccuracy();
    }
}
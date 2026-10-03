@org.junit.Test(expected = org.apache.commons.math3.exception.NotStrictlyPositiveException.class)
public void fDistributionRejectsNonPositiveDenominatorDegreesOfFreedom() {
    new org.apache.commons.math3.distribution.FDistribution(1.0, 0.0);
}

@org.junit.Test
public void fDistributionComputesPositiveDensityCdfAndVariance() {
    org.apache.commons.math3.distribution.FDistribution distribution =
            new org.apache.commons.math3.distribution.FDistribution(2.0, 4.0);

    org.junit.Assert.assertEquals(8.0 / 27.0, distribution.density(1.0), 1e-14);
    org.junit.Assert.assertEquals(5.0 / 9.0, distribution.cumulativeProbability(1.0), 1e-14);

    org.apache.commons.math3.distribution.FDistribution finiteVariance =
            new org.apache.commons.math3.distribution.FDistribution(6.0, 8.0);
    org.junit.Assert.assertEquals(4.0 / 3.0, finiteVariance.getNumericalMean(), 1e-14);
    org.junit.Assert.assertEquals(16.0 / 9.0, finiteVariance.getNumericalVariance(), 1e-14);
    org.junit.Assert.assertTrue(Double.isNaN(distribution.getNumericalVariance()));
}

@org.junit.Test
public void uniformDistributionLowerSupportBoundIsInclusive() {
    org.apache.commons.math3.distribution.UniformRealDistribution distribution =
            new org.apache.commons.math3.distribution.UniformRealDistribution(-2.0, 6.0);

    org.junit.Assert.assertTrue(distribution.isSupportLowerBoundInclusive());
    org.junit.Assert.assertEquals(-2.0, distribution.getSupportLowerBound(), 0.0);
    org.junit.Assert.assertEquals(0.125, distribution.density(-2.0), 0.0);
    org.junit.Assert.assertEquals(0.0, distribution.density(-2.1), 0.0);
    org.junit.Assert.assertEquals(0.0, distribution.cumulativeProbability(-2.0), 0.0);
    org.junit.Assert.assertEquals(0.5, distribution.cumulativeProbability(2.0), 0.0);
}

@org.junit.Test
public void uniformDistributionReportsMomentsAndConnectedSupport() {
    org.apache.commons.math3.distribution.UniformRealDistribution distribution =
            new org.apache.commons.math3.distribution.UniformRealDistribution(-2.0, 6.0);

    org.junit.Assert.assertEquals(6.0, distribution.getSupportUpperBound(), 0.0);
    org.junit.Assert.assertEquals(2.0, distribution.getNumericalMean(), 0.0);
    org.junit.Assert.assertEquals(16.0 / 3.0, distribution.getNumericalVariance(), 0.0);
    org.junit.Assert.assertTrue(distribution.isSupportConnected());
}
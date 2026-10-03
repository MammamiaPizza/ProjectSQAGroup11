@org.junit.Test
public void testGetCovariancesReturnsDefensiveCopy() {
    org.apache.commons.math3.distribution.MultivariateNormalDistribution distribution =
        new org.apache.commons.math3.distribution.MultivariateNormalDistribution(
            new double[] { 1.0, 2.0 },
            new double[][] { { 4.0, 1.0 }, { 1.0, 9.0 } });

    org.apache.commons.math3.linear.RealMatrix covariances = distribution.getCovariances();
    covariances.setEntry(0, 0, -1.0);

    org.junit.Assert.assertEquals(4.0, distribution.getCovariances().getEntry(0, 0), 0.0);
}
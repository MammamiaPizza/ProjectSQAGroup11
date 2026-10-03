@Test
public void testIsSupportConnected() {
    HypergeometricDistribution d = new HypergeometricDistribution(10, 5, 3);
    org.junit.Assert.assertTrue(d.isSupportConnected());
}

@Test
public void testSupportLowerBoundNeverNegativeAndFormula() {
    // MATH-1021: sample=-50. Verify lower bound is non-negative and matches formula.
    HypergeometricDistribution d = new HypergeometricDistribution(1000, 400, 500);
    int lb = d.getSupportLowerBound();
    org.junit.Assert.assertTrue("Lower bound must be non-negative", lb >= 0);
    int expected = Math.max(0, d.getSampleSize() + d.getNumberOfSuccesses() -
d.getPopulationSize());
    org.junit.Assert.assertEquals("Lower bound must equal max(0, k+m-N)", expected, lb);
}
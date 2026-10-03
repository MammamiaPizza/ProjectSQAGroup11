@Test
public void testNumericalVarianceIsReturnedConsistentlyAfterCalculation() {
    HypergeometricDistribution distribution = new HypergeometricDistribution(20, 7, 5);

    assertEquals(273.0 / 304.0, distribution.getNumericalVariance(), EPS);
    assertEquals(273.0 / 304.0, distribution.getNumericalVariance(), EPS);
}
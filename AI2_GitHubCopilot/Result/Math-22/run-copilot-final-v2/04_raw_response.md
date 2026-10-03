@Test(expected = org.apache.commons.math3.exception.NotStrictlyPositiveException.class)
public void testConstructorNumeratorDegreesOfFreedomNotPositive() {
    new FDistribution(0.0, 5.0);
}

@Test(expected = org.apache.commons.math3.exception.NotStrictlyPositiveException.class)
public void testConstructorDenominatorDegreesOfFreedomNotPositive() {
    new FDistribution(5.0, 0.0);
}

@Test(expected = org.apache.commons.math3.exception.NumberIsTooLargeException.class)
public void testConstructorLowerNotLessThanUpper() {
    new UniformRealDistribution(5.0, 5.0);
}

@Test
public void testGetNumericalMean() {
    UniformRealDistribution dist =new UniformRealDistribution(1.5, 7.5);
    assertEquals(4.5, dist.getNumericalMean(), 1e-9);
}
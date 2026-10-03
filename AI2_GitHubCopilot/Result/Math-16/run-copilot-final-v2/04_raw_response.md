@Test
public void testAcosCoverage() {
    final double delta = 1e-12;
    assertEquals(Math.acos(0.25), FastMath.acos(0.25), delta);
    assertEquals(Math.acos(0.75), FastMath.acos(0.75), delta);
    assertEquals(Math.acos(-0.3), FastMath.acos(-0.3), delta);
}

@Test
public void testUlpInfinite() {
    assertEquals(Double.POSITIVE_INFINITY, FastMath.ulp(Double.POSITIVE_INFINITY), 0.0);
    assertEquals(Double.POSITIVE_INFINITY, FastMath.ulp(Double.NEGATIVE_INFINITY), 0.0);
}

@Test
public void testExpLargeNegativeReturnsZero() {
    assertEquals(0.0, FastMath.exp(-800.0), 0.0);
    assertEquals(0.0, FastMath.exp(-1000.0), 0.0);
}

@Test
public void testPowUnderflowForBaseBetweenZeroAndOneLargeExponent() {
    assertEquals(0.0, FastMath.pow(0.5, 1100.0), 0.0);
    assertEquals(0.0, FastMath.pow(0.1, 600.0), 0.0);
}
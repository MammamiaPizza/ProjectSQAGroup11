@Test
public void testAddAndCheck() {
    assertEquals(3, MathUtils.addAndCheck(1, 2));
    assertEquals(0, MathUtils.addAndCheck(-1, 1));
    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        fail("Integer overflow should have thrown");
    } catch (org.apache.commons.math.MathRuntimeException e) {}
    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        fail("Integer underflow should have thrown");
    } catch (org.apache.commons.math.MathRuntimeException e) {}
    assertEquals(3L, MathUtils.addAndCheck(1L, 2L));
    assertEquals(0L, MathUtils.addAndCheck(Long.MIN_VALUE, Long.MAX_VALUE));
    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        fail("Long overflow should have thrown");
    } catch (org.apache.commons.math.MathRuntimeException e) {}
    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        fail("Long underflow should have thrown");
    } catch (org.apache.commons.math.MathRuntimeException e) {}
    assertEquals(-3L, MathUtils.addAndCheck(-1L, -2L));
    assertEquals(0L, MathUtils.addAndCheck(-5L, 5L));
}

@Test
public void testBinomialCoefficientBoundary() {
    assertEquals(1L, MathUtils.binomialCoefficient(0, 0));
    assertEquals(1L, MathUtils.binomialCoefficient(5, 5));
    assertEquals(1L, MathUtils.binomialCoefficient(5, 0));
    assertEquals(5L, MathUtils.binomialCoefficient(5, 1));
    assertEquals(5L, MathUtils.binomialCoefficient(5, 4));
}

@Test
public void testEqualsMaxUlpsScalar() {
    assertTrue(MathUtils.equals(1.0, 1.0, 0));
    assertTrue(MathUtils.equals(1.0, 1.0 + Math.ulp(1.0), 1));
    assertTrue(MathUtils.equals(0.0, -0.0, 1));
    assertFalse(MathUtils.equals(0.0, 1.0, 0));
    assertFalse(MathUtils.equals(Double.NaN, Double.NaN, 1));
    assertFalse(MathUtils.equals(Double.NaN, 0.0, 100));
    assertFalse(MathUtils.equals(0.0, Double.NaN, 100));
}

@Test
public void testEqualsIncludingNaNMaxUlpsScalar() {
    assertTrue(MathUtils.equalsIncludingNaN(Double.NaN, Double.NaN, 1));
    assertFalse(MathUtils.equalsIncludingNaN(Double.NaN, 0.0, 100));
    assertTrue(MathUtils.equalsIncludingNaN(0.0, -0.0, 1));
    assertTrue(MathUtils.equalsIncludingNaN(1.0, 1.0, 0));
}
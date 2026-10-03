@Test
public void testAddAndCheckIntNormal() {
    assertEquals(5, MathUtils.addAndCheck(2, 3));
}

@Test
public void testAddAndCheckIntOverflowMax() {
    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        fail("Expected ArithmeticException for positive overflow");
    } catch (ArithmeticException e) {
        // expected
    }
}

@Test
public void testAddAndCheckLongBoundaries() {
    // positive overflow
    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        fail("Expected ArithmeticException for positive long overflow");
    } catch (ArithmeticException e) {
        // expected
    }
    // negative overflow
    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        fail("Expected ArithmeticException for negative long overflow");
    } catch (ArithmeticException e) {
        // expected
    }
    // no overflow
    assertEquals(5L, MathUtils.addAndCheck(2L, 3L));
}

@Test
public void testBinomialCoefficientBasic() {
    assertEquals(1L, MathUtils.binomialCoefficient(5, 5));
    assertEquals(1L, MathUtils.binomialCoefficient(5, 0));
    assertEquals(5L, MathUtils.binomialCoefficient(5, 1));
    assertEquals(5L, MathUtils.binomialCoefficient(5, 4));
    // ensure a non-trivial value computes without exception
    assertTrue(MathUtils.binomialCoefficient(5, 2) > 0);
}
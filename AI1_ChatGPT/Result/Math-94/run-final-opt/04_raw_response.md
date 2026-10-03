@org.junit.Test
public void testAddAndCheckIntBoundariesAndOverflow() {
    org.junit.Assert.assertEquals(Integer.MAX_VALUE,
            MathUtils.addAndCheck(Integer.MAX_VALUE, 0));
    org.junit.Assert.assertEquals(Integer.MIN_VALUE,
            MathUtils.addAndCheck(Integer.MIN_VALUE, 0));

    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }

    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }
}

@org.junit.Test
public void testAddAndCheckLongSignsAndOverflow() {
    org.junit.Assert.assertEquals(7L, MathUtils.addAndCheck(5L, 2L));
    org.junit.Assert.assertEquals(-7L, MathUtils.addAndCheck(-5L, -2L));
    org.junit.Assert.assertEquals(-3L, MathUtils.addAndCheck(-5L, 2L));

    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }

    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }
}

@org.junit.Test
public void testBinomialCoefficientBoundaryAndInvalidArguments() {
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(8, 8));
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(8, 0));
    org.junit.Assert.assertEquals(10L, MathUtils.binomialCoefficient(5, 2));

    try {
        MathUtils.binomialCoefficient(2, 3);
        org.junit.Assert.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }

    try {
        MathUtils.binomialCoefficient(-1, -2);
        org.junit.Assert.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}
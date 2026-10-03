@org.junit.Test
public void testAddAndCheckIntReturnsSumAndRejectsOverflow() {
    org.junit.Assert.assertEquals(-1, org.apache.commons.math.util.MathUtils.addAndCheck(2, -3));

    try {
        org.apache.commons.math.util.MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }

    try {
        org.apache.commons.math.util.MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }
}

@org.junit.Test
public void testAddAndCheckLongHandlesSignsAndRejectsOverflow() {
    org.junit.Assert.assertEquals(3L, org.apache.commons.math.util.MathUtils.addAndCheck(7L, -4L));
    org.junit.Assert.assertEquals(-11L, org.apache.commons.math.util.MathUtils.addAndCheck(-5L, -6L));

    try {
        org.apache.commons.math.util.MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }

    try {
        org.apache.commons.math.util.MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
        // expected
    }
}

@org.junit.Test
public void testBinomialCoefficientValidatesArgumentsAndBaseCases() {
    org.junit.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.binomialCoefficient(5, 0));
    org.junit.Assert.assertEquals(1L, org.apache.commons.math.util.MathUtils.binomialCoefficient(5, 5));

    try {
        org.apache.commons.math.util.MathUtils.binomialCoefficient(3, 4);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }

    try {
        org.apache.commons.math.util.MathUtils.binomialCoefficient(-1, 0);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}
@org.junit.Test
public void testArrayEqualityHandlesNullReferences() {
    org.junit.Assert.assertTrue(MathUtils.equals((double[]) null, (double[]) null));
    org.junit.Assert.assertTrue(MathUtils.equalsIncludingNaN((double[]) null, (double[]) null));

    org.junit.Assert.assertFalse(MathUtils.equals((double[]) null, new double[0]));
    org.junit.Assert.assertFalse(MathUtils.equals(new double[0], (double[]) null));
    org.junit.Assert.assertFalse(MathUtils.equalsIncludingNaN((double[]) null, new double[0]));
    org.junit.Assert.assertFalse(MathUtils.equalsIncludingNaN(new double[0], (double[]) null));
}

@org.junit.Test
public void testAddAndCheckReturnsSumsAndRejectsOverflow() {
    org.junit.Assert.assertEquals(-1, MathUtils.addAndCheck(2, -3));
    org.junit.Assert.assertEquals(1L, MathUtils.addAndCheck(Long.MAX_VALUE, Long.MIN_VALUE));

    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }

    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }

    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }

    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("Expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }
}

@org.junit.Test
public void testBinomialCoefficientHandlesBaseAndSymmetricCases() {
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(7, 0));
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(7, 7));
    org.junit.Assert.assertEquals(7L, MathUtils.binomialCoefficient(7, 1));
    org.junit.Assert.assertEquals(35L, MathUtils.binomialCoefficient(7, 3));
    org.junit.Assert.assertEquals(35L, MathUtils.binomialCoefficient(7, 4));
}
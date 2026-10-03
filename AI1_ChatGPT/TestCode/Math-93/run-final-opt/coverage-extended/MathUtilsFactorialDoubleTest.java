package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MathUtilsFactorialDoubleTest {

    @Test
    public void testFactorialDoubleZeroIsOne() {
        assertEquals(1.0d, MathUtils.factorialDouble(0), 0.0d);
    }

    @Test
    public void testFactorialDoubleComputesSmallFactorial() {
        assertEquals(120.0d, MathUtils.factorialDouble(5), 0.0d);
    }

    @Test
    public void testFactorialDoubleSeventeenIsExact() {
        assertEquals(355687428096000.0d, MathUtils.factorialDouble(17), 0.0d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFactorialDoubleRejectsNegativeArguments() {
        MathUtils.factorialDouble(-1);
    }

@org.junit.Test
public void testFactorialSeventeenIsExact() {
    org.junit.Assert.assertEquals(355687428096000L, MathUtils.factorial(17));
}

@org.junit.Test
public void testAddAndCheckIntHandlesBoundsAndOverflow() {
    org.junit.Assert.assertEquals(-1, MathUtils.addAndCheck(Integer.MAX_VALUE, Integer.MIN_VALUE));

    try {
        MathUtils.addAndCheck(Integer.MAX_VALUE, 1);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }

    try {
        MathUtils.addAndCheck(Integer.MIN_VALUE, -1);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }
}

@org.junit.Test
public void testAddAndCheckLongHandlesSignsAndOverflow() {
    org.junit.Assert.assertEquals(-12L, MathUtils.addAndCheck(-5L, -7L));
    org.junit.Assert.assertEquals(-1L, MathUtils.addAndCheck(Long.MAX_VALUE, Long.MIN_VALUE));

    try {
        MathUtils.addAndCheck(Long.MAX_VALUE, 1L);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }

    try {
        MathUtils.addAndCheck(Long.MIN_VALUE, -1L);
        org.junit.Assert.fail("expected ArithmeticException");
    } catch (ArithmeticException expected) {
    }
}

@org.junit.Test
public void testBinomialCoefficientValidatesArgumentsAndIdentityCases() {
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(5, 0));
    org.junit.Assert.assertEquals(1L, MathUtils.binomialCoefficient(5, 5));

    try {
        MathUtils.binomialCoefficient(2, 3);
        org.junit.Assert.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }

    try {
        MathUtils.binomialCoefficient(-1, -1);
        org.junit.Assert.fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
}
}

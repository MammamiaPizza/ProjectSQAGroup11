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
}
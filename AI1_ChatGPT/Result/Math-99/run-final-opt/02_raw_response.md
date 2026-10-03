package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class MathUtilsOverflowTest {

    @Test
    public void testGcdNormalSignedAndZeroValues() {
        assertEquals(0, MathUtils.gcd(0, 0));
        assertEquals(6, MathUtils.gcd(0, -6));
        assertEquals(6, MathUtils.gcd(-54, 24));
        assertEquals(1, MathUtils.gcd(35, 64));
    }

    @Test
    public void testGcdMinimumValueAndZeroOverflowInEitherOrder() {
        assertGcdOverflow(Integer.MIN_VALUE, 0);
        assertGcdOverflow(0, Integer.MIN_VALUE);
    }

    @Test
    public void testGcdTwoMinimumValuesOverflows() {
        assertGcdOverflow(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    @Test
    public void testGcdMinimumValueWithRepresentableDivisors() {
        assertEquals(1, MathUtils.gcd(Integer.MIN_VALUE, 3));
        assertEquals(2, MathUtils.gcd(Integer.MIN_VALUE, 2));
        assertEquals(1, MathUtils.gcd(-3, Integer.MIN_VALUE));
    }

    @Test
    public void testLcmNormalSignedValues() {
        assertEquals(42, MathUtils.lcm(21, 6));
        assertEquals(21, MathUtils.lcm(-3, 7));
        assertEquals(12, MathUtils.lcm(-4, -6));
    }

    @Test
    public void testLcmWithZeroAndMinimumValueIsZero() {
        assertEquals(0, MathUtils.lcm(Integer.MIN_VALUE, 0));
        assertEquals(0, MathUtils.lcm(0, Integer.MIN_VALUE));
    }

    @Test
    public void testLcmMinimumValueAndUnitOverflows() {
        assertLcmOverflow(Integer.MIN_VALUE, 1);
        assertLcmOverflow(1, Integer.MIN_VALUE);
        assertLcmOverflow(Integer.MIN_VALUE, -1);
        assertLcmOverflow(-1, Integer.MIN_VALUE);
    }

    @Test
    public void testLcmProductOverflowThrowsArithmeticException() {
        assertLcmOverflow(Integer.MAX_VALUE, 2);
        assertLcmOverflow(46341, 46349);
    }

    private void assertGcdOverflow(int a, int b) {
        try {
            MathUtils.gcd(a, b);
            fail("Expected ArithmeticException for gcd(" + a + ", " + b + ")");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    private void assertLcmOverflow(int a, int b) {
        try {
            MathUtils.lcm(a, b);
            fail("Expected ArithmeticException for lcm(" + a + ", " + b + ")");
        } catch (ArithmeticException expected) {
            // expected
        }
    }
}
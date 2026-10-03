package org.apache.commons.math3.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FastMathMath905Test {

    @Test
    public void testMath905LargePositive() {
        final double argument = 1.0e20 * FastMath.PI;
        assertEquals(0.0, FastMath.sin(argument), 0.0);
    }

    @Test
    public void testMath905LargeNegative() {
        final double argument = -1.0e20 * FastMath.PI;
        assertEquals(0.0, FastMath.sin(argument), 0.0);
    }

    @Test
    public void testSinAtZeroPreservesZero() {
        assertEquals(0.0, FastMath.sin(0.0), 0.0);
        assertEquals(Double.doubleToRawLongBits(-0.0),
                     Double.doubleToRawLongBits(FastMath.sin(-0.0)));
    }

    @Test
    public void testSinInfinityIsNaN() {
        assertTrue(Double.isNaN(FastMath.sin(Double.POSITIVE_INFINITY)));
        assertTrue(Double.isNaN(FastMath.sin(Double.NEGATIVE_INFINITY)));
    }
}

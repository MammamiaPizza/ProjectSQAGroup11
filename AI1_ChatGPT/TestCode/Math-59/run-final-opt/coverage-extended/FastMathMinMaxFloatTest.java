package org.apache.commons.math.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FastMathMinMaxFloatTest {

    @Test
    public void testMaxSelectsPositiveValueWhenFirst() {
        assertEquals(50.0f, FastMath.max(50.0f, -50.0f), 0.0f);
    }

    @Test
    public void testMaxSelectsPositiveValueWhenSecond() {
        assertEquals(50.0f, FastMath.max(-50.0f, 50.0f), 0.0f);
    }

    @Test
    public void testMinSelectsNegativeValueInEitherOperandOrder() {
        assertEquals(-50.0f, FastMath.min(50.0f, -50.0f), 0.0f);
        assertEquals(-50.0f, FastMath.min(-50.0f, 50.0f), 0.0f);
    }

    @Test
    public void testMinAndMaxWithAdjacentFiniteValues() {
        float lower = 1.0f;
        float higher = Float.intBitsToFloat(Float.floatToRawIntBits(lower) + 1);

        assertEquals(lower, FastMath.min(lower, higher), 0.0f);
        assertEquals(lower, FastMath.min(higher, lower), 0.0f);
        assertEquals(higher, FastMath.max(lower, higher), 0.0f);
        assertEquals(higher, FastMath.max(higher, lower), 0.0f);
    }

    @Test
    public void testMinAndMaxAtFiniteExtremes() {
        assertEquals(Float.MAX_VALUE, FastMath.max(Float.MAX_VALUE, -Float.MAX_VALUE), 0.0f);
        assertEquals(Float.MAX_VALUE, FastMath.max(-Float.MAX_VALUE, Float.MAX_VALUE), 0.0f);
        assertEquals(-Float.MAX_VALUE, FastMath.min(Float.MAX_VALUE, -Float.MAX_VALUE), 0.0f);
        assertEquals(-Float.MAX_VALUE, FastMath.min(-Float.MAX_VALUE, Float.MAX_VALUE), 0.0f);
    }

    @Test
    public void testMinAndMaxWithSmallestMagnitudeFloatValues() {
        assertEquals(Float.MIN_VALUE, FastMath.max(Float.MIN_VALUE, -Float.MIN_VALUE), 0.0f);
        assertEquals(-Float.MIN_VALUE, FastMath.min(Float.MIN_VALUE, -Float.MIN_VALUE), 0.0f);
    }

    @Test
    public void testMinAndMaxPreserveEqualFiniteValue() {
        assertEquals(37.25f, FastMath.min(37.25f, 37.25f), 0.0f);
        assertEquals(37.25f, FastMath.max(37.25f, 37.25f), 0.0f);
    }

@org.junit.Test
public void testAcosSpecialValuesAndRegularArgument() {
    org.junit.Assert.assertTrue(Double.isNaN(FastMath.acos(Double.NaN)));
    org.junit.Assert.assertTrue(Double.isNaN(FastMath.acos(1.1)));
    org.junit.Assert.assertTrue(Double.isNaN(FastMath.acos(-1.1)));
    org.junit.Assert.assertEquals(Math.PI, FastMath.acos(-1.0), 0.0);
    org.junit.Assert.assertEquals(0.0, FastMath.acos(1.0), 0.0);
    org.junit.Assert.assertEquals(Math.PI / 2.0, FastMath.acos(0.0), 0.0);
    org.junit.Assert.assertEquals(Math.acos(0.5), FastMath.acos(0.5), 1.0e-14);
}

@org.junit.Test
public void testAbsForPositiveAndNegativePrimitiveValues() {
    org.junit.Assert.assertEquals(7, FastMath.abs(-7));
    org.junit.Assert.assertEquals(7, FastMath.abs(7));
    org.junit.Assert.assertEquals(9L, FastMath.abs(-9L));
    org.junit.Assert.assertEquals(9L, FastMath.abs(9L));
    org.junit.Assert.assertEquals(3.5f, FastMath.abs(-3.5f), 0.0f);
    org.junit.Assert.assertEquals(3.5f, FastMath.abs(3.5f), 0.0f);
    org.junit.Assert.assertEquals(2.25, FastMath.abs(-2.25), 0.0);
    org.junit.Assert.assertEquals(2.25, FastMath.abs(2.25), 0.0);
}
}

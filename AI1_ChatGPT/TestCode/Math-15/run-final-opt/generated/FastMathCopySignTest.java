package org.apache.commons.math3.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FastMathCopySignTest {

    @Test
    public void testCopySignDoubleUsesNegativeZeroSign() {
        assertEquals(Double.doubleToRawLongBits(-1.0),
                     Double.doubleToRawLongBits(FastMath.copySign(1.0, -0.0)));
    }

    @Test
    public void testCopySignDoublePreservesMagnitudeAndNonzeroSign() {
        assertEquals(Double.doubleToRawLongBits(3.5),
                     Double.doubleToRawLongBits(FastMath.copySign(-3.5, 2.0)));
        assertEquals(Double.doubleToRawLongBits(-3.5),
                     Double.doubleToRawLongBits(FastMath.copySign(3.5, -2.0)));
    }

    @Test
    public void testCopySignDoubleProducesCorrectSignedZeros() {
        assertEquals(Double.doubleToRawLongBits(-0.0),
                     Double.doubleToRawLongBits(FastMath.copySign(0.0, -1.0)));
        assertEquals(Double.doubleToRawLongBits(0.0),
                     Double.doubleToRawLongBits(FastMath.copySign(-0.0, 1.0)));
    }

    @Test
    public void testCopySignFloatUsesNegativeZeroSign() {
        assertEquals(Float.floatToRawIntBits(-1.0f),
                     Float.floatToRawIntBits(FastMath.copySign(1.0f, -0.0f)));
    }

    @Test
    public void testCopySignFloatPreservesMagnitudeAndNonzeroSign() {
        assertEquals(Float.floatToRawIntBits(3.5f),
                     Float.floatToRawIntBits(FastMath.copySign(-3.5f, 2.0f)));
        assertEquals(Float.floatToRawIntBits(-3.5f),
                     Float.floatToRawIntBits(FastMath.copySign(3.5f, -2.0f)));
    }

    @Test
    public void testCopySignFloatProducesCorrectSignedZeros() {
        assertEquals(Float.floatToRawIntBits(-0.0f),
                     Float.floatToRawIntBits(FastMath.copySign(0.0f, -1.0f)));
        assertEquals(Float.floatToRawIntBits(0.0f),
                     Float.floatToRawIntBits(FastMath.copySign(-0.0f, 1.0f)));
    }
}

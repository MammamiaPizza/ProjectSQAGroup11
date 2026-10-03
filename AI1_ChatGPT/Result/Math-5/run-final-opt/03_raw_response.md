package org.apache.commons.math3.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComplexReciprocalZeroTest {

    @Test
    public void reciprocalOfZeroIsNaN() {
        Complex result = Complex.ZERO.reciprocal();

        assertTrue(result.isInfinite());
        assertEquals(Complex.INF, result);
        assertFalse(result.isNaN());
    }

    @Test
    public void reciprocalOfNegativeRealZeroIsNaN() {
        Complex result = new Complex(-0.0, 0.0).reciprocal();

        assertTrue(result.isInfinite());
        assertEquals(Complex.INF, result);
    }

    @Test
    public void reciprocalOfNegativeImaginaryZeroIsNaN() {
        Complex result = new Complex(0.0, -0.0).reciprocal();

        assertTrue(result.isInfinite());
        assertEquals(Complex.INF, result);
    }

    @Test
    public void reciprocalOfFiniteNonZeroComplexUsesComplexInverse() {
        Complex result = new Complex(3.0, 4.0).reciprocal();

        assertEquals(0.12, result.getReal(), 0.0);
        assertEquals(-0.16, result.getImaginary(), 0.0);
        assertFalse(result.isNaN());
    }

    @Test
    public void reciprocalOfNaNIsNaN() {
        Complex result = new Complex(Double.NaN, 1.0).reciprocal();

        assertTrue(result.isNaN());
        assertEquals(Complex.NaN, result);
    }
}
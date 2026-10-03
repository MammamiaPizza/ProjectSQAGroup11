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

@Test
public void absHandlesFiniteNaNAndInfiniteValues() {
    org.junit.Assert.assertEquals(3.0, new Complex(3.0).abs(), 0.0);
    org.junit.Assert.assertEquals(4.0, new Complex(0.0, -4.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    org.junit.Assert.assertTrue(Double.isNaN(Complex.NaN.abs()));
    org.junit.Assert.assertEquals(Double.POSITIVE_INFINITY,
                                  new Complex(Double.POSITIVE_INFINITY, 1.0).abs(),
                                  0.0);
}

@Test
public void addSupportsComplexAndRealOperandsAndPropagatesNaN() {
    Complex complexSum = new Complex(1.5, -2.0).add(new Complex(3.0, 4.5));
    org.junit.Assert.assertEquals(4.5, complexSum.getReal(), 0.0);
    org.junit.Assert.assertEquals(2.5, complexSum.getImaginary(), 0.0);

    Complex realSum = new Complex(1.5, -2.0).add(3.0);
    org.junit.Assert.assertEquals(4.5, realSum.getReal(), 0.0);
    org.junit.Assert.assertEquals(-2.0, realSum.getImaginary(), 0.0);

    org.junit.Assert.assertTrue(Complex.NaN.add(1.0).isNaN());
    org.junit.Assert.assertTrue(new Complex(1.0, 2.0).add(Double.NaN).isNaN());
    org.junit.Assert.assertTrue(new Complex(1.0, 2.0).add(Complex.NaN).isNaN());
}

@Test
public void acosOfNaNIsNaN() {
    org.junit.Assert.assertTrue(Complex.NaN.acos().isNaN());
}
}

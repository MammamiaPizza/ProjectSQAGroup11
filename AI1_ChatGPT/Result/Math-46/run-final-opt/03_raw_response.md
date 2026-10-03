package org.apache.commons.math.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.exception.NullArgumentException;
import org.junit.Test;

public class ComplexMath46FaultTest {

    @Test
    public void testFiniteComplexDividedByComplexZeroIsInfinite() {
        Complex result = new Complex(3.0, -4.0).divide(Complex.ZERO);

        assertEquals(Double.POSITIVE_INFINITY, result.getReal(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, result.getImaginary(), 0.0);
    }

    @Test
    public void testFiniteComplexDividedByScalarZeroIsInfinite() {
        Complex result = new Complex(-2.0, 5.0).divide(0.0);

        assertEquals(Double.POSITIVE_INFINITY, result.getReal(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, result.getImaginary(), 0.0);
    }

    @Test
    public void testZeroDividedByComplexZeroIsNaN() {
        Complex result = Complex.ZERO.divide(Complex.ZERO);

        assertTrue(result.isNaN());
    }

    @Test
    public void testZeroDividedByScalarZeroIsNaN() {
        Complex result = Complex.ZERO.divide(0.0);

        assertTrue(result.isNaN());
    }

    @Test
    public void testFiniteComplexDividedByInfiniteComplexIsZero() {
        Complex result = new Complex(3.0, -4.0).divide(Complex.INF);

        assertEquals(Complex.ZERO, result);
    }

    @Test
    public void testFiniteComplexDividedByInfiniteScalarIsZero() {
        Complex result = new Complex(3.0, -4.0).divide(Double.POSITIVE_INFINITY);

        assertEquals(Complex.ZERO, result);
    }

    @Test
    public void testAtanOfIIsInfiniteComplex() {
        Complex result = Complex.I.atan();

        assertEquals(0.0, result.getReal(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, result.getImaginary(), 0.0);
    }

    @Test
    public void testRegularComplexDivision() {
        Complex result = new Complex(3.0, 4.0).divide(new Complex(1.0, -2.0));

        assertEquals(-1.0, result.getReal(), 0.0);
        assertEquals(2.0, result.getImaginary(), 0.0);
    }

    @Test(expected = NullArgumentException.class)
    public void testDivideByNullComplexThrows() {
        Complex.ONE.divide((Complex) null);
    }
}
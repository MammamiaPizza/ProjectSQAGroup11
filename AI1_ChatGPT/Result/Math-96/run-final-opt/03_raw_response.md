package org.apache.commons.math.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComplexMath221Test {

    private static void assertRawDoubleEquals(String message, double expected, double actual) {
        assertEquals(message, Double.doubleToRawLongBits(expected),
                Double.doubleToRawLongBits(actual));
    }

    @Test
    public void dividePositiveFiniteComponentsByInfinityPreservesPositiveZeros() {
        Complex result = new Complex(3.0, 4.0).divide(Complex.INF);

        assertRawDoubleEquals("real component", 0.0, result.getReal());
        assertRawDoubleEquals("imaginary component", 0.0, result.getImaginary());
    }

    @Test
    public void divideMixedFiniteComponentsByInfinityPreservesEachZeroSign() {
        Complex result = new Complex(3.0, -4.0).divide(Complex.INF);

        assertRawDoubleEquals("real component", 0.0, result.getReal());
        assertRawDoubleEquals("imaginary component", 0.0, result.getImaginary());
    }

    @Test
    public void divideNegativeFiniteComponentsByInfinityPreservesNegativeZeros() {
        Complex result = new Complex(-3.0, -4.0).divide(Complex.INF);

        assertRawDoubleEquals("real component", 0.0, result.getReal());
        assertRawDoubleEquals("imaginary component", 0.0, result.getImaginary());
    }

    @Test
    public void divideSignedZeroComponentsByInfinityPreservesTheirSigns() {
        Complex result = new Complex(-0.0, 0.0).divide(Complex.INF);

        assertRawDoubleEquals("real component", 0.0, result.getReal());
        assertRawDoubleEquals("imaginary component", 0.0, result.getImaginary());
    }

    @Test
    public void divideByComplexWithAnInfiniteComponentReturnsSignedZeros() {
        Complex result = new Complex(-2.0, 5.0)
                .divide(new Complex(Double.POSITIVE_INFINITY, 1.0));

        assertRawDoubleEquals("real component", 0.0, result.getReal());
        assertRawDoubleEquals("imaginary component", 0.0, result.getImaginary());
    }

    @Test
    public void divideFiniteComplexNumbersUsesStandardQuotient() {
        Complex result = new Complex(3.0, 4.0).divide(new Complex(1.0, -2.0));

        assertEquals(-1.0, result.getReal(), 0.0);
        assertEquals(2.0, result.getImaginary(), 0.0);
    }

    @Test
    public void divideByZeroComplexReturnsNaN() {
        Complex result = new Complex(1.0, -2.0).divide(Complex.ZERO);

        assertTrue(result.isNaN());
    }

    @Test
    public void divideWithNaNOperandReturnsNaN() {
        Complex result = new Complex(1.0, 2.0).divide(Complex.NaN);

        assertTrue(result.isNaN());
    }
}
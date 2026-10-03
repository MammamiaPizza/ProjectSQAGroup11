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

@org.junit.Test
public void absHandlesNaNInfinityAndStableFiniteComponentRatios() {
    org.junit.Assert.assertTrue(Double.isNaN(new Complex(Double.NaN, 1.0).abs()));
    org.junit.Assert.assertEquals(Double.POSITIVE_INFINITY,
            new Complex(1.0, Double.NEGATIVE_INFINITY).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    org.junit.Assert.assertEquals(2.0, new Complex(0.0, -2.0).abs(), 0.0);
}

@org.junit.Test
public void addCombinesRealAndImaginaryComponentsIndependently() {
    Complex result = new Complex(3.0, -4.0).add(new Complex(-2.0, 5.0));

    org.junit.Assert.assertEquals(1.0, result.getReal(), 0.0);
    org.junit.Assert.assertEquals(1.0, result.getImaginary(), 0.0);
}

@org.junit.Test
public void inverseTrigonometricFunctionsHandleNaNAndZeroPrincipalValues() {
    org.junit.Assert.assertTrue(Complex.NaN.acos().isNaN());
    org.junit.Assert.assertTrue(Complex.NaN.asin().isNaN());
    org.junit.Assert.assertTrue(Complex.NaN.atan().isNaN());

    Complex zero = Complex.ZERO;
    Complex acos = zero.acos();
    Complex asin = zero.asin();
    Complex atan = zero.atan();

    org.junit.Assert.assertEquals(Math.PI / 2.0, acos.getReal(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, acos.getImaginary(), 0.0);
    org.junit.Assert.assertEquals(0.0, asin.getReal(), 0.0);
    org.junit.Assert.assertEquals(0.0, asin.getImaginary(), 0.0);
    org.junit.Assert.assertEquals(0.0, atan.getReal(), 0.0);
    org.junit.Assert.assertEquals(0.0, atan.getImaginary(), 0.0);
}
}

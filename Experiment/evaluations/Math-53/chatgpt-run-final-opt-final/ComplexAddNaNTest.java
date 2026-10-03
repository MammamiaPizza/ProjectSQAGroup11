package org.apache.commons.math.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.exception.NullArgumentException;
import org.junit.Test;

public class ComplexAddNaNTest {

    @Test
    public void testAddFiniteComplexNumbersIsComponentWise() {
        Complex left = new Complex(3.5, -2.0);
        Complex right = new Complex(-1.5, 4.25);

        Complex result = left.add(right);

        assertEquals(2.0, result.getReal(), 0.0);
        assertEquals(2.25, result.getImaginary(), 0.0);
        assertTrue(!result.isNaN());
    }

    @Test
    public void testAddNaNOnLeftReturnsNaN() {
        Complex result = Complex.NaN.add(new Complex(3.0, -4.0));

        assertTrue(result.isNaN());
        assertEquals(Complex.NaN, result);
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test
    public void testAddNaNOnRightReturnsNaN() {
        Complex result = new Complex(3.0, -4.0).add(Complex.NaN);

        assertTrue(result.isNaN());
        assertEquals(Complex.NaN, result);
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test
    public void testAddComplexWithOnlyNaNRealPartReturnsNaN() {
        Complex result = new Complex(2.0, 3.0).add(new Complex(Double.NaN, 0.0));

        assertTrue(result.isNaN());
        assertEquals(Complex.NaN, result);
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test
    public void testAddComplexWithOnlyNaNImaginaryPartReturnsNaN() {
        Complex result = new Complex(2.0, 3.0).add(new Complex(0.0, Double.NaN));

        assertTrue(result.isNaN());
        assertEquals(Complex.NaN, result);
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test
    public void testAddTwoNaNComplexNumbersReturnsNaN() {
        Complex result = Complex.NaN.add(Complex.NaN);

        assertTrue(result.isNaN());
        assertEquals(Complex.NaN, result);
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test(expected = NullArgumentException.class)
    public void testAddNullThrowsNullArgumentException() {
        Complex.ONE.add(null);
    }

@Test
public void testAbsHandlesSpecialValuesAndFiniteComponentOrderings() {
    assertTrue(Double.isNaN(Complex.NaN.abs()));
    assertEquals(Double.POSITIVE_INFINITY,
            new Complex(Double.POSITIVE_INFINITY, 1.0).abs(), 0.0);
    assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    assertEquals(2.0, new Complex(0.0, -2.0).abs(), 0.0);
}

@Test
public void testInverseTrigonometricFunctionsAtZeroAndNaN() {
    Complex acos = Complex.ZERO.acos();
    assertEquals(Math.PI / 2.0, acos.getReal(), 1.0e-12);
    assertEquals(0.0, acos.getImaginary(), 1.0e-12);

    Complex asin = Complex.ZERO.asin();
    assertEquals(0.0, asin.getReal(), 1.0e-12);
    assertEquals(0.0, asin.getImaginary(), 1.0e-12);

    Complex atan = Complex.ZERO.atan();
    assertEquals(0.0, atan.getReal(), 1.0e-12);
    assertEquals(0.0, atan.getImaginary(), 1.0e-12);

    assertTrue(Complex.NaN.acos().isNaN());
    assertTrue(Complex.NaN.asin().isNaN());
    assertTrue(Complex.NaN.atan().isNaN());
}
}

package org.apache.commons.math.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComplexMath47Test {

    @Test
    public void atanOfImaginaryUnitIsNaNInBothComponents() {
        Complex result = Complex.I.atan();

        assertTrue(result.isNaN());
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test
    public void nonZeroComplexDividedByZeroIsNaN() {
        Complex result = new Complex(3.0, -4.0).divide(Complex.ZERO);

        assertTrue(result.isNaN());
        assertTrue(Double.isNaN(result.getReal()));
        assertTrue(Double.isNaN(result.getImaginary()));
    }

    @Test
    public void purelyRealComplexDividedByZeroIsNaN() {
        Complex result = new Complex(5.0, 0.0).divide(new Complex(0.0, 0.0));

        assertTrue(result.isNaN());
    }

    @Test
    public void purelyImaginaryComplexDividedByZeroIsNaN() {
        Complex result = new Complex(0.0, -2.0).divide(Complex.ZERO);

        assertTrue(result.isNaN());
    }

    @Test
    public void zeroDividedByZeroIsNaN() {
        Complex result = Complex.ZERO.divide(Complex.ZERO);

        assertTrue(result.isNaN());
    }

    @Test
    public void divisionByNonZeroComplexUsesComplexQuotient() {
        Complex result = new Complex(3.0, 4.0).divide(new Complex(1.0, -2.0));

        assertEquals(-1.0, result.getReal(), 0.0);
        assertEquals(2.0, result.getImaginary(), 0.0);
    }
}

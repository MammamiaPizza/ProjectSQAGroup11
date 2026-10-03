package org.apache.commons.math.complex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplexMATH722Test {

    @Test
    public void testTanLargeFiniteImaginaryPartsApproachSignedI() {
        Complex positive = new Complex(0.5, 1000.0).tan();
        assertEquals(0.0, positive.getReal(), 0.0);
        assertEquals(1.0, positive.getImaginary(), 0.0);

        Complex negative = new Complex(0.5, -1000.0).tan();
        assertEquals(0.0, negative.getReal(), 0.0);
        assertEquals(-1.0, negative.getImaginary(), 0.0);
    }

    @Test
    public void testTanhLargeFiniteRealPartsApproachSignedOne() {
        Complex positive = new Complex(1000.0, 0.5).tanh();
        assertEquals(1.0, positive.getReal(), 0.0);
        assertEquals(0.0, positive.getImaginary(), 0.0);

        Complex negative = new Complex(-1000.0, 0.5).tanh();
        assertEquals(-1.0, negative.getReal(), 0.0);
        assertEquals(0.0, negative.getImaginary(), 0.0);
    }

    @Test
    public void testTanInfiniteImaginaryPartsApproachSignedI() {
        Complex positive = new Complex(0.5, Double.POSITIVE_INFINITY).tan();
        assertEquals(0.0, positive.getReal(), 0.0);
        assertEquals(1.0, positive.getImaginary(), 0.0);

        Complex negative = new Complex(0.5, Double.NEGATIVE_INFINITY).tan();
        assertEquals(0.0, negative.getReal(), 0.0);
        assertEquals(-1.0, negative.getImaginary(), 0.0);
    }

    @Test
    public void testTanhInfiniteRealPartsApproachSignedOne() {
        Complex positive = new Complex(Double.POSITIVE_INFINITY, 0.5).tanh();
        assertEquals(1.0, positive.getReal(), 0.0);
        assertEquals(0.0, positive.getImaginary(), 0.0);

        Complex negative = new Complex(Double.NEGATIVE_INFINITY, 0.5).tanh();
        assertEquals(-1.0, negative.getReal(), 0.0);
        assertEquals(0.0, negative.getImaginary(), 0.0);
    }

    @Test
    public void testTanAndTanhPropagateNaN() {
        Complex value = new Complex(Double.NaN, 1.0);

        assertTrue(value.tan().isNaN());
        assertTrue(value.tanh().isNaN());
    }

    @Test
    public void testTanAndTanhRetainOrdinaryFiniteValues() {
        Complex tan = new Complex(Math.PI / 4.0, 0.0).tan();
        assertEquals(1.0, tan.getReal(), 1.0e-15);
        assertEquals(0.0, tan.getImaginary(), 0.0);

        Complex tanh = new Complex(0.0, Math.PI / 4.0).tanh();
        assertEquals(0.0, tanh.getReal(), 0.0);
        assertEquals(1.0, tanh.getImaginary(), 1.0e-15);
    }

@Test
public void testSingleArgumentConstructorAndAdditionHandleFiniteAndNaNValues() {
    Complex value = new Complex(2.5);
    org.junit.Assert.assertEquals(2.5, value.getReal(), 0.0);
    org.junit.Assert.assertEquals(0.0, value.getImaginary(), 0.0);

    Complex scalarSum = value.add(1.5);
    org.junit.Assert.assertEquals(4.0, scalarSum.getReal(), 0.0);
    org.junit.Assert.assertEquals(0.0, scalarSum.getImaginary(), 0.0);

    Complex complexSum = new Complex(1.0, 2.0).add(new Complex(3.0, -5.0));
    org.junit.Assert.assertEquals(4.0, complexSum.getReal(), 0.0);
    org.junit.Assert.assertEquals(-3.0, complexSum.getImaginary(), 0.0);

    org.junit.Assert.assertTrue(value.add(Double.NaN).isNaN());
    org.junit.Assert.assertTrue(Complex.NaN.add(1.0).isNaN());
    org.junit.Assert.assertTrue(Complex.ONE.add(Complex.NaN).isNaN());
}

@Test
public void testAbsHandlesNaNInfinityAxesAndScaledFiniteValues() {
    org.junit.Assert.assertTrue(Double.isNaN(new Complex(Double.NaN, 1.0).abs()));
    org.junit.Assert.assertEquals(Double.POSITIVE_INFINITY,
            new Complex(Double.POSITIVE_INFINITY, 1.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(3.0, 4.0).abs(), 0.0);
    org.junit.Assert.assertEquals(5.0, new Complex(4.0, 3.0).abs(), 0.0);
    org.junit.Assert.assertEquals(7.0, new Complex(0.0, -7.0).abs(), 0.0);
    org.junit.Assert.assertEquals(0.0, Complex.ZERO.abs(), 0.0);
}

@Test
public void testAcosHandlesNaNAndZero() {
    org.junit.Assert.assertTrue(Complex.NaN.acos().isNaN());

    Complex result = Complex.ZERO.acos();
    org.junit.Assert.assertEquals(Math.PI / 2.0, result.getReal(), 1.0e-15);
    org.junit.Assert.assertEquals(0.0, result.getImaginary(), 1.0e-15);
}
}

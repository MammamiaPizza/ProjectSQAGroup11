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
}

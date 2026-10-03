package org.apache.commons.math.special;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.junit.Test;

public class GammaMath104Test {

    @Test
    public void testRegularizedGammaPAtOneOneHasRequiredPrecision() throws MathException {
        assertEquals(0.632120558828558,
                     Gamma.regularizedGammaP(1.0, 1.0),
                     1.0e-14);
    }

    @Test
    public void testRegularizedGammaPForUnitShapeAboveShapeUsesCorrectValue() throws MathException {
        assertEquals(0.8646647167633873,
                     Gamma.regularizedGammaP(1.0, 2.0),
                     1.0e-13);
    }

    @Test
    public void testRegularizedGammaPAndQAtZero() throws MathException {
        assertEquals(0.0, Gamma.regularizedGammaP(2.5, 0.0), 0.0);
        assertEquals(1.0, Gamma.regularizedGammaQ(2.5, 0.0), 0.0);
    }

    @Test
    public void testRegularizedGammaQForUnitShapeAtOne() throws MathException {
        assertEquals(0.36787944117144233,
                     Gamma.regularizedGammaQ(1.0, 1.0),
                     1.0e-13);
    }

    @Test
    public void testRegularizedGammaPAndQAreComplements() throws MathException {
        double p = Gamma.regularizedGammaP(1.0, 1.0, 1.0e-14, 100000);
        double q = Gamma.regularizedGammaQ(1.0, 1.0, 1.0e-14, 100000);

        assertEquals(1.0, p + q, 1.0e-13);
    }

    @Test(expected = MaxIterationsExceededException.class)
    public void testRegularizedGammaPThrowsWhenNoIterationsAreAllowed() throws MathException {
        Gamma.regularizedGammaP(1.0, 1.0, 1.0e-15, 0);
    }

    @Test
    public void testRegularizedGammaPReturnsNaNForInvalidArguments() throws MathException {
        assertTrue(Double.isNaN(Gamma.regularizedGammaP(0.0, 1.0)));
        assertTrue(Double.isNaN(Gamma.regularizedGammaP(1.0, -1.0)));
    }
}
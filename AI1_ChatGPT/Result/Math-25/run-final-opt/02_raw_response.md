package org.apache.commons.math3.optimization.fitting;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.optimization.general.LevenbergMarquardtOptimizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HarmonicFitterMATH844Test {

    @Test(expected = MathIllegalStateException.class)
    public void testParameterGuesserRejectsConstantObservations() {
        WeightedObservedPoint[] observations = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 0.0, 1.0),
            new WeightedObservedPoint(1.0, 1.0, 1.0),
            new WeightedObservedPoint(1.0, 2.0, 1.0),
            new WeightedObservedPoint(1.0, 3.0, 1.0)
        };

        new HarmonicFitter.ParameterGuesser(observations).guess();
    }

    @Test(expected = MathIllegalStateException.class)
    public void testParameterGuesserRejectsRepeatedAbscissa() {
        WeightedObservedPoint[] observations = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 0.0, 1.0),
            new WeightedObservedPoint(1.0, 0.0, 1.0),
            new WeightedObservedPoint(1.0, 1.0, -1.0),
            new WeightedObservedPoint(1.0, 2.0, 1.0)
        };

        new HarmonicFitter.ParameterGuesser(observations).guess();
    }

    @Test(expected = MathIllegalStateException.class)
    public void testFitWithoutInitialGuessRejectsIllConditionedObservations() {
        HarmonicFitter fitter = new HarmonicFitter(new LevenbergMarquardtOptimizer());
        fitter.addObservedPoint(1.0, 0.0, 1.0);
        fitter.addObservedPoint(1.0, 1.0, 1.0);
        fitter.addObservedPoint(1.0, 2.0, 1.0);
        fitter.addObservedPoint(1.0, 3.0, 1.0);

        fitter.fit();
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testParameterGuesserRequiresAtLeastFourObservations() {
        new HarmonicFitter.ParameterGuesser(new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 0.0, 0.0),
            new WeightedObservedPoint(1.0, 1.0, 1.0),
            new WeightedObservedPoint(1.0, 2.0, 0.0)
        });
    }

    @Test
    public void testParameterGuesserSortsCopyAndProducesFiniteParameters() {
        WeightedObservedPoint[] observations = new WeightedObservedPoint[] {
            point(2.0),
            point(0.0),
            point(3.0),
            point(1.0),
            point(4.0),
            point(5.0)
        };

        double[] guess = new HarmonicFitter.ParameterGuesser(observations).guess();

        assertEquals(2.0, observations[0].getX(), 0.0);
        assertEquals(0.0, observations[1].getX(), 0.0);
        assertTrue(guess[0] > 0.0);
        assertTrue(guess[1] > 0.0);
        assertFalse(Double.isNaN(guess[0]));
        assertFalse(Double.isInfinite(guess[0]));
        assertFalse(Double.isNaN(guess[1]));
        assertFalse(Double.isInfinite(guess[1]));
        assertFalse(Double.isNaN(guess[2]));
        assertFalse(Double.isInfinite(guess[2]));
    }

    @Test
    public void testFitWithInitialGuessRecoversHarmonicParameters() {
        HarmonicFitter fitter = new HarmonicFitter(new LevenbergMarquardtOptimizer());
        addObservations(fitter);

        double[] fitted = fitter.fit(new double[] { 1.8, 1.4, 0.2 });

        assertEquals(2.0, fitted[0], 1.0e-4);
        assertEquals(1.5, fitted[1], 1.0e-4);
        assertEquals(0.3, fitted[2], 1.0e-4);
    }

    @Test
    public void testFitWithoutInitialGuessRecoversHarmonicParameters() {
        HarmonicFitter fitter = new HarmonicFitter(new LevenbergMarquardtOptimizer());
        addObservations(fitter);

        double[] fitted = fitter.fit();

        assertEquals(2.0, fitted[0], 1.0e-3);
        assertEquals(1.5, fitted[1], 1.0e-3);
        assertEquals(0.3, fitted[2], 1.0e-3);
    }

    private static WeightedObservedPoint point(double x) {
        return new WeightedObservedPoint(1.0, x, 2.0 * Math.cos(1.5 * x + 0.3));
    }

    private static void addObservations(HarmonicFitter fitter) {
        for (int i = 0; i <= 60; i++) {
            double x = i * 0.1;
            fitter.addObservedPoint(1.0, x, 2.0 * Math.cos(1.5 * x + 0.3));
        }
    }
}
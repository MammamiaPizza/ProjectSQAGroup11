package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.analysis.function.Gaussian;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GaussianFitterMATH519Test {

    private static final double FWHM_TO_SIGMA =
        2.0 * Math.sqrt(2.0 * Math.log(2.0));

    @Test
    public void testGuesserUsesHalfOfPeakHeightForPositiveMeanAndUnsortedPoints() {
        final WeightedObservedPoint[] points = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 12.0, 0.0),
            new WeightedObservedPoint(1.0, 10.0, 8.0),
            new WeightedObservedPoint(1.0, 9.0, 4.0),
            new WeightedObservedPoint(1.0, 11.0, 4.0),
            new WeightedObservedPoint(1.0, 8.0, 0.0)
        };

        final double[] guess = new GaussianFitter.ParameterGuesser(points).guess();

        assertEquals(8.0, guess[0], 0.0);
        assertEquals(10.0, guess[1], 0.0);
        assertEquals(4.0 / FWHM_TO_SIGMA, guess[2], 1e-15);
    }

    @Test
    public void testGuesserUsesHalfOfPeakHeightForNegativeMean() {
        final WeightedObservedPoint[] points = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, -8.0, 0.0),
            new WeightedObservedPoint(1.0, -10.0, 8.0),
            new WeightedObservedPoint(1.0, -9.0, 4.0),
            new WeightedObservedPoint(1.0, -11.0, 4.0),
            new WeightedObservedPoint(1.0, -12.0, 0.0)
        };

        final double[] guess = new GaussianFitter.ParameterGuesser(points).guess();

        assertEquals(8.0, guess[0], 0.0);
        assertEquals(-10.0, guess[1], 0.0);
        assertEquals(4.0 / FWHM_TO_SIGMA, guess[2], 1e-15);
        assertTrue(guess[2] > 0.0);
    }

    @Test
    public void testGuesserInterpolatesHalfHeightCrossings() {
        final WeightedObservedPoint[] points = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 3.0, 0.0),
            new WeightedObservedPoint(1.0, 0.0, 0.0),
            new WeightedObservedPoint(1.0, 1.0, 8.0),
            new WeightedObservedPoint(1.0, 2.0, 4.0)
        };

        final double[] guess = new GaussianFitter.ParameterGuesser(points).guess();

        assertEquals(8.0, guess[0], 0.0);
        assertEquals(1.0, guess[1], 0.0);
        assertEquals(3.0 / FWHM_TO_SIGMA, guess[2], 1e-15);
    }

    @Test
    public void testGuesserFallsBackToWholeRangeWhenHalfHeightIsNotObserved() {
        final WeightedObservedPoint[] points = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 5.0, 9.0),
            new WeightedObservedPoint(1.0, 2.0, 8.0),
            new WeightedObservedPoint(1.0, 4.0, 10.0),
            new WeightedObservedPoint(1.0, 3.0, 9.0)
        };

        final double[] guess = new GaussianFitter.ParameterGuesser(points).guess();

        assertEquals(10.0, guess[0], 0.0);
        assertEquals(4.0, guess[1], 0.0);
        assertEquals(3.0 / FWHM_TO_SIGMA, guess[2], 1e-15);
    }

    @Test
    public void testGuesserDoesNotReorderCallerObservationArray() {
        final WeightedObservedPoint first = new WeightedObservedPoint(1.0, 2.0, 0.0);
        final WeightedObservedPoint second = new WeightedObservedPoint(1.0, 0.0, 0.0);
        final WeightedObservedPoint third = new WeightedObservedPoint(1.0, 1.0, 8.0);
        final WeightedObservedPoint fourth = new WeightedObservedPoint(1.0, 3.0, 0.0);
        final WeightedObservedPoint[] points = { first, second, third, fourth };

        new GaussianFitter.ParameterGuesser(points).guess();

        assertTrue(points[0] == first);
        assertTrue(points[1] == second);
        assertTrue(points[2] == third);
        assertTrue(points[3] == fourth);
    }

    @Test
    public void testGuessReturnsDefensiveCopy() {
        final WeightedObservedPoint[] points = new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 0.0, 0.0),
            new WeightedObservedPoint(1.0, 1.0, 8.0),
            new WeightedObservedPoint(1.0, 2.0, 4.0),
            new WeightedObservedPoint(1.0, 3.0, 0.0)
        };
        final GaussianFitter.ParameterGuesser guesser =
            new GaussianFitter.ParameterGuesser(points);

        final double[] first = guesser.guess();
        first[0] = -100.0;
        final double[] second = guesser.guess();

        assertEquals(8.0, second[0], 0.0);
    }

    @Test(expected = NullArgumentException.class)
    public void testGuesserRejectsNullObservations() {
        new GaussianFitter.ParameterGuesser(null);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testGuesserRequiresAtLeastThreeObservations() {
        new GaussianFitter.ParameterGuesser(new WeightedObservedPoint[] {
            new WeightedObservedPoint(1.0, 0.0, 0.0),
            new WeightedObservedPoint(1.0, 1.0, 1.0)
        });
    }

    @Test
    public void testFitWithPositiveMeanProducesParametersAcceptedByGaussian() {
        final GaussianFitter fitter =
            new GaussianFitter(new LevenbergMarquardtOptimizer());

        final Gaussian gaussian = new Gaussian(2.0, 10.0, 1.0);
        for (int x = 7; x <= 13; x++) {
            fitter.addObservedPoint(1.0, x, gaussian.value(x));
        }

        final double[] parameters = fitter.fit();
        final double valueAtMean = new Gaussian.Parametric().value(parameters[1], parameters);

        assertEquals(3, parameters.length);
        assertTrue(parameters[2] > 0.0);
        assertFalse(Double.isNaN(valueAtMean));
        assertFalse(Double.isInfinite(valueAtMean));
    }
}
package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CMAESOptimizerSigmaValidationTest {

    private static final MultivariateFunction CENTERED_SQUARE = new MultivariateFunction() {
        public double value(double[] point) {
            final double delta = point[0] - 0.5;
            return delta * delta;
        }
    };

    private CMAESOptimizer optimizer(double[] sigma) {
        return new CMAESOptimizer(4, sigma, 200, 0.0, true, 0, 0,
                                  new MersenneTwister(123456L), false, null);
    }

    private PointValuePair optimize(double[] sigma) {
        return optimizer(sigma).optimize(5000, CENTERED_SQUARE, GoalType.MINIMIZE,
                                         new double[] { 0.5 },
                                         new double[] { 0.0 },
                                         new double[] { 1.0 });
    }

    @Test(expected = MathIllegalStateException.class)
    public void testInputSigmaGreaterThanBoundedCoordinateRangeIsRejected() {
        optimize(new double[] { 1.0000001 });
    }

    @Test
    public void testInputSigmaEqualToBoundedCoordinateRangeIsAccepted() {
        PointValuePair result = optimize(new double[] { 1.0 });

        assertNotNull(result);
        assertEquals(0.0, result.getValue(), 0.0);
        assertTrue(result.getPoint()[0] >= 0.0);
        assertTrue(result.getPoint()[0] <= 1.0);
    }

    @Test
    public void testInputSigmaSmallerThanBoundedCoordinateRangeIsAccepted() {
        PointValuePair result = optimize(new double[] { 0.25 });

        assertNotNull(result);
        assertEquals(0.0, result.getValue(), 0.0);
        assertTrue(result.getPoint()[0] >= 0.0);
        assertTrue(result.getPoint()[0] <= 1.0);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testInputSigmaDimensionMustMatchStartPointDimension() {
        optimizer(new double[] { 0.25, 0.25 }).optimize(
                5000,
                CENTERED_SQUARE,
                GoalType.MINIMIZE,
                new double[] { 0.5 },
                new double[] { 0.0 },
                new double[] { 1.0 });
    }
}
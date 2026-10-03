package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CMAESOptimizerBoundsTest {

    @Test
    public void returnedPointDoesNotExceedUpperBoundWhenUnconstrainedOptimumIsAboveIt() {
        CMAESOptimizer optimizer = new CMAESOptimizer(
                8, new double[] { 0.25 }, 1000, 0.0,
                true, 0, 0, new MersenneTwister(123456789), false);

        PointValuePair result = optimizer.optimize(
                10000,
                new MultivariateFunction() {
                    public double value(double[] point) {
                        return point[0];
                    }
                },
                GoalType.MAXIMIZE,
                new double[] { 0.25 },
                new double[] { 0.0 },
                new double[] { 0.5 });

        assertNotNull(result);
        assertTrue("Returned point must respect the upper bound: " + result.getPoint()[0],
                   result.getPoint()[0] <= 0.5);
        assertTrue("Returned point must respect the lower bound: " + result.getPoint()[0],
                   result.getPoint()[0] >= 0.0);
    }

    @Test
    public void returnedPointDoesNotGoBelowLowerBoundWhenUnconstrainedOptimumIsBelowIt() {
        CMAESOptimizer optimizer = new CMAESOptimizer(
                8, new double[] { 0.25 }, 1000, 0.0,
                true, 0, 0, new MersenneTwister(987654321), false);

        PointValuePair result = optimizer.optimize(
                10000,
                new MultivariateFunction() {
                    public double value(double[] point) {
                        return point[0];
                    }
                },
                GoalType.MINIMIZE,
                new double[] { 0.25 },
                new double[] { 0.0 },
                new double[] { 0.5 });

        assertNotNull(result);
        assertTrue("Returned point must respect the lower bound: " + result.getPoint()[0],
                   result.getPoint()[0] >= 0.0);
        assertTrue("Returned point must respect the upper bound: " + result.getPoint()[0],
                   result.getPoint()[0] <= 0.5);
    }
}
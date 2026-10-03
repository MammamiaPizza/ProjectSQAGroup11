package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Assert;
import org.junit.Test;

public class CMAESOptimizerBoundaryRegressionTest {

    @Test
    public void testFitAccuracyDependsOnBoundary() {
        CMAESOptimizer optimizer = new CMAESOptimizer(
                4, new double[] { 1.0 }, 10000, 0.0, true, 0, 0,
                new MersenneTwister(123456789L), false);

        PointValuePair result = optimizer.optimize(
                10000,
                new MultivariateFunction() {
                    @Override
                    public double value(double[] point) {
                        return point[0];
                    }
                },
                GoalType.MAXIMIZE,
                new double[] { 8.0 },
                new double[] { 0.0 },
                new double[] { 11.1 });

        Assert.assertEquals(11.1, result.getValue(), 1e-6);
        Assert.assertEquals(11.1, result.getPoint()[0], 1e-6);
    }

    @Test
    public void testBoundedInteriorMinimumIsReturnedInOriginalCoordinates() {
        CMAESOptimizer optimizer = new CMAESOptimizer(
                4, new double[] { 1.0 }, 10000, 0.0, true, 0, 0,
                new MersenneTwister(987654321L), false);

        PointValuePair result = optimizer.optimize(
                10000,
                new MultivariateFunction() {
                    @Override
                    public double value(double[] point) {
                        double delta = point[0] - 3.0;
                        return delta * delta;
                    }
                },
                GoalType.MINIMIZE,
                new double[] { 8.0 },
                new double[] { 0.0 },
                new double[] { 10.0 });

        Assert.assertTrue(result.getPoint()[0] >= 0.0);
        Assert.assertTrue(result.getPoint()[0] <= 10.0);
        Assert.assertEquals(3.0, result.getPoint()[0], 1e-3);
        Assert.assertEquals(0.0, result.getValue(), 1e-6);
    }
}

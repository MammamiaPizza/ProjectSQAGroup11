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

@Test(expected = org.apache.commons.math3.exception.MathUnsupportedOperationException.class)
public void testMixedFiniteAndInfiniteBoundsAreRejected() {
    new CMAESOptimizer().optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 1.0 },
            new double[] { 0.0 },
            new double[] { Double.POSITIVE_INFINITY });
}

@Test(expected = org.apache.commons.math3.exception.DimensionMismatchException.class)
public void testInputSigmaDimensionMustMatchStartPointDimension() {
    new CMAESOptimizer(0, new double[] { 0.1, 0.1 }).optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.0 },
            new double[] { Double.NEGATIVE_INFINITY },
            new double[] { Double.POSITIVE_INFINITY });
}

@Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
public void testNegativeInputSigmaIsRejected() {
    new CMAESOptimizer(0, new double[] { -0.1 }).optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.0 },
            new double[] { -1.0 },
            new double[] { 1.0 });
}

@Test(expected = org.apache.commons.math3.exception.OutOfRangeException.class)
public void testInputSigmaLargerThanBoundedRangeIsRejected() {
    new CMAESOptimizer(0, new double[] { 2.0 }).optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5 },
            new double[] { 0.0 },
            new double[] { 1.0 });
}
}

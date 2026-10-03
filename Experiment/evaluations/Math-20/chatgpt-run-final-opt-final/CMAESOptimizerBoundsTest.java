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

@Test(expected = org.apache.commons.math3.exception.MathUnsupportedOperationException.class)
public void rejectsMixedFiniteAndInfiniteBounds() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { 0.1, 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.0 },
            new double[] { 0.0, Double.NEGATIVE_INFINITY },
            new double[] { 1.0, Double.POSITIVE_INFINITY });
}

@Test(expected = org.apache.commons.math3.exception.DimensionMismatchException.class)
public void rejectsSigmaWithWrongDimension() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.5 },
            new double[] { 0.0, 0.0 },
            new double[] { 1.0, 1.0 });
}

@Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
public void rejectsNegativeSigma() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { -0.1, 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.5 },
            new double[] { 0.0, 0.0 },
            new double[] { 1.0, 1.0 });
}

@Test(expected = org.apache.commons.math3.exception.OutOfRangeException.class)
public void rejectsSigmaLargerThanBoundedRange() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { 1.1, 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.5 },
            new double[] { 0.0, 0.0 },
            new double[] { 1.0, 1.0 });
}

private org.apache.commons.math3.analysis.MultivariateFunction cmaesValidationObjective() {
    return new org.apache.commons.math3.analysis.MultivariateFunction() {
        public double value(double[] point) {
            return 0.0;
        }
    };
}
}

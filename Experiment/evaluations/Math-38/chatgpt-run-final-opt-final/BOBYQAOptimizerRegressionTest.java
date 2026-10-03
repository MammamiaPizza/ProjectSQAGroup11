package org.apache.commons.math.optimization.direct;

import org.apache.commons.math.analysis.MultivariateFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BOBYQAOptimizerRegressionTest {

    @Test
    public void constrainedRosenbrockWithSixInterpolationPointsConvergesWithoutInternalException() {
        MultivariateFunction rosenbrock = new MultivariateFunction() {
            public double value(double[] point) {
                double x = point[0];
                double y = point[1];
                return 100.0 * (y - x * x) * (y - x * x)
                    + (1.0 - x) * (1.0 - x);
            }
        };

        BOBYQAOptimizer optimizer = new BOBYQAOptimizer(6);
        RealPointValuePair result = optimizer.optimize(
            100000,
            rosenbrock,
            GoalType.MINIMIZE,
            new double[] { -1.2, 1.0 },
            new double[] { -2.0, -2.0 },
            new double[] { 2.0, 2.0 });

        double[] point = result.getPoint();
        assertTrue(point[0] >= -2.0 && point[0] <= 2.0);
        assertTrue(point[1] >= -2.0 && point[1] <= 2.0);
        assertEquals(0.0, result.getValue(), 1.0e-8);
        assertEquals(1.0, point[0], 1.0e-4);
        assertEquals(1.0, point[1], 1.0e-4);
    }

    @Test
    public void boundedOptimizationReturnsBoundaryMinimumAndRespectsBounds() {
        MultivariateFunction quadratic = new MultivariateFunction() {
            public double value(double[] point) {
                double dx = point[0] - 3.0;
                double dy = point[1] + 1.0;
                return dx * dx + dy * dy;
            }
        };

        BOBYQAOptimizer optimizer = new BOBYQAOptimizer(5);
        RealPointValuePair result = optimizer.optimize(
            10000,
            quadratic,
            GoalType.MINIMIZE,
            new double[] { 1.0, -0.5 },
            new double[] { 0.0, -2.0 },
            new double[] { 2.0, 0.0 });

        double[] point = result.getPoint();
        assertTrue(point[0] >= 0.0 && point[0] <= 2.0);
        assertTrue(point[1] >= -2.0 && point[1] <= 0.0);
        assertEquals(1.0, result.getValue(), 1.0e-6);
        assertEquals(2.0, point[0], 1.0e-4);
        assertEquals(-1.0, point[1], 1.0e-4);
    }
}

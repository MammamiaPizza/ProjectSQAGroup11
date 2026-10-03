package org.apache.commons.math.optimization.direct;

import static org.junit.Assert.assertEquals;

import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleRealPointChecker;
import org.junit.Test;

public class MultiDirectionalRegressionTest {

    @Test
    public void testMinimizeQuadraticUsingReflectionAndExpansion() throws Exception {
        MultiDirectional optimizer = new MultiDirectional();
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleRealPointChecker(1.0e-9, 1.0e-9));
        optimizer.setStartConfiguration(new double[] { 0.5, 0.5 });

        RealPointValuePair optimum = optimizer.optimize(new MultivariateRealFunction() {
            public double value(double[] point) {
                double dx = point[0] - 1.0;
                double dy = point[1] + 2.0;
                return dx * dx + dy * dy;
            }
        }, GoalType.MINIMIZE, new double[] { 3.0, -4.0 });

        assertEquals(1.0, optimum.getPoint()[0], 1.0e-5);
        assertEquals(-2.0, optimum.getPoint()[1], 1.0e-5);
        assertEquals(0.0, optimum.getValue(), 1.0e-9);
    }

    @Test
    public void testMaximizeConcaveQuadraticUsingReflectionAndExpansion() throws Exception {
        MultiDirectional optimizer = new MultiDirectional();
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleRealPointChecker(1.0e-9, 1.0e-9));
        optimizer.setStartConfiguration(new double[] { 0.5, 0.5 });

        RealPointValuePair optimum = optimizer.optimize(new MultivariateRealFunction() {
            public double value(double[] point) {
                double dx = point[0] - 2.0;
                double dy = point[1] + 1.0;
                return -(dx * dx + dy * dy);
            }
        }, GoalType.MAXIMIZE, new double[] { -1.0, 3.0 });

        assertEquals(2.0, optimum.getPoint()[0], 1.0e-5);
        assertEquals(-1.0, optimum.getPoint()[1], 1.0e-5);
        assertEquals(0.0, optimum.getValue(), 1.0e-9);
    }

    @Test
    public void testStationaryBestPointConvergesWithoutExhaustingIterations() throws Exception {
        MultiDirectional optimizer = new MultiDirectional();
        optimizer.setMaxIterations(100);
        optimizer.setConvergenceChecker(new SimpleRealPointChecker(1.0e-12, 1.0e-12));
        optimizer.setStartConfiguration(new double[] { 0.25, 0.25 });

        RealPointValuePair optimum = optimizer.optimize(new MultivariateRealFunction() {
            public double value(double[] point) {
                return point[0] * point[0] + point[1] * point[1];
            }
        }, GoalType.MINIMIZE, new double[] { 0.0, 0.0 });

        assertEquals(0.0, optimum.getPoint()[0], 0.0);
        assertEquals(0.0, optimum.getPoint()[1], 0.0);
        assertEquals(0.0, optimum.getValue(), 0.0);
    }
}

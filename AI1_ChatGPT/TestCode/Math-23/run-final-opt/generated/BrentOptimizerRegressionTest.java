package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrentOptimizerRegressionTest {

    @Test
    public void testInitialBestPointIsRetainedForMinimization() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-10);
        UnivariatePointValuePair result = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return x * x;
                    }
                },
                GoalType.MINIMIZE,
                -1.0,
                1.0,
                0.0);

        assertEquals(0.0, result.getPoint(), 0.0);
        assertEquals(0.0, result.getValue(), 0.0);
    }

    @Test
    public void testInitialBestPointIsRetainedForMaximization() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-10);
        UnivariatePointValuePair result = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return -x * x;
                    }
                },
                GoalType.MAXIMIZE,
                -1.0,
                1.0,
                0.0);

        assertEquals(0.0, result.getPoint(), 0.0);
        assertEquals(0.0, result.getValue(), 0.0);
    }

    @Test
    public void testInitialBestPointIsRetainedWhenCheckerStopsImmediately() {
        BrentOptimizer optimizer = new BrentOptimizer(
                1e-12,
                1e-10,
                new ConvergenceChecker<UnivariatePointValuePair>() {
                    public boolean converged(int iteration,
                                             UnivariatePointValuePair previous,
                                             UnivariatePointValuePair current) {
                        return true;
                    }
                });

        UnivariatePointValuePair result = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return x * x;
                    }
                },
                GoalType.MINIMIZE,
                -1.0,
                1.0,
                0.0);

        assertEquals(0.0, result.getPoint(), 0.0);
        assertEquals(0.0, result.getValue(), 0.0);
    }

    @Test
    public void testFindsInteriorMinimum() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-10);
        UnivariatePointValuePair result = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return (x - 2.0) * (x - 2.0) + 3.0;
                    }
                },
                GoalType.MINIMIZE,
                -5.0,
                6.0,
                -1.0);

        assertEquals(2.0, result.getPoint(), 1e-6);
        assertEquals(3.0, result.getValue(), 1e-10);
    }

    @Test
    public void testFindsInteriorMaximum() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-10);
        UnivariatePointValuePair result = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return 4.0 - (x + 1.5) * (x + 1.5);
                    }
                },
                GoalType.MAXIMIZE,
                -5.0,
                3.0,
                0.0);

        assertEquals(-1.5, result.getPoint(), 1e-6);
        assertEquals(4.0, result.getValue(), 1e-10);
    }

    @Test
    public void testApproachesBothIntervalBoundaries() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-10);

        UnivariatePointValuePair minimum = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return x;
                    }
                },
                GoalType.MINIMIZE,
                0.0,
                5.0,
                2.5);

        UnivariatePointValuePair maximum = optimizer.optimize(
                1000,
                new UnivariateFunction() {
                    public double value(double x) {
                        return x;
                    }
                },
                GoalType.MAXIMIZE,
                0.0,
                5.0,
                2.5);

        assertTrue(minimum.getPoint() < 1e-6);
        assertEquals(minimum.getPoint(), minimum.getValue(), 0.0);
        assertTrue(maximum.getPoint() > 5.0 - 1e-6);
        assertEquals(maximum.getPoint(), maximum.getValue(), 0.0);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testRejectsTooSmallRelativeThreshold() {
        new BrentOptimizer(Math.ulp(1.0), 1e-10);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testRejectsNonPositiveAbsoluteThreshold() {
        new BrentOptimizer(1e-12, 0.0);
    }
}

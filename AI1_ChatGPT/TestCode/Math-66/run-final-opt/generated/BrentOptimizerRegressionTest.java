package org.apache.commons.math.optimization.univariate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.optimization.GoalType;
import org.junit.Test;

public class BrentOptimizerRegressionTest {

    private static final UnivariateRealFunction QUINTIC = new UnivariateRealFunction() {
        public double value(double x) {
            return (x - 1.0) * (x - 0.5) * x * (x + 0.5) * (x + 1.0);
        }
    };

    private static final UnivariateRealFunction SIN = new UnivariateRealFunction() {
        public double value(double x) {
            return Math.sin(x);
        }
    };

    @Test
    public void testQuinticMinimumHasTriggerAccuracy() throws Exception {
        BrentOptimizer optimizer = new BrentOptimizer();

        double result = optimizer.optimize(QUINTIC, GoalType.MINIMIZE, -0.3, -0.2);

        assertEquals(-0.2719561270319131, result, 1.0e-10);
        assertEquals(QUINTIC.value(result), optimizer.getFunctionValue(), 0.0);
        assertTrue(optimizer.getIterationCount() > 0);
    }

    @Test
    public void testSinMinimumHasTriggerAccuracy() throws Exception {
        BrentOptimizer optimizer = new BrentOptimizer();

        double result = optimizer.optimize(SIN, GoalType.MINIMIZE, 4.0, 5.0);

        assertEquals(4.71238898038469, result, 1.0e-10);
        assertEquals(-1.0, optimizer.getFunctionValue(), 1.0e-14);
        assertTrue(optimizer.getIterationCount() > 0);
    }

    @Test
    public void testExplicitStartValueFindsQuinticMinimum() throws Exception {
        BrentOptimizer optimizer = new BrentOptimizer();

        double result = optimizer.optimize(QUINTIC, GoalType.MINIMIZE,
                                           -0.3, -0.2, -0.25);

        assertEquals(-0.2719561270319131, result, 1.0e-10);
        assertEquals(QUINTIC.value(result), optimizer.getFunctionValue(), 0.0);
    }

    @Test
    public void testMaximizationUsesRequestedGoalType() throws Exception {
        BrentOptimizer optimizer = new BrentOptimizer();

        double result = optimizer.optimize(SIN, GoalType.MAXIMIZE, 0.0, 3.0, 1.0);

        assertEquals(Math.PI / 2.0, result, 1.0e-10);
        assertEquals(1.0, optimizer.getFunctionValue(), 1.0e-14);
    }

    @Test
    public void testReversedBoundsStillLocateBoundaryMinimum() throws Exception {
        BrentOptimizer optimizer = new BrentOptimizer();
        UnivariateRealFunction linear = new UnivariateRealFunction() {
            public double value(double x) {
                return x;
            }
        };

        double result = optimizer.optimize(linear, GoalType.MINIMIZE, 1.0, 0.0);

        assertEquals(0.0, result, 1.0e-8);
        assertEquals(result, optimizer.getFunctionValue(), 0.0);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testZeroRelativeAccuracyIsRejectedDuringOptimization() throws Exception {
        BrentOptimizer optimizer = new BrentOptimizer();
        optimizer.setRelativeAccuracy(0.0);

        optimizer.optimize(SIN, GoalType.MINIMIZE, 4.0, 5.0);
    }
}

package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.junit.Assert;
import org.junit.Test;

public class BrentOptimizerBestPointTest {

    @Test
    public void testMinimizationReturnsBestInitialPointAtDefaultTermination() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);

        UnivariatePointValuePair result = optimizer.optimize(100,
                                                             new UnivariateFunction() {
                                                                 public double value(double x) {
                                                                     return x * x;
                                                                 }
                                                             },
                                                             GoalType.MINIMIZE,
                                                             -1.0, 2.0, 0.0);

        Assert.assertEquals(0.0, result.getPoint(), 1e-8);
        Assert.assertEquals(0.0, result.getValue(), 1e-16);
    }

    @Test
    public void testMaximizationReturnsBestInitialPointAtDefaultTermination() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);

        UnivariatePointValuePair result = optimizer.optimize(100,
                                                             new UnivariateFunction() {
                                                                 public double value(double x) {
                                                                     return -x * x;
                                                                 }
                                                             },
                                                             GoalType.MAXIMIZE,
                                                             -1.0, 2.0, 0.0);

        Assert.assertEquals(0.0, result.getPoint(), 1e-8);
        Assert.assertEquals(0.0, result.getValue(), 1e-16);
    }

    @Test
    public void testConvergenceCheckerDoesNotCauseWorseCurrentPointToBeReturned() {
        final UnivariatePointValuePair[] compared = new UnivariatePointValuePair[2];
        ConvergenceChecker<UnivariatePointValuePair> checker =
            new ConvergenceChecker<UnivariatePointValuePair>() {
                public boolean converged(int iteration,
                                         UnivariatePointValuePair previous,
                                         UnivariatePointValuePair current) {
                    compared[0] = previous;
                    compared[1] = current;
                    return true;
                }
            };

        BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12, checker);
        UnivariatePointValuePair result = optimizer.optimize(100,
                                                             new UnivariateFunction() {
                                                                 public double value(double x) {
                                                                     return x * x;
                                                                 }
                                                             },
                                                             GoalType.MINIMIZE,
                                                             -1.0, 2.0, 0.0);

        Assert.assertNotNull(compared[0]);
        Assert.assertNotNull(compared[1]);
        Assert.assertTrue(compared[1].getValue() > compared[0].getValue());
        Assert.assertEquals(compared[0].getPoint(), result.getPoint(), 0.0);
        Assert.assertEquals(compared[0].getValue(), result.getValue(), 0.0);
    }

    @Test
    public void testFindsInteriorQuadraticMinimum() {
        BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);

        UnivariatePointValuePair result = optimizer.optimize(100,
                                                             new UnivariateFunction() {
                                                                 public double value(double x) {
                                                                     double delta = x - 2.0;
                                                                     return delta * delta;
                                                                 }
                                                             },
                                                             GoalType.MINIMIZE,
                                                             0.0, 4.0, 1.0);

        Assert.assertEquals(2.0, result.getPoint(), 1e-8);
        Assert.assertEquals(0.0, result.getValue(), 1e-16);
    }

    @Test(expected = NotStrictlyPositiveException.class)
    public void testRejectsNonPositiveAbsoluteThreshold() {
        new BrentOptimizer(1e-10, 0.0);
    }

    @Test(expected = NumberIsTooSmallException.class)
    public void testRejectsTooSmallRelativeThreshold() {
        new BrentOptimizer(0.0, 1e-10);
    }
}
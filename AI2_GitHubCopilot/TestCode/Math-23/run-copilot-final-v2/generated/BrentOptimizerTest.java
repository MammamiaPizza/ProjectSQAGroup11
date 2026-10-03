package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.junit.Test;
import static org.junit.Assert.*;

public class BrentOptimizerTest {

 private static final double REL_THRESHOLD = 1e-12;
 private static final double ABS_THRESHOLD = 1e-12;

 @Test
 public void testKeepInitIfBest() {
     // If initial guess is the optimum, it must be returned as best point.
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return x * x; }
     };
     BrentOptimizer opt = new BrentOptimizer(1e-10, 1e-14);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, -5, 5, 0);
     assertEquals("Best point not returned", 0.0, result.getPoint(), 1e-9);
     assertEquals("Best value not returned", 0.0, result.getValue(), 1e-12);
 }

 @Test
 public void testInitNotAtMinimum() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return (x - 2) * (x - 2); }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, -5, 5, 0);
     assertEquals(2.0, result.getPoint(), 1e-6);
     assertEquals(0.0, result.getValue(), 1e-8);
 }

 @Test
 public void testInitAtLowerBound() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return (x - 1) * (x - 1); }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, 1, 5, 1);
     assertEquals(1.0, result.getPoint(), 1e-8);
     assertEquals(0.0, result.getValue(), 1e-8);
 }

 @Test
 public void testInitAtUpperBound() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return (x + 3) * (x + 3); }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, -3, 3, 3);
     assertEquals(-3.0, result.getPoint(), 1e-8);
     assertEquals(0.0, result.getValue(), 1e-8);
 }

 @Test
 public void testMinimumInteriorFarInit() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return (x - 7) * (x - 7) + 5; }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, 0, 20, 2);
     assertEquals(7.0, result.getPoint(), 1e-5);
     assertEquals(5.0, result.getValue(), 1e-8);
 }

 @Test
 public void testMaximization() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return -x * x; }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MAXIMIZE, -5, 5, 2);
     assertEquals(0.0, result.getPoint(), 1e-6);
     assertEquals(0.0, result.getValue(), 1e-8);
 }

 @Test
 public void testCosineMinimum() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return Math.cos(x); }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, 0, 2 * Math.PI, 1);
     assertEquals(Math.PI, result.getPoint(), 1e-5);
     assertEquals(-1.0, result.getValue(), 1e-8);
 }

 @Test
 public void testFunctionWithFlatMinimum() {
     // x^4 has a flat minimum at 0
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return x * x * x * x; }
     };
     BrentOptimizer opt = new BrentOptimizer(REL_THRESHOLD, ABS_THRESHOLD);
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, -3, 3, 1);
     assertEquals(0.0, result.getPoint(), 1e-5);
     assertEquals(0.0, result.getValue(), 1e-8);
 }

 @Test
 public void testEarlyStoppingCheckerPreservesBest() {
     UnivariateFunction f = new UnivariateFunction() {
         public double value(double x) { return x * x; }
     };
     // Stop after 5 iterations; the best point so far must be returned.
     BrentOptimizer opt = new BrentOptimizer(1e-10, 1e-14,
             new ConvergenceChecker<UnivariatePointValuePair>() {
                 private int count;
                 public boolean converged(int iteration,
                                          UnivariatePointValuePair previous,
                                          UnivariatePointValuePair current) {
                     count++;
                     return count >= 5;
                 }
             });
     UnivariatePointValuePair result = opt.optimize(1000, f, GoalType.MINIMIZE, -10, 10, 1);
     assertNotNull(result);
     // The optimum should be near 0 with value near 0
     assertTrue("Best point not near optimum after early stop",
                Math.abs(result.getValue()) <= 0.1);
 }

 @Test(expected = NotStrictlyPositiveException.class)
 public void testNegativeAbsoluteThreshold() {
     new BrentOptimizer(1e-10, -1.0);
 }

 @Test(expected = NumberIsTooSmallException.class)
 public void testTooSmallRelativeThreshold() {
     double tooSmall = 2 * Math.ulp(1d) / 2; // smaller than MIN_RELATIVE_TOLERANCE
     new BrentOptimizer(tooSmall, 1.0);
 }

}

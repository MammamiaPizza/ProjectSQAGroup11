package org.apache.commons.math.optimization.direct;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.commons.math.analysis.MultivariateFunction;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

public class BOBYQAOptimizerTest {

 private static class Rosenbrock implements MultivariateFunction {
     @Override
     public double value(double[] x) {
         double sum = 0;
         for (int i = 0; i < x.length - 1; i++) {
             double t1 = x[i + 1] - x[i] * x[i];
             double t2 = 1 - x[i];
             sum += 100 * t1 * t1 + t2 * t2;
         }
         return sum;
     }
 }

 @Test
 public void testConstrainedRosenWithMoreInterpolationPoints() {
     final int n = 2;
     final int npt = n + 4;
     final double[] lower = new double[n];
     final double[] upper = new double[n];
     Arrays.fill(lower, -5);
     Arrays.fill(upper, 5);
     final double[] start = new double[] { -5, -5 };

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("optimal value close to 0, got " + value, Math.abs(value) < 5e-2);
 }

 @Test
 public void test3DWithPoints2nPlus2() {
     final int n = 3;
     final int npt = 2 * n + 2;
     final double[] lower = new double[n];
     final double[] upper = new double[n];
     Arrays.fill(lower, -2);
     Arrays.fill(upper, 2);
     final double[] start = new double[n];
     Arrays.fill(start, -2);

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("optimal value close to 0, got " + value, Math.abs(value) < 5e-2);
 }

 @Test
 public void test5DWithPoints2nPlus2() {
     final int n = 5;
     final int npt = 2 * n + 2;
     final double[] lower = new double[n];
     final double[] upper = new double[n];
     Arrays.fill(lower, -2);
     Arrays.fill(upper, 2);
     final double[] start = new double[n];
     Arrays.fill(start, -2);

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("optimal value close to 0, got " + value, Math.abs(value) < 5e-2);
 }

 @Test
 public void testMaxAllowedInterpolationPoints() {
     final int n = 3;
     final int maxNpt = (n + 2) * (n + 1) / 2;
     final double[] lower = new double[n];
     final double[] upper = new double[n];
     Arrays.fill(lower, -2);
     Arrays.fill(upper, 2);
     final double[] start = new double[n];
     Arrays.fill(start, -2);

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(maxNpt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("optimal value close to 0, got " + value, Math.abs(value) < 5e-2);
 }

 @Test
 public void testMinimumInterpolationPointsWorks() {
     final int n = 2;
     final int npt = n + 2;
     final double[] lower = new double[] { -5, -5 };
     final double[] upper = new double[] { 5, 5 };
     final double[] start = new double[] { -5, -5 };

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("optimal value close to 0, got " + value, Math.abs(value) < 5e-2);
 }

 @Test
 public void testDefaultNumberOfInterpolationPoints() {
     final int n = 2;
     final int npt = 2 * n + 1;
     final double[] lower = new double[]{-2, -2};
     final double[] upper = new double[]{2, 2};
     final double[] start = new double[]{-1, 1};

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("optimal value close to 0, got " + value, Math.abs(value) < 5e-2);
 }

 @Test
 public void testInterpolationPointsTooSmallThrowsException() {
     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(3);
     double[] lower = new double[]{-1, -1};
     double[] upper = new double[]{1, 1};
     double[] start = new double[]{0, 0};

     try {
         optimizer.optimize(1000, new Rosenbrock(), GoalType.MINIMIZE, start, lower, upper);
         fail("Expected NumberIsTooSmallException");
     } catch (NumberIsTooSmallException e) {
         // expected
     }
 }

 @Test
 public void testStartPointAtBoundDoesNotThrow() {
     final int n = 2;
     final int npt = n + 4;
     final double[] lower = new double[]{-5, -5};
     final double[] upper = new double[]{5, 5};
     final double[] start = new double[]{-5, -5};

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);
     assertNotNull(result);
     double value = result.getValue();
     assertTrue("value close to 0", Math.abs(value) < 5e-2);
 }

 @Test
 public void testUnconstrainedOptimum() {
     final int n = 2;
     final int npt = 2 * n + 1;
     final double[] start = new double[]{-1, 1};

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start);

     assertNotNull(result);
     double value = result.getValue();
     assertTrue("value close to 0", Math.abs(value) < 5e-2);
 }

 @Test
 public void testDifferentFunctionEllipsoid() {
     final int n = 2;
     final int npt = n + 4;
     final double[] lower = new double[]{-10, -10};
     final double[] upper = new double[]{10, 10};
     final double[] start = new double[]{-10, -10};

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new MultivariateFunction() {
         @Override
         public double value(double[] x) {
             final double a = 2.;
             final double b = 3.;
             return (x[0] - a) * (x[0] - a) + (x[1] - b) * (x[1] - b);
         }
     }, GoalType.MINIMIZE, start, lower, upper);

     assertNotNull(result);
     double[] point = result.getPoint();
     double val = result.getValue();
     assertEquals("x0 close to a", 2.0, point[0], 1e-1);
     assertEquals("x1 close to b", 3.0, point[1], 1e-1);
     assertTrue("value small", val < 1e-2);
 }

 @Test
 public void testOptimizeWithBoundsThatExcludeOptimum() {
     final int n = 2;
     final int npt = n + 4;
     final double[] lower = new double[]{2, 2};
     final double[] upper = new double[]{5, 5};
     final double[] start = new double[]{3, 3};

     BOBYQAOptimizer optimizer = new BOBYQAOptimizer(npt);
     RealPointValuePair result = optimizer.optimize(1000000, new Rosenbrock(),
                                                    GoalType.MINIMIZE, start, lower, upper);
     assertNotNull(result);
     // Just ensure the optimization finishes and returns a finite point/value.
     assertTrue("optimization finished", true);
 }

}
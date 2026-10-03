package org.apache.commons.math3.optimization.univariate;

 import org.apache.commons.math3.analysis.UnivariateFunction;
 import org.apache.commons.math3.exception.NumberIsTooSmallException;
 import org.apache.commons.math3.exception.NotStrictlyPositiveException;
 import org.apache.commons.math3.optimization.ConvergenceChecker;
 import org.apache.commons.math3.optimization.GoalType;
 import org.junit.Assert;
 import org.junit.Test;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * Tests for {@link BrentOptimizer} covering the MATH-855 bug:
  * the optimizer must return the best point, not the most recently evaluated one.
  */
 public class BrentOptimizerTest {

     /**
      * A simple quadratic with minimum at x=0.3 with value 0.
      * Evaluations are logged so that the returned point can be compared
      * with all visited points.  The buggy optimizer may return a point
      * that is not the best among those evaluated.
      */
     @Test
     public void testMath855() {
         final UnivariateFunction f = new UnivariateFunction() {
             @Override
             public double value(double x) {
                 return (x - 0.3) * (x - 0.3);
             }
         };
         final EvaluationLogger logger = new EvaluationLogger(f);
         final BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
         final UnivariatePointValuePair result =
                 optimizer.optimize(200, logger, GoalType.MINIMIZE, 0, 1, 0.5);

         // Determine the best point among all evaluations.
         double bestValue = Double.POSITIVE_INFINITY;
         double bestX = Double.NaN;
         for (int i = 0; i < logger.getValues().size(); i++) {
             double v = logger.getValues().get(i);
             if (v < bestValue) {
                 bestValue = v;
                 bestX = logger.getPoints().get(i);
             }
         }

         // The returned point must be as good as the best visited point.
         Assert.assertEquals("Returned value is not the best", bestValue, result.getValue(), 1e-15);
         Assert.assertEquals("Returned point is not the best", bestX, result.getPoint(), 1e-10);
     }

     /**
      * Tests minimization of a simple quadratic. The optimum is known analytically.
      */
     @Test
     public void testMinimizeQuadratic() {
         final UnivariateFunction f = new UnivariateFunction() {
             @Override
             public double value(double x) {
                 return (x - 2.0) * (x - 2.0);
             }
         };
         final BrentOptimizer optimizer = new BrentOptimizer(1e-12, 1e-12);
         final UnivariatePointValuePair result =
                 optimizer.optimize(100, f, GoalType.MINIMIZE, 0, 4, 1);
         Assert.assertNotNull(result);
         Assert.assertEquals(2.0, result.getPoint(), 1e-8);
         Assert.assertEquals(0.0, result.getValue(), 1e-12);
     }

     /**
      * Tests maximization of a negated quadratic.
      */
     @Test
     public void testMaximizeQuadratic() {
         final UnivariateFunction f = new UnivariateFunction() {
             @Override
             public double value(double x) {
                 return -x * x + 4 * x;   // maximum at x=2, value 4
             }
         };
         final BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);
         final UnivariatePointValuePair result = optimizer.optimize(100, f, GoalType.MAXIMIZE, 0, 4,
1);
         Assert.assertNotNull(result);
         Assert.assertEquals(2.0, result.getPoint(), 1e-8);
         Assert.assertEquals(4.0, result.getValue(), 1 e-10);
     }

     /**
      * Tests that the optimizer rejects a relative tolerance below the minimum allowed.
      */
     @Test(expected = NumberIsTooSmallException.class)
     public void testConstructorTooSmallRelativeTolerance() {
         new BrentOptimizer(1e-16, 1e-10);
     }

     /**
      * Tests that the optimizer rejects a non-positive absolute tolerance.
      */
     @Test(expected = NotStrictlyPositiveException.class)
     public void testConstructorNonPositiveAbsoluteTolerance() {
         new BrentOptimizer(1e-10, 0);
     }

     /**
      * Tests that the goal type is reported correctly.
      */
     @Test
     public void testGetGoalType() {
         BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);
         UnivariatePointValuePair result =
                 optimizer.optimize(50, new SimpleQuadratic(), GoalType.MAXIMIZE, -1, 3, 0);
         Assert.assertEquals(GoalType.MAXIMIZE, optimizer.getGoalType());
     }

     /**
      * Tests that the lower and upper bounds are stored.
      */
     @Test
     public void testGetMinMax() {
         BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);
         optimizer.optimize(50, new SimpleQuadratic(), GoalType.MINIMIZE, -3, 5, 0);
         Assert.assertEquals(-3.0, optimizer.getMin(), 0);
         Assert.assertEquals(5.0, optimizer.getMax(), 0);
     }

     /**
      * Tests optimization with a custom convergence checker that stops after a fixed number
      * of iterations.  The last evaluated point is returned directly.
      */
     @Test
     public void testCustomCconvergenceChecker() {
         final SimpleQuadratic f = new SimpleQuadratic();
         final int maxIterations = 5;
         ConvergenceChecker<UnivariatePointValuePair> checker =
                 new ConvergenceChecker<UnivariatePointValuePair>() {
                     public boolean converged(int iteration,
                                             UnivariatePointValuePair previous,
                                              UnivariatePointValuePair current) {
                         return iteration >= maxIterations;
                     }
                 };
         BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12, checker);
         UnivariatePointValuePair result =
                 optimizer.optimize(100, f, GoalType.MINIMIZE, -5, 5, 2);
         Assert.assertNotNull(result);
         // With early termination the returned point may not be the global optimum,
         // but it should be the point from the last accepted iteration.
         Assert.assertTrue("Result point should be within bounds",
                           result.getPoint() >= -5 && result.getPoint() <= 5);
     }

     /**
      * With a single-point search interval, the optimizer should terminate immediately.
      */
     @Test
     public void testDegenerateInterval() {
         final UnivariateFunction f = new UnivariateFunction() {
             @Override
             public double value(double x) {
                 return x * x;
             }
         };
         BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1 e-12);
         UnivariatePointValuePair result =
                 optimizer.optimize(50, f, GoalType.MINIMIZE, 3, 3, 3);
         Assert.assertEquals(3.0, result.getPoint(), 0);
         Assert.assertEquals(9.0, result.getValue(),)
     }

     /**
      * The best-point property is tested with a function that is steep on one side
      * and shallow on the other, making it more likely that the last evaluated point
      * is not the optimum.
      */
     @Test
     public void testBestPointReturned() {
         final UnivariateFunction f = new UnivariateFunction() {
             @Override
             public double value(double x) {
                 // Asymmetric quadratic: steeper for x<0.5, shallower for x>0.5.
                 // Minimum at x=0.5 with value 0.
                 if (x <= 0.5) {
                     return 10 * (x - 0.5) * (x - 0.5);
                 } else {
                     return (x - 0.5) * (x - 0.5);
                 }
             }
         };
         EvaluationLogger logger = new EvaluationLogger(f);
         BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
         UnivariatePointValuePair result =
                 optimizer.optimize(300, logger, GoalType.MINIMIZE, 0, 1, 0.2);

         double bestValue = Double.POSITIVE_INFINITY;
         double bestX = Double.NaN;
         for (int i = 0; i < logger.getValues().size(); i++) {
             double v = logger.getValues().get(i);
             if (v < bestValue) {                bestValue = v;
                 bestX = logger.getPoints().get(i);
             }
         }
         Assert.assertEquals(bestValue, result.getValue(), 1e-15);
         Assert.assertEquals(bestX, result.getPoint(), 1e-10);
     }

     /**
      * Negative case: the optimizer must throw when no data has been set.
      */
     @Test(expected = NullPointerException.class)
     public void testOptimizeWithoutFunction() {
         BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-12);
         optimizer.optimize(50, null, GoalType.MINIMIZE, 0, 1, 0);
     }

     // Helper that logs every evaluation.
     private static class EvaluationLogger implements UnivariateFunction {
         private final UnivariateFunction delegate;
         private final List<Double> points = new ArrayList<Double>();
         private final List<Double> values = new ArrayList<Double>();

         EvaluationLogger(UnivariateFunction delegate) {
             this.delegate = delegate;
         }

         @Override
         public double value(double x) {
             double v = delegate.value(x);
             points.add(x);
             values.add(v);
             return v;
         }

         List<Double> getPoints() { return points; }
         List<Double> getValues() { return values; }
     }

     // A simple quadratic with minimum at x=0, value 0.
     private static class SimpleQuadratic implements UnivariateFunction {
         @Override
         public double value(double x) {
             return x * x;
         }
     }
 }

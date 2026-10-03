package org.apache.commons.math3.optimization.direct;

 import java.util.List;

 import org.apache.commons.math3.analysis.MultivariateFunction;
 import org.apache.commons.math3.linear.RealMatrix;
 import org.apache.commons.math3.optimization.GoalType;
 import org.apache.commons.math3.optimization.PointValuePair;
 import org.apache.commons.math3.random.MersenneTwister;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for {@link CMAESOptimizer} targeting boundary-related behavior
  * (MATH-867: fitness accuracy degraded when boundaries are present).
  */
 public class CMAESOptimizerTest {

     private static MultivariateFunction quadratic(final double[] target) {
         return new MultivariateFunction() {
             public double value(double[] point) {
                 double sum = 0;
                 for (int i = 0; i < point.length; i++) {
                     double d = point[i] - target[i];
                     sum += d * d;
                 }
                 return sum;
             }
         };
     }

     private static CMAESOptimizer optimizer(int lambda, double[] sigma, int maxIter, long seed) {
         return new CMAESOptimizer(lambda, sigma, maxIter, 0,
                 true, 0, 0, new MersenneTwister(seed), false);
     }

     /**
      * MATH-867: without boundaries the optimizer finds the true optimum (~11.1).
      * When boundaries that include the optimum are present, the bug causes
      * convergence to an incorrect boundary-influenced value.
      */
     @Test
     public void testFitAccuracyDependsOnBoundary() {
         double[] target = {11.1};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(12, new double[]{5.0}, 100000, 12345);

         double[] start = {10.0};
         double[] lb = {0.0};
         double[] ub = {30.0}; // bounds contain the unconstrained optimum

         PointValuePair result = opt.optimize(100000, f, GoalType.MINIMIZE, start, lb, ub);

         // Optimum should be near 11.1 because bounds [0,30] include it.
         // Bug MATH-867 causes the result to degrade toward a boundary (~8.0).
         Assert.assertEquals(11.1, result.getPoint()[0], 1.0);
         Assert.assertEquals(0.0, result.getValue(), 0.5);
     }

     /** Unconstrained optimization converges to the true optimum. */
     @Test
     public void testUnconstrainedOptimization() {
         double[] target = {5.0, 3.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(8, new double[]{2.0, 2.0}, 50000, 42);

         double[] start = {0.0, 0.0};
         PointValuePair result = opt.optimize(40000, f, GoalType.MINIMIZE, start);

         Assert.assertEquals(target[0], result.getPoint()[0], 0.5);
         Assert.assertEquals(target[1], result.getPoint()[1], 0.5);
         Assert.assertEquals(0.0, result.getValue(), 0.01);
     }

     /** When the true optimum lies outside the bounds, converge to the nearest boundary. */
     @Test
     public void testConstrainedOptimumAtBoundary() {
         double[] target = {20.0}; // optimum at 20, outside [0, 10]
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(10, new double[]{3.0}, 50000, 99);

         double[] start = {5.0};
         double[] lb = {0.0};
         double[] ub = {10.0};

         PointValuePair result = opt.optimize(40000, f, GoalType.MINIMIZE, start, lb, ub);

         // Nearest feasible point to the unconstrained optimum (20) is 10.
         Assert.assertEquals(10.0, result.getPoint()[0], 0.5);
     }

     /** Start point outside bounds must be repaired to a feasible point. */
     @Test
     public void testStartOutsideBounds() {
         double[] target = {3.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(10, new double[]{1.0}, 30000, 77);

         double[] start = {100.0};
         double[] lb = {-5.0};
         double[] ub = {10.0};

         PointValuePair result = opt.optimize(30000, f, GoalType.MINIMIZE, start, lb, ub);

         double pt = result.getPoint()[0];
         Assert.assertTrue("below lower bound", pt >= lb[0]);
         Assert.assertTrue("above upper bound", pt <= ub[0]);
         Assert.assertEquals(3.0, pt, 1.0);
     }

     /** Very tight bounds still allow convergence to the constrained optimum. */
     @Test
     public void testTightBounds() {
         double[] target = {5.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(6, new double[]{0.1}, 20000, 33);

         double[] start = {4.9};
         double[] lb = {4.5};
         double[] ub = {5.5};

         PointValuePair result = opt.optimize(20000, f, GoalType.MINIMIZE, start, lb, ub);

         double pt = result.getPoint()[0];
         Assert.assertTrue(pt >= lb[0]);
         Assert.assertTrue(pt <= ub[0]);
         Assert.assertEquals(5.0, pt, 0.3);
     }

     /** Zero-range bounds force the optimizer onto the only feasible point. */
     @Test
     public void testZeroRangeBounds() {
         double[] target = {10.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(5, new double[]{0.5}, 10000, 55);

         double[] start = {7.0};
         double[] lb = {7.0};
         double[] ub = {7.0};

         PointValuePair result = opt.optimize(5000, f, GoalType.MINIMIZE, start, lb, ub);

         Assert.assertEquals(7.0, result.getPoint()[0], 0.01);
     }

     /** Large initial sigma still converges given enough iterations. */
     @Test
     public void testDifferentInputSigma() {
         double[] target = {0.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(8, new double[]{50.0}, 100000, 11);

         double[] start = {100.0};
         PointValuePair result = opt.optimize(80000, f, GoalType.MINIMIZE, start);

         Assert.assertEquals(0.0, result.getPoint()[0], 2.0);
     }

     /** When max iterations is very low, the optimizer returns the best-so-far. */
     @Test
     public void testMaxIterationsReached() {
         double[] target = {100.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(5, new double[]{1.0}, 20, 123);

         double[] start = {0.0};
         PointValuePair result = opt.optimize(100000, f, GoalType.MINIMIZE, start);

         Assert.assertNotNull(result);
         Assert.assertNotNull(result.getPoint());
     }

     /** Statistics are collected when generateStatistics is enabled. */
     @Test
     public void testStatisticsCollection() {
         double[] target = {2.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = new CMAESOptimizer(6, new double[]{0.5}, 5000, 0,
                 true, 0, 0, new MersenneTwister(42), true);

         double[] start = {0.0};
         opt.optimize(5000, f, GoalType.MINIMIZE, start);

         List<Double> sigmaHist = opt.getStatisticsSigmaHistory();
         List<RealMatrix> meanHist = opt.getStatisticsMeanHistory();
         List<Double> fitHist = opt.getStatisticsFitnessHistory();
         List<RealMatrix> dHist = opt.getStatisticsDHistory();

         Assert.assertNotNull(sigmaHist);
         Assert.assertNotNull(meanHist);
         Assert.assertNotNull(fitHist);
         Assert.assertNotNull(dHist);
         Assert.assertFalse(sigmaHist.isEmpty());
         Assert.assertFalse(fitHist.isEmpty());
     }

     /** Without generateStatistics all statistic lists are empty. */
     @Test
     public void testNoStatisticsByDefault() {
         double[] target = {1.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(5, new double[]{1.0}, 1000, 7);

         double[] start = {0.0};
         opt.optimize(1000, f, GoalType.MINIMIZE, start);

         Assert.assertTrue(opt.getStatisticsSigmaHistory().isEmpty());
         Assert.assertTrue(opt.getStatisticsMeanHistory().isEmpty());
         Assert.assertTrue(opt.getStatisticsFitnessHistory().isEmpty());
         Assert.assertTrue(opt.getStatisticsDHistory().isEmpty());
     }

     /** Multidimensional bounded optimization respects all bounds. */
     @Test
     public void testMultidimensionalWithBounds() {
         double[] target = {4.0, -2.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(12, new double[]{2.0, 2.0}, 50000, 22);

         double[] start = {0.0, 0.0};
         double[] lb = {-5.0, -5.0};
         double[] ub = {10.0, 10.0};

         PointValuePair result = opt.optimize(40000, f, GoalType.MINIMIZE, start, lb, ub);

         double[] pt = result.getPoint();
         Assert.assertTrue(pt[0] >= lb[0] && pt[0] <= ub[0]);
         Assert.assertTrue(pt[1] >= lb[1] && pt[1] <= ub[1]);
         Assert.assertEquals(target[0], pt[0], 1.0);
         Assert.assertEquals(target[1], pt[1], 1.0);
     }

     /** Every component of the returned point respects its bounds. */
     @Test
     public void testBoundaryRepairKeepsPointFeasible() {
         double[] target = {3.0, 3.0, 3.0};
         MultivariateFunction f = quadratic(target);
         CMAESOptimizer opt = optimizer(10, new double[]{2.0, 2.0, 2.0}, 30000, 5);

         double[] start = {0.0, 0.0, 0.0};
         double[] lb = {-2.0, 0.0, 1.0};
         double[] ub = {8.0, 6.0, 5.0};

         PointValuePair result = opt.optimize(30000, f, GoalType.MINIMIZE, start, lb, ub);

         double[] pt = result.getPoint();
         for (int i = 0; i < pt.length; i++) {
             Assert.assertTrue("dim " + i + " < lower", pt[i] >= lb[i]);
             Assert.assertTrue("dim " + i + " > upper", pt[i] <= ub[i]);
         }
     }
 }

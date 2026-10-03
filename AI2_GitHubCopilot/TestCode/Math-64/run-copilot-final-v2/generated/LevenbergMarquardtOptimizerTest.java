package org.apache.commons.math.optimization.general;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.util.Arrays;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
 import org.apache.commons.math.analysis.MultivariateMatrixFunction;
 import org.apache.commons.math.optimization.OptimizationException;
 import org.apache.commons.math.optimization.VectorialPointValuePair;
 import org.junit.Test;

 /**
  * Tests for {@link LevenbergMarquardtOptimizer} that expose the convergence
  * accuracy bug (MATH-405).  Every test uses assertions against known
  * reference values from the Minpack project, or exercises the optimizer
  * under configurations that may trigger the faulty code path.
  */
 public class LevenbergMarquardtOptimizerTest {

     // ---------- helper --------------------------------------------------------

     private static double sumSquares(final double[] v) {
         double s = 0.0;
         for (final double d : v) {
             s += d * d;
         }
         return s;
     }

     // ---------- Minpack problem implementations -------------------------------

     /**
      * Jennrich &amp; Sampson (1968) function (m=10, n=2).
      * Reference optimum objective  = 0.2578199266368004
      */
     private static final class JennrichSampson
             implements DifferentiableMultivariateVectorialFunction {

         private static final int M = 10;

         @Override
         public double[] value(final double[] b) throws FunctionEvaluationException {
             final double[] out = new double[M];
             for (int i = 0; i < M; i++) {
                 final double j = i + 1.0;                 // 1 .. 10
                 out[i] = 2.0 + 2.0 * j
                         - Math.exp(j * b[0]) - Math.exp(j * b[1]);
             }
             return out;
         }

         @Override
         public MultivariateMatrixFunction jacobian() {
             return new MultivariateMatrixFunction() {
                 @Override
                 public double[][] value(final double[] b)
                         throws FunctionEvaluationException {
                     final double[][] jac = new double[M][2];
                     for (int i = 0; i < M; i++) {
                         final double j = i + 1.0;
                         jac[i][0] = -j * Math.exp(j * b[0]);
                         jac[i][1] = -j * Math.exp(j * b[1]);
                     }
                     return jac;
                 }
             };
         }

     }

     /**
      * Freudenstein and Roth function (m=2, n=2).
      * Reference optimum objective = 11.41300466147456
      */
     private static final class FreudensteinRoth
             implements DifferentiableMultivariateVectorialFunction {

         @Override
         public double[] value(final double[] b) throws FunctionEvaluationException {
             final double x1 = b[0];
             final double x2 = b[1];
             return new double[] {
                     -13.0 + x1 + ((5.0 - x2) * x2 - 2.0) * x2,
                     -29.0 + x1 + ((x2 + 1.0) * x2 - 14.0) * x2
             };
         }

         @Override
         public MultivariateMatrixFunction jacobian() {
             return new MultivariateMatrixFunction() {
                 @Override
                 public double[][] value(final double[] b)
                         throws FunctionEvaluationException {
                     final double x2 = b[1];
                     final double[][] jac = new double[2][2];
                     jac[0][0] = 1.0;
                     jac[0][1] = 10.0 * x2 - 3.0 * x2 * x2 - 2.0;
                     jac[1][0] = 1.0;
                     jac[1][1] = 3.0 * x2 * x2 + 2.0 * x2 - 14.0;
                     return jac;
                 }
             };
         }

     }

     // ---------- fault-related tests ------------------------------------------

     @Test
     public void testJennrichSampsonDefault() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();

         final DifferentiableMultivariateVectorialFunction func =
                 new JennrichSampson();
         final double[] start   = { 0.3, 0.4 };
         final double[] target  = new double[10];
         final double[] weights = new double[10];
         Arrays.fill(weights, 1.0);

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);

         final double cost = sumSquares(func.value(result.getPoint()));
         assertEquals(0.2578199266368004, cost, 1e-10);
     }

     @Test
     public void testFreudensteinRothDefault() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();

         final DifferentiableMultivariateVectorialFunction func =
                 new FreudensteinRoth();
         final double[] start   = { 0.5, -2.0 };
         final double[] target  = new double[2];
         final double[] weights = { 1.0, 1.0 };

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);

         final double cost = sumSquares(func.value(result.getPoint()));
         assertEquals(11.41300466147456, cost, 1e-10);
     }

     @Test
     public void testJennrichSampsonCustomQRThreshold() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.setQRRankingThreshold(1.0e-11);

         final DifferentiableMultivariateVectorialFunction func =
                 new JennrichSampson();
         final double[] start   = { 0.3, 0.4 };
         final double[] target  = new double[10];
         final double[] weights = new double[10];
         Arrays.fill(weights, 1.0);

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);

         final double cost = sumSquares(func.value(result.getPoint()));
         assertTrue("cost out of range: " + cost,
                    Math.abs(cost - 0.2578199266368004) < 1e-6);
     }

     @Test
     public void testFreudensteinRothCustomStepBound() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.setInitialStepBoundFactor(1.0);

         final DifferentiableMultivariateVectorialFunction func =
                 new FreudensteinRoth();
         final double[] start   = { 0.5, -2.0 };
         final double[] target  = new double[2];
         final double[] weights = { 1.0, 1.0 };

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);

         final double cost = sumSquares(func.value(result.getPoint()));
         // The buggy version may deviate more; use a slightly wider but
         // still revealing tolerance.
         assertEquals(11.41300466147456, cost, 1e-6);
     }

     // ---------- boundary / extreme tolerances ---------------------------------

     @Test
     public void testZeroOrthoTolerance() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.setOrthoTolerance(0.0);

         final DifferentiableMultivariateVectorialFunction func =
                 new JennrichSampson();
         final double[] start   = { 0.3, 0.4 };
         final double[] target  = new double[10];
         final double[] weights = new double[10];
         Arrays.fill(weights, 1.0);

         // must not hang nor throw
         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);
         final double cost = sumSquares(func.value(result.getPoint()));
         assertTrue("cost should be finite and reasonable",
                    cost > 0.0 && cost < 100.0);
     }

     @Test
     public void testExtremeCostRelativeTolerance() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.setCostRelativeTolerance(1.0e-20);

         final DifferentiableMultivariateVectorialFunction func =
                 new FreudensteinRoth();
         final double[] start   = { 0.5, -2.0 };
         final double[] target  = new double[2];
         final double[] weights = { 1.0, 1.0 };

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);
         final double cost = sumSquares(func.value(result.getPoint()));
         assertTrue(cost > 0.0);
     }

     @Test
     public void testExtremeParRelativeTolerance() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.setParRelativeTolerance(1.0e-20);

         final DifferentiableMultivariateVectorialFunction func =
                 new FreudensteinRoth();
         final double[] start   = { 0.5, -2.0 };
         final double[] target  = new double[2];
         final double[] weights = { 1.0, 1.0 };

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);
         final double cost = sumSquares(func.value(result.getPoint()));
         assertTrue(cost > 0.0);
     }

     // ---------- invalid / exception branches ---------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testNullFunction() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.optimize(null,
                            new double[] { 0.0 },
                            new double[] { 1.0 },
                            new double[] { 0.3, 0.4 });
     }

     @Test(expected = OptimizationException.class)
     public void testMismatchedTargetDimensions() throws Exception {
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         final DifferentiableMultivariateVectorialFunction func =
                 new JennrichSampson();
         // target too long – expect error from super class or optimizer
         optimizer.optimize(func,
                            new double[20],   // wrong length
                            new double[10],
                            new double[] { 0.3, 0.4 });
     }

     @Test(expected = OptimizationException.class)
     public void testFunctionReturningNaN() throws Exception {
         final DifferentiableMultivariateVectorialFunction nanFunc =
                 new DifferentiableMultivariateVectorialFunction() {
                     @Override
                     public double[] value(final double[] b) {
                         return new double[] { Double.NaN, Double.NaN };
                     }
                     @Override
                     public MultivariateMatrixFunction jacobian() {
                         return new MultivariateMatrixFunction() {
                             @Override
                             public double[][] value(final double[] b) {
                                 return new double[][] {
                                     { Double.NaN, Double.NaN },
                                     { Double.NaN, Double.NaN }
                                 };
                             }
                         };
                     }
                 };
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.optimize(nanFunc,
                            new double[2], new double[] { 1.0, 1.0 },
                            new double[] { 0.0, 0.0 });
     }

     @Test(expected = OptimizationException.class)
     public void testFunctionReturningInfinity() throws Exception {
         final DifferentiableMultivariateVectorialFunction infFunc =
                 new DifferentiableMultivariateVectorialFunction() {
                     @Override
                     public double[] value(final double[] b) {
                         return new double[] {
                             Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
                         };
                     }
                     @Override
                     public MultivariateMatrixFunction jacobian() {
                         return new MultivariateMatrixFunction() {
                             @Override
                             public double[][] value(final double[] b) {
                                 return new double[][] {
                                     { 1.0, 0.0 },
                                     { 0.0, 1.0 }
                                 };
                             }
                         };
                     }
                 };
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         optimizer.optimize(infFunc,
                            new double[2], new double[] { 1.0, 1.0 },
                            new double[] { 0.0, 0.0 });
     }

     // ---------- trivial convex problem to confirm solver works at all ---------

     @Test
     public void testSimpleParabola() throws Exception {
         // f_i(x) = x - a       (m=3, n=1)
         final DifferentiableMultivariateVectorialFunction func =
                 new DifferentiableMultivariateVectorialFunction() {
                     @Override
                     public double[] value(final double[] b) {
                         final double x = b[0];
                         return new double[] { x - 2.0, x - 3.0, x - 4.0 };
                     }
                     @Override
                     public MultivariateMatrixFunction jacobian() {
                         return new MultivariateMatrixFunction() {
                             @Override
                             public double[][] value(final double[] b) {
                                 return new double[][] { { 1.0 }, { 1.0 }, { 1.0 } };
                             }
                         };
                     }
                 };
         final LevenbergMarquardtOptimizer optimizer =
                 new LevenbergMarquardtOptimizer();
         final double[] start   = { 0.0 };
         final double[] target  = new double[3];
         final double[] weights = { 1.0, 1.0, 1.0 };

         final VectorialPointValuePair result =
                 optimizer.optimize(func, target, weights, start);
         final double[] point = result.getPoint();
         assertEquals(1, point.length);
         // Average of 2,3,4 => 3
         assertEquals(3.0, point[0], 1e-10);
     }

 }

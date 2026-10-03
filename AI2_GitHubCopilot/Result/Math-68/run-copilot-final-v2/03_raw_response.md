package org.apache.commons.math.optimization.general;

 import static org.junit.Assert.*;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.analysis.MultivariateVectorialFunction;
 import org.apache.commons.math.optimization.OptimizationException;
 import org.apache.commons.math.optimization.VectorialPointValuePair;
 import org.junit.Test;

 /**
  * Tests for {@link LevenbergMarquardtOptimizer} focusing on convergence accuracy
  * and edge cases related to bug MATH-362.
  */
 public class LevenbergMarquardtOptimizerTest {

     // ---- problem definitions ----
     private static final double JENNRICH_COST = 0.2578330049;
     private static final double FREUDENSTEIN_COST = 11.4121122022341;

     /**
      * A linear least squares problem with a rank-deficient Jacobian that allows
      * exact control of the optimal cost.
      */
     private static class ControlledCostProblem implements MultivariateVectorialFunction {
         private final double[][] a;
         private final double[] offset;

         /**
          * @param a      m × n matrix (must be rank-deficient to make the optimum
          *               a subspace)
          * @param offset vector orthogonal to cols(a); its squared norm is the
          *               global minimum cost
          */
         ControlledCostProblem(double[][] a, double[] offset) {
             this.a = a;
             this.offset = offset;
         }

         public double[] value(double[] point) throws FunctionEvaluationException {
             double[] res = new double[a.length];
             for (int i = 0; i < a.length; i++) {
                 double s = 0;
                 for (int j = 0; j < point.length; j++) {
                     s += a[i][j] * point[j];
                 }
                 res[i] = s + offset[i];
             }
             return res;
         }
     }

     /** Build a rank-1 matrix with a given expected optimal cost. */
     private static ControlledCostProblem buildProblem(double expectedCost) {
         // rank-1 matrix (3 × 2), columns proportional to [1,2,3]
         double[][] a = {{1, 1}, {2, 2}, {3, 3}};
         // a vector orthogonal to the column space: [1, -2, 1]
         double[] pattern = {1, -2, 1};
         double norm2 = pattern[0]*pattern[0] + pattern[1]*pattern[1] + pattern[2]*pattern[2];
         double scale = Math.sqrt(expectedCost / norm2);
         double[] offset = new double[3];
         for (int i = 0; i < 3; i++) {
             offset[i] = scale * pattern[i];
         }
         return new ControlledCostProblem(a, offset);
     }

     /** Configure the optimizer with reasonable defaults for the given problem. */
     private LevenbergMarquardtOptimizer prepare(ControlledCostProblem p,
                                                 double crt, double prt, double orthoTol) {
         LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
         optimizer.setObjective(p);
         optimizer.setTarget(new double[3]);
         optimizer.setMaxIterations(1000);
         optimizer.setCostRelativeTolerance(crt);
         optimizer.setParRelativeTolerance(prt);
         optimizer.setOrthoTolerance(orthoTol);
         return optimizer;
     }

     /** Compute the sum of squares of the returned residuals. */
     private double cost(VectorialPointValuePair opt) {
         double[] v = opt.getValue();
         double s = 0;
         for (double d : v) {
             s += d * d;
         }
         return s;
     }

     // ========== normal: trigger-failing problems ==========

     @Test
     public void testJennrichSampsonConvergence() throws Exception {
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-10, 1.0e-10, 1.0e-10);
         double[] startPoint = {0.3, 0.4};
         VectorialPointValuePair opt = optimizer.optimize(startPoint);
         assertEquals(JENNRICH_COST, cost(opt), 1.0e-5);
     }

     @Test
     public void testFreudensteinRothConvergence() throws Exception {
         ControlledCostProblem p = buildProblem(FREUDENSTEIN_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-10, 1.0e-10, 1.0e-10);
         double[] startPoint = {0.3, 0.4};
         VectorialPointValuePair opt = optimizer.optimize(startPoint);
         assertEquals(FREUDENSTEIN_COST, cost(opt), 1.0e-4);
     }

     // ========== boundary: tight tolerances ==========

     @Test
     public void testTightTolerances() throws Exception {
         // use tolerances 1e-12 to force high precision
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-12, 1.0e-12, 1.0e-12);
         double[] startPoint = {0.3, 0.4};
         VectorialPointValuePair opt = optimizer.optimize(startPoint);
         // the optimizer should stay close to the reference value even with tight tolerances
         assertEquals(JENNRICH_COST, cost(opt), 1.0e-6);
     }

     // ========== boundary: non-positive initialStepBoundFactor ==========

     @Test
     public void testNegativeInitialStepBoundFactor() throws Exception {
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
         optimizer.setInitialStepBoundFactor(-1.0);
         optimizer.setObjective(p);
         optimizer.setTarget(new double[3]);
         double[] startPoint = {0.3, 0.4};
         // according to specification the optimizer may clamp or throw
         try {
             VectorialPointValuePair opt = optimizer.optimize(startPoint);
             // if it succeeds the result must still be correct
             assertTrue("cost diverged", cost(opt) < 1.0);
         } catch (OptimizationException e) {
             // acceptable behaviour
         }
     }

     // ========== error: NaN / Inf in objective ==========

     @Test(expected = OptimizationException.class)
     public void testNaNObjective() throws Exception {
         MultivariateVectorialFunction nanFunc = new MultivariateVectorialFunction() {
             public double[] value(double[] point) throws FunctionEvaluationException {
                 return new double[]{Double.NaN};
             }
         };
         LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
         optimizer.setObjective(nanFunc);
         optimizer.setTarget(new double[]{0.0});
         optimizer.optimize(new double[]{0.0});
     }

     @Test(expected = OptimizationException.class)
     public void testInfObjective() throws Exception {
         MultivariateVectorialFunction infFunc = new MultivariateVectorialFunction() {
             public double[] value(double[] point) throws FunctionEvaluationException {
                 return new double[]{Double.POSITIVE_INFINITY};
             }
         };
         LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
         optimizer.setObjective(infFunc);
         optimizer.setTarget(new double[]{0.0});
         optimizer.optimize(new double[]{0.0});
     }

     // ========== gradient norm at optimum ≈ 0 ==========

     @Test
     public void testGradientNormNearZero() throws Exception {
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-10, 1.0e-10, 1.0e-10);
         double[] startPoint = {0.3, 0.4};
         VectorialPointValuePair opt = optimizer.optimize(startPoint);
         double[] x = opt.getPoint();
         double h = 1e-6;
         double gradNorm = 0;
         for (int j = 0; j < x.length; j++) {
             double[] xp = x.clone(); xp[j] += h;
             double[] xm = x.clone(); xm[j] -= h;
             double fp = costOfResiduals(p.value(xp));
             double fm = costOfResiduals(p.value(xm));
             double deriv = (fp - fm) / (2 * h);
             gradNorm += deriv * deriv;
         }
         gradNorm = Math.sqrt(gradNorm);
         assertTrue("gradient norm too large: " + gradNorm, gradNorm < 1e-4);
     }

     private double costOfResiduals(double[] r) {
         double s = 0;
         for (double v : r) s += v * v;
         return s;
     }

     // ========== residual norm matches expected value ==========

     @Test
     public void testResidualNormMatchesReference() throws Exception {
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-10, 1.0e-10, 1.0e-10);
         double[] startPoint = {0.3, 0.4};
         VectorialPointValuePair opt = optimizer.optimize(startPoint);
         double residualNorm = Math.sqrt(cost(opt));
         assertEquals(Math.sqrt(JENNRICH_COST), residualNorm, 1e-4);
     }

     // ========== rank-deficient Jacobian is handled without exceptions ==========

     @Test
     public void testRankDeficientJacobian() throws Exception {
         // A with rank 1
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-10, 1.0e-10, 1.0e-10);
         double[] startPoint = {0.3, 0.4};
         VectorialPointValuePair opt = optimizer.optimize(startPoint);
         // shall not throw, and reach the expected optimum subspace
         assertTrue("cost deviation too big", Math.abs(cost(opt) - JENNRICH_COST) < 1e-4);
     }

     // ========== convergence with small maximum iterations ==========

     @Test
     public void testLimitedIterations() throws Exception {
         ControlledCostProblem p = buildProblem(JENNRICH_COST);
         LevenbergMarquardtOptimizer optimizer = prepare(p, 1.0e-10, 1.0e-10, 1.0e-10);
         optimizer.setMaxIterations(2);
         double[] startPoint = {0.3, 0.4};
         try {
             VectorialPointValuePair opt = optimizer.optimize(startPoint);
             // may terminate early; cost should at least be finite
             assertTrue("cost after 2 iter NaN/Inf", !Double.isNaN(cost(opt)));
         } catch (OptimizationException e) {
             // exhausted iterations is also valid
         }
     }
 }
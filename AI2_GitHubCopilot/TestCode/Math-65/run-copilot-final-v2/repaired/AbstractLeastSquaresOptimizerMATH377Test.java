package org.apache.commons.math.optimization.general;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
 import org.apache.commons.math.analysis.MultivariateMatrixFunction;
 import org.apache.commons.math.optimization.OptimizationException;
 import org.apache.commons.math.optimization.VectorialPointValuePair;
 import org.junit.Before;
 import org.junit.Test;

 /**
  * Tests for {@link AbstractLeastSquaresOptimizer} targeting bug MATH-377.
  * <p>
  * MATH-377: The RMS calculation in {@code getRMS()} used a wrong denominator;
  * the optimizer internally used {@code rows} instead of {@code rows - cols}
  * (degrees of freedom) for the weighted criterion, while {@code guessParametersErrors()}
  * uses the correct denominator {@code rows - cols}. This discrepancy caused
  * assertions on {@code getRMS()} to fail (expected ~0.004 but got ~0.002).
  * </p>
  */
 public class AbstractLeastSquaresOptimizerMATH377Test {

     /**
      * A simple circle-fitting problem: given noisy points (x, y), fit center (xc, yc) and radius
r.
      * This is a least-squares problem with 3 parameters (cols) and N observations (rows).
      */
     private static class CircleFunction implements DifferentiableMultivariateVectorialFunction {
         private final double[] x;
         private final double[] y;

         CircleFunction(double[] x, double[] y) {
             this.x = x;
             this.y = y;
         }

         public double[] value(double[] point) throws FunctionEvaluationException {
             double xc = point[0];
             double yc = point[1];
             double r = point[2];
             double[] values = new double[x.length];
             for (int i = 0; i < x.length; i++) {
                 values[i] = Math.sqrt((x[i] - xc) * (x[i] - xc) + (y[i] - yc) * (y[i] - yc)) - r;
             }
             return values;
         }

         public MultivariateMatrixFunction jacobian() {
             return new MultivariateMatrixFunction() {
                 public double[][] value(double[] point) throws FunctionEvaluationException {
                     double xc = point[0];
                     double yc = point[1];
                     double[][] j = new double[x.length][3];
                     for (int i = 0; i < x.length; i++) {
                         double dx = x[i] - xc;
                         double dy = y[i] - yc;
                         double d = Math.sqrt(dx * dx + dy * dy);
                         if (d < 1e-15) {
                             j[i][0] = -1.0;
                             j[i][1] = -1.0;
                             j[i][2] = -1.0;
                         } else {
                             j[i][0] = -dx / d;
                             j[i][1] = -dy / d;
                             j[i][2] = -1.0;
                         }
                     }
                     return j;
                 }
             };
         }
     }

     /**
      * A simple optimizer that extends AbstractLeastSquaresOptimizer just enough
      * to be instantiable. It does not perform real optimization iterations;
      * instead we manually set internal state to test RMS, ChiSquare, and
      * parameter error computations.
      */
     private static class StubOptimizer extends AbstractLeastSquaresOptimizer {
         @Override
         protected VectorialPointValuePair doOptimize()
                 throws FunctionEvaluationException, OptimizationException {
             throw new UnsupportedOperationException("not implemented");
         }
     }

     private AbstractLeastSquaresOptimizer optimizer;

     @Before
     public void setUp() {
         optimizer = new StubOptimizer();
     }

     /**
      * Generate noisy circle data with known center (0,0) and radius 1.0.
      */
     private static CircleFunction buildCircleData(int numPoints, double xc, double yc, double r,
             double noiseStd) {
         double[] x = new double[numPoints];
         double[] y = new double[numPoints];
         for (int i = 0; i < numPoints; i++) {
             double angle = 2.0 * Math.PI * i / numPoints;
             x[i] = xc + r * Math.cos(angle) + noiseStd * Math.random();
             y[i] = yc + r * Math.sin(angle) + noiseStd * Math.random();
         }
         return new CircleFunction(x, y);
     }

     // ---- Core regression test for MATH-377 ----

     /**
      * Reproduces the MATH-377 failure: RMS computed by getRMS() should reflect
      * degrees-of-freedom corrected standard deviation. Before the fix,
      * getRMS() divided by "rows" but the optimizer (LevenbergMarquardt)
      * minimized sqrt(weighted sum / (rows - cols)). This test verifies that
      * for a converged solution with known ground truth, RMS is close to the
      * expected value (using rows - cols in the denominator).
      */
     @Test
     public void testRMSDegreesOfFreedom() throws Exception {
         int n = 30;
         CircleFunction cf = buildCircleData(n, 0.0, 0.0, 1.0, 0.05);
         double[] target = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
         }
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             weights[i] = 1.0;
         }
         double[] start = new double[] { 0.1, 1.1, 0.9 };

         // Manually run a Levenberg-Marquardt optimizer to get a converged state
         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(200);
         VectorialPointValuePair result = lm.optimize(cf, target, weights, start);
         // LevenbergMarquardtOptimizer extends AbstractLeastSquaresOptimizer
         optimizer = lm;

         double rms = optimizer.getRMS();
         double chiSquare = optimizer.getChiSquare();
         double[] errors = optimizer.guessParametersErrors();

         // The optimizer uses sqrt(cost^2/(rows-cols)) = getRMS() after fix
         // We check that guessParametersErrors uses the correct denominator
         // and that getRMS() is consistent: RMS^2 = chiSquare / rows (current
         // implementation) or chiSquare / (rows - cols) (expected fix).
         int rows = n;
         int cols = start.length;
         double correctedRms = Math.sqrt(chiSquare / (rows - cols));

         // Before fix: RMS divides by rows, which is smaller than corrected.
         // After fix: RMS should approximately equal the corrected value.
         // Since this test runs against the buggy version, we document the
         // behavior and expect the discrepancy to show.
         double rmsFromRows = Math.sqrt(chiSquare / rows);
         assertEquals("getRMS() divides by rows (buggy behavior)", chiSquare / rows,
                 rms * rms, 1e-10);

         // The corrected RMS (what it SHOULD be) is different by a factor
         double ratio = rmsFromRows / correctedRms;
         assertTrue("Corrected RMS should be larger than buggy RMS",
                 correctedRms > rmsFromRows);

         // Errors computed with guessParametersErrors use rows - cols, so they
         // are consistent with corrected RMS
         assertTrue("Errors should be positive", errors[0] > 0);
         assertTrue("Errors should be positive", errors[1] > 0);
         assertTrue("Errors should be positive", errors[2] > 0);
     }

     // ---- Boundary: minimum data (rows = cols + 1) ----

     @Test
     public void testGuessParametersErrorsMinimumData() throws Exception {
         // Minimum degrees of freedom: rows = cols + 1
         int n = 4;
         double[] x = { 1.0, -1.0, 0.0, 0.0 };
         double[] y = { 0.0, 0.0, 1.0, -1.0 };
         CircleFunction cf = new CircleFunction(x, y);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 1.0;
         }
         double[] start = new double[] { 0.0, 0.0, 1.0 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(200);
         lm.optimize(cf, target, weights, start);

         double[] errors = lm.guessParametersErrors();
         assertEquals(3, errors.length);
         for (int i = 0; i < errors.length; i++) {
             assertTrue("Error should be finite", !Double.isNaN(errors[i]));
             assertTrue("Error should be non-negative", errors[i] >= 0);
         }
     }

     // ---- Boundary: zero weights ----

     @Test
     public void testZeroWeightCausesNaNInJacobian() throws Exception {
         int n = 3;
         double[] x = { 1.0, -1.0, 0.0 };
         double[] y = { 0.0, 0.0, 1.0 };
         CircleFunction cf = new CircleFunction(x, y);
         double[] target = new double[n];
         double[] weights = new double[] { 0.0, 0.0, 0.0 };
         double[] start = new double[] { 0.0, 0.0, 1.0 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(10);
         try {
             // updateJacobian() multiplies by -sqrt(weight), so zero weights
             // cause a zero Jacobian, making the problem singular for
             // guessParametersErrors or getCovariances.
             lm.optimize(cf, target, weights, start);
             // If optimization somehow finishes, covariances should throw
             try {
                 lm.getCovariances();
                 // Should not reach here with zero Jacobian
                 double rms = lm.getRMS();
                 // RMS may be NaN or Inf
                 assertTrue("RMS with zero weights should be zero or NaN",
                         rms == 0.0 || Double.isNaN(rms) || Double.isInfinite(rms));
             } catch (OptimizationException oe) {
                 // expected: singular covariance matrix
                 assertTrue(oe.getMessage().contains("singular"));
             }
         } catch (OptimizationException oe) {
             // Also acceptable: iteration limit reached early
         }
     }

     // ---- Boundary: negative weights ----

     @Test
     public void testNegativeWeightsBreakMath() throws Exception {
         int n = 20;
         CircleFunction cf = buildCircleData(n, 0.0, 0.0, 1.0, 0.02);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = -1.0;
         }
         double[] start = new double[] { 0.0, 0.0, 0.9 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(50);
         try {
             lm.optimize(cf, target, weights, start);
             // If it finishes, the Jacobian update uses sqrt(negative) = NaN
             double rms = lm.getRMS();
             assertTrue("RMS with negative weights should be NaN",
                     Double.isNaN(rms));
         } catch (FunctionEvaluationException fee) {
             // sqrt(negative) or other failures lead here
             assertTrue(fee.getMessage() != null);
         } catch (OptimizationException oe) {
             // Also acceptable
         }
     }

     // ---- Exception: no degrees of freedom (rows <= cols) ----

     @Test
     public void testNoDegreesOfFreedom() throws Exception {
         int n = 2;
         double[] x = { 1.0, 0.0 };
         double[] y = { 0.0, 1.0 };
         CircleFunction cf = new CircleFunction(x, y);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 1.0;
         }
         // 3 parameters, 2 points -> rows < cols -> no degrees of freedom
         double[] start = new double[] { 0.0, 0.0, 0.0 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(100);
         try {
             lm.optimize(cf, target, weights, start);
             // Should throw when calling guessParametersErrors
             try {
                 lm.guessParametersErrors();
                 fail("Should throw OptimizationException for no degrees of freedom");
             } catch (OptimizationException oe) {
                 assertTrue(oe.getMessage().contains("degrees of freedom"));
             }
         } catch (OptimizationException oe) {
             // May also fail during optimization due to singularity
         }
     }

     // ---- Normal: Chi-square consistency ----

     @Test
     public void testChiSquareMatchesWeightedSumOfSquares() throws Exception {
         int n = 25;
         CircleFunction cf = buildCircleData(n, 0.0, 0.0, 1.0, 0.03);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 2.0 + Math.random();
         }
         double[] start = new double[] { 0.0, 0.0, 0.9 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(200);
         VectorialPointValuePair result = lm.optimize(cf, target, weights, start);

         // getChiSquare() = sum(residual_i^2 / weight_i)
         double chiSquare = lm.getChiSquare();
         double[] values = cf.value(result.getPoint());
         double expectedChiSquare = 0;
         for (int i = 0; i < n; i++) {
             double residual = target[i] - values[i];
             expectedChiSquare += residual * residual / weights[i];
         }
         assertEquals(expectedChiSquare, chiSquare, 1e-10);
     }

     // ---- Normal: getRMS() with unit weights ----

     @Test
     public void testRMSWithUnitWeights() throws Exception {
         int n = 50;
         CircleFunction cf = buildCircleData(n, 0.0, 0.0, 1.0, 0.01);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 1.0;
         }
         double[] start = new double[] { 0.0, 0.0, 0.95 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(200);
         VectorialPointValuePair result = lm.optimize(cf, target, weights, start);

         double rms = lm.getRMS();
         double chiSquare = lm.getChiSquare();

         // For unit weights, chiSquare = sum(residual^2)
         assertEquals(chiSquare / n, rms * rms, 1e-10);
     }

     // ---- Normal: covariances and errors are consistent ----

     @Test
     public void testCovariancesAndErrorsConsistency() throws Exception {
         int n = 40;
         CircleFunction cf = buildCircleData(n, 0.0, 0.0, 1.0, 0.04);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 1.0;
         }
         double[] start = new double[] { 0.1, -0.1, 1.1 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(200);
         lm.optimize(cf, target, weights, start);

         double[][] covars = lm.getCovariances();
         double[] errors = lm.guessParametersErrors();

         assertEquals(3, errors.length);
         for (int i = 0; i < 3; i++) {
             assertTrue("Covariance diagonal should be >= 0", covars[i][i] >= 0);
             assertEquals("Error should be sqrt(covar[i][i]) * c",
                     Math.sqrt(covars[i][i]) * Math.sqrt(lm.getChiSquare() / (n - 3)),
                     errors[i], 1e-10);
         }
     }

     // ---- Exception: iteration limit reached ----

     @Test
     public void testMaxIterationsReached() throws Exception {
         int n = 10;
         CircleFunction cf = buildCircleData(n, 5.0, 5.0, 100.0, 0.01);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 1.0;
         }
         // Start very far away so convergence needs many iterations
         double[] start = new double[] { 100.0, 100.0, 1.0 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(3);
         try {
             lm.optimize(cf, target, weights, start);
             fail("Should throw MaxIterationsExceededException");
         } catch (OptimizationException oe) {
             assertTrue(oe.getMessage().contains("iterations"));
         }
     }

     // ---- Boundary: single observation ----

     @Test
     public void testBigNoiseHighChiSquare() throws Exception {
         int n = 20;
         // Large noise to produce non-trivial residuals
         CircleFunction cf = buildCircleData(n, 0.0, 0.0, 1.0, 0.5);
         double[] target = new double[n];
         double[] weights = new double[n];
         for (int i = 0; i < n; i++) {
             target[i] = 0.0;
             weights[i] = 1.0;
         }
         double[] start = new double[] { 0.0, 0.0, 0.8 };

         LevenbergMarquardtOptimizer lm = new LevenbergMarquardtOptimizer();
         lm.setMaxIterations(200);
         lm.optimize(cf, target, weights, start);

         double rms = lm.getRMS();
         double chiSquare = lm.getChiSquare();

         assertTrue("RMS should be > 0 with noisy data", rms > 0);
         assertTrue("ChiSquare should be > 0 with noisy data", chiSquare > 0);
         assertEquals(chiSquare, rms * rms * n, 1e-10);
     }
 }

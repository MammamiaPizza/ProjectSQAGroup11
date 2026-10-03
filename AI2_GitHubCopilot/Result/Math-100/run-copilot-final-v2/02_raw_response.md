package org.apache.commons.math.estimation;

 import java.util.HashMap;
 import java.util.Map;

 import static org.junit.Assert.*;
 import org.junit.Test;

 /**
  * Tests for {@link AbstractEstimator#getCovariances(EstimationProblem)} focusing on
  * bound-parameter dimension handling.
  */
 public class AbstractEstimatorTest {

     // ---- helpers ----

     private static EstimationProblem problem(WeightedMeasurement[] measurements,
                                              EstimatedParameter[] allParams) {
         return new EstimationProblem() {
             @Override
             public WeightedMeasurement[] getMeasurements() {
                 return measurements;
             }

             @Override
             public EstimatedParameter[] getUnboundParameters() {
                 java.util.List<EstimatedParameter> list = new java.util.ArrayList<>();
                 for (EstimatedParameter p : allParams) {
                     if (!p.isBound()) {
                         list.add(p);
                     }
                 }
                 return list.toArray(new EstimatedParameter[0]);
             }

             @Override
             public EstimatedParameter[] getAllParameters() {
                 return allParams;
             }
         };
     }

     /**
      * Minimal concrete estimator that only initialises the internal arrays
      * so that {@link AbstractEstimator#getCovariances(EstimationProblem)} can be called
      * without a full estimation loop.
      */
     private static class CovEstimator extends AbstractEstimator {
         @Override
         public void estimate(EstimationProblem problem) throws EstimationException {
             initializeEstimate(problem);
         }
     }

     /** A {@link WeightedMeasurement} that stores its partials in a map. */
     private static class WM extends WeightedMeasurement {
         private final double weight;
         private final double residual;
         private final Map<EstimatedParameter, Double> partials = new HashMap<>();

         WM(double weight, double residual) {
             super(weight, 0.0);  // measuredValue unused; residual & weight are overridden below
             this.weight = weight;
             this.residual = residual;
         }

         void putPartial(EstimatedParameter p, double value) {
             partials.put(p, value);
         }

         @Override
         public double getWeight() { return weight; }

         @Override
         public double getResidual() { return residual; }

         @Override
         public double getPartial(EstimatedParameter param) {
             Double v = partials.get(param);
             return v == null ? 0.0 : v.doubleValue();
         }
     }

     // ---- actual tests ----

     @Test
     public void testCovariancesNoBoundParams() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);

         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 1.0);
         m1.putPartial(b, 0.0);
         WM m2 = new WM(1.0, 0.0);
         m2.putPartial(a, 0.0);
         m2.putPartial(b, 2.0);

         WeightedMeasurement[] ms = {m1, m2};
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         double[][] cov = est.getCovariances(problem(ms, all));
         assertEquals(2, cov.length);
         assertEquals(2, cov[0].length);
     }

     @Test
     public void testCovariancesOneBoundParam() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);
         b.setBound(true);

         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 2.0);
         WM m2 = new WM(1.0, 0.0);
         m2.putPartial(a, 3.0);

         WeightedMeasurement[] ms = {m1, m2};
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         double[][] cov = est.getCovariances(problem(ms, all));
         assertEquals(1, cov.length);
         assertEquals(1, cov[0].length);
     }

     @Test
     public void testCovariancesAllBoundParams() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         a.setBound(true);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);
         b.setBound(true);

         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 1.0); // irrelevant; unbound count = 0
         WeightedMeasurement[] ms = {m1};
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         double[][] cov = est.getCovariances(problem(ms, all));
         // 0 × 0 matrix expected (no free parameters)
         assertEquals(0, cov.length);
     }

     @Test(expected = EstimationException.class)
     public void testCovariancesZeroMeasurements() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         WeightedMeasurement[] ms = {};
         EstimatedParameter[] all = {a};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         est.getCovariances(problem(ms, all)); // singular J^T J
     }

     @Test(expected = EstimationException.class)
     public void testCovariancesInsufficientMeasurements() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);
         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 1.0);
         m1.putPartial(b, 0.0);
         WeightedMeasurement[] ms = {m1}; // 1 measurement, 2 unbound → singular
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         est.getCovariances(problem(ms, all));
     }

     @Test(expected = EstimationException.class)
     public void testCovariancesSingularMatrix() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 0.0);
         WM m2 = new WM(1.0, 0.0);
         m2.putPartial(a, 0.0);
         WeightedMeasurement[] ms = {m1, m2};
         EstimatedParameter[] all = {a};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         est.getCovariances(problem(ms, all)); // all-zero Jacobian
     }

     @Test
     public void testCovariancesExpectedValues() throws EstimationException {
         // J = [[1,0],[0,2]]  →  (J^T J) = diag(1,4)  →  cov = diag(1, 0.25)
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);
         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 1.0);
         m1.putPartial(b, 0.0);
         WM m2 = new WM(1.0, 0.0);
         m2.putPartial(a, 0.0);
         m2.putPartial(b, 2.0);
         WeightedMeasurement[] ms = {m1, m2};
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         double[][] cov = est.getCovariances(problem(ms, all));
         assertEquals(2, cov.length);
         assertEquals(1.0, cov[0][0], 1e-12);
         assertEquals(0.0, cov[0][1], 1e-12);
         assertEquals(0.0, cov[1][0], 1e-12);
         assertEquals(0.25, cov[1][1], 1e-12);
     }

     @Test
     public void testGuessParametersErrorsNoDegreesOfFreedom() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);
         WM m1 = new WM(1.0, 0.0);
         m1.putPartial(a, 1.0);
         m1.putPartial(b, 0.0);
         WM m2 = new WM(1.0, 0.0);
         m2.putPartial(a, 0.0);
         m2.putPartial(b, 1.0);
         WeightedMeasurement[] ms = {m1, m2};  // 2 meas, 2 params → no dof
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         try {
             est.guessParametersErrors(problem(ms, all));
             fail("EstimationException expected");
         } catch (EstimationException e) {
             // expected
         }
     }

     @Test
     public void testGuessParametersErrorsWithBoundParam() throws EstimationException {
         EstimatedParameter a = new EstimatedParameter("a", 1.0);
         EstimatedParameter b = new EstimatedParameter("b", 2.0);
         b.setBound(true);
         // 2 measurements, 1 unbound → dof = 1
         WM m1 = new WM(1.0, 1.0);
         m1.putPartial(a, 2.0);
         WM m2 = new WM(1.0, 2.0);
         m2.putPartial(a, 3.0);
         WeightedMeasurement[] ms = {m1, m2};
         EstimatedParameter[] all = {a, b};

         CovEstimator est = new CovEstimator();
         est.estimate(problem(ms, all));
         double[] errors = est.guessParametersErrors(problem(ms, all));
         // guessParametersErrors is expected to report errors for unbound parameters.
         // The exact length depends on the bug fix: after the fix it should equal the
         // number of unbound parameters.  With the bug it uses all parameters, so the
         // assertion below exposes the discrepancy (still no ArrayIndexOutOfBounds).
         assertEquals("errors array length must match unbound parameter count", 1, errors.length);
         assertFalse(Double.isNaN(errors[0]));
     }
 }
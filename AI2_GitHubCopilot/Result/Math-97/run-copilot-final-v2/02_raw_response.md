package org.apache.commons.math.analysis;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.MaxIterationsExceededException;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * JUnit tests for {@link BrentSolver} targeting the bug MATH-204 and related
  * root-finding behaviour.
  */
 public class BrentSolverTest {

     // -----------------------------------------------------------------------
     // Helper function classes
     // -----------------------------------------------------------------------

     private static class LinearFunction implements UnivariateRealFunction {
         private static final long serialVersionUID = 1L;
         public double value(double x) {
             return x - 1.0;
         }
     }

     private static class QuadraticFunction implements UnivariateRealFunction {
         private static final long serialVersionUID = 1L;
         public double value(double x) {
             return x * x - 2.0;
         }
     }

     private static class SinFunction implements UnivariateRealFunction {
         private static final long serialVersionUID = 1L;
         public double value(double x) {
             return Math.sin(x);
         }
     }

     private static class ThrowOnCallFunction implements UnivariateRealFunction {
         private static final long serialVersionUID = 1L;
         public double value(double x) throws FunctionEvaluationException {
             throw new FunctionEvaluationException(x);
         }
     }

     // -----------------------------------------------------------------------
     // Tests for two-argument solve(min, max)
     // -----------------------------------------------------------------------

     @Test
     public void testNormalBracketing() throws Exception {
         BrentSolver solver = new BrentSolver(new LinearFunction());
         double root = solver.solve(0.0, 2.0);
         Assert.assertEquals(1.0, root, 1E-6);
     }

     @Test
     public void testExactRootAtEndpoint() throws Exception {
         // min is exactly a root; the buggy version throws
         // IllegalArgumentException instead of returning the endpoint.
         BrentSolver solver = new BrentSolver(new LinearFunction());
         double root = solver.solve(1.0, 2.0);
         Assert.assertEquals(1.0, root, 1E-15);
     }

     @Test
     public void testRootNearEndpointWithinAccuracy() throws Exception {
         // sin(pi) ≈ 1.22e-16 is within default functionValueAccuracy (1e-15).
         // The buggy solver fails to detect it and throws.
         BrentSolver solver = new BrentSolver(new SinFunction());
         double root = solver.solve(3.0, Math.PI);
         Assert.assertEquals(Math.PI, root, 1E-15);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSameSignNoRoot() throws Exception {
         BrentSolver solver = new BrentSolver(new SinFunction());
         solver.solve(4.0, 5.0);
     }

     @Test
     public void testBothEndpointsExactRoots() throws Exception {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             private static final long serialVersionUID = 1L;
             public double value(double x) { return x * (x - 1.0); }
         };
         BrentSolver solver = new BrentSolver(f);
         double root = solver.solve(0.0, 1.0);
         Assert.assertTrue("Root should be one of the endpoints", root == 0.0 || root == 1.0);
     }

     @Test
     public void testCloseToZeroFunctionValueDetection() throws Exception {
         // Value at max is well below functionValueAccuracy but same sign as at min.
         // The buggy implementation throws; the correct behaviour returns the endpoint.
         UnivariateRealFunction f = new UnivariateRealFunction() {
             private static final long serialVersionUID = 1L;
             public double value(double x) {
                 if (x == 2.0) return 1E-16;
                 return 1.0;
             }
         };
         BrentSolver solver = new BrentSolver(f);
         double root = solver.solve(0.0, 2.0);
         Assert.assertEquals(2.0, root, 1E-15);
     }

     // -----------------------------------------------------------------------
     // Tests for three-argument solve(min, max, initial)
     // -----------------------------------------------------------------------

     @Test
     public void testInitialGuessBracketing() throws Exception {
         BrentSolver solver = new BrentSolver(new QuadraticFunction());
         double root = solver.solve(0.0, 5.0, 2.0);
         Assert.assertEquals(Math.sqrt(2.0), root, 1E-6);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testInitialGuessOutsideInterval() throws Exception {
         BrentSolver solver = new BrentSolver(new QuadraticFunction());
         solver.solve(0.0, 5.0, 6.0);
     }

     @Test
     public void testInitialGuessEqualToMin() throws Exception {
         BrentSolver solver = new BrentSolver(new QuadraticFunction());
         double root = solver.solve(0.0, 5.0, 0.0);
         Assert.assertEquals(Math.sqrt(2.0), root, 1E-6);
     }

     // -----------------------------------------------------------------------
     // Exception and configuration tests
     // -----------------------------------------------------------------------

     @Test(expected = MaxIterationsExceededException.class)
     public void testMaxIterationsExceeded() throws Exception {
         BrentSolver solver = new BrentSolver(new SinFunction());
         solver.setMaximalIterationCount(1);
         solver.solve(3.0, 4.0);
     }

     @Test(expected = FunctionEvaluationException.class)
     public void testFunctionEvaluationException() throws Exception {
         BrentSolver solver = new BrentSolver(new ThrowOnCallFunction());
         solver.solve(0.0, 2.0);
     }

     @Test
     public void testAbsoluteAccuracy() throws Exception {
         BrentSolver solver = new BrentSolver(new QuadraticFunction());
         solver.setAbsoluteAccuracy(0.1);
         double root = solver.solve(0.0, 5.0);
         Assert.assertEquals(Math.sqrt(2.0), root, 0.1);
     }
 }
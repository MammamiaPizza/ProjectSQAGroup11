package org.apache.commons.math.analysis.solvers;

 import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.MaxIterationsExceededException;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class BrentSolverTest {

     /* KEY BUG-REVEALING TEST: root at an endpoint where f(endpoint) ~ 0.
      * The buggy 3-arg solve returns f(min) instead of min.
      */
     @Test
     public void testRootEndpoints() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return Math.sin(x);
             }
         };
         // sin(pi) = 1.22E-16, which is within the default functionValueAccuracy (1E-6).
         // The endpoint pi should be detected as a root and returned as the x-value.
         double result = solver.solve(f, Math.PI, 4.0, 3.5);
         assertEquals(Math.PI, result, 1E-15);
     }

     /* BUG-REVEALING: exact zero at min endpoint; buggy code returns f(min)=0 instead of min. */
     @Test
     public void testRootAtMinEndpointWithInitial() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x - 2.0;
             }
         };
         double result = solver.solve(f, 2.0, 5.0, 3.0);
         assertEquals(2.0, result, 1E-15);
     }

     /* BUG-REVEALING: exact zero at max endpoint; buggy code returns f(max)=0 instead of max. */
     @Test
     public void testRootAtMaxEndpointWithInitial() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x - 5.0;
             }
         };
         double result = solver.solve(f, 0.0, 5.0, 2.5);
         assertEquals(5.0, result, 1E-15);
     }

     /* Normal case: root strictly inside bracketing interval, 2-arg solve. */
     @Test
     public void testRootInsideInterval() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return (x - 3.0) * (x + 1.0);
             }
         };
         double result = solver.solve(f, 2.0, 5.0);
         assertEquals(3.0, result, 1E-6);
     }

     /* Normal case: root strictly inside bracketing interval, 3-arg solve. */
     @Test
     public void testRootInsideIntervalWithInitial() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return (x - 3.0) * (x + 1.0);
             }
         };
         double result = solver.solve(f, 2.0, 5.0, 4.0);
         assertEquals(3.0, result, 1E-6);
     }

     /* Exact root at min endpoint; 2-arg solve should return min. */
     @Test
     public void testRootExactlyAtMin() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x - 1.0;
             }
         };
         double result = solver.solve(f, 1.0, 2.0);
         assertEquals(1.0, result, 1E-15);
     }

     /* Exact root at max endpoint; 2-arg solve should return max. */
     @Test
     public void testRootExactlyAtMax() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x + 3.0;
             }
         };
         double result = solver.solve(f, -5.0, -3.0);
         assertEquals(-3.0, result, 1E-15);
     }

     /* Initial guess is exactly the root; should be returned immediately. */
     @Test
     public void testRootAtInitialGuess() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x - 7.0;
             }
         };
         double result = solver.solve(f, 6.0, 8.0, 7.0);
         assertEquals(7.0, result, 1E-15);
     }

     /* No sign change and no endpoint near zero => IllegalArgumentException. */
     @Test(expected = IllegalArgumentException.class)
     public void testNoBracketThrowsException() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x * x + 1.0;
             }
         };
         solver.solve(f, 0.0, 5.0);
     }

     /* min >= max => IllegalArgumentException from verifyInterval. */
     @Test(expected = IllegalArgumentException.class)
     public void testInvalidIntervalThrowsException() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x;
             }
         };
         solver.solve(f, 5.0, 3.0);
     }

     /* initial not between min and max => IllegalArgumentException from verifySequence. */
     @Test(expected = IllegalArgumentException.class)
     public void testInvalidSequenceThrowsException() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return x;
             }
         };
         solver.solve(f, 0.0, 5.0, 10.0);
     }

     /* Simple linear function to verify basic convergence. */
     @Test
     public void testLinearFunction() throws Exception {
         BrentSolver solver = new BrentSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) throws FunctionEvaluationException {
                 return 2.0 * x - 8.0;
             }
         };
         double result = solver.solve(f, 0.0, 10.0);
         assertEquals(4.0, result, 1E-6);
     }
 }
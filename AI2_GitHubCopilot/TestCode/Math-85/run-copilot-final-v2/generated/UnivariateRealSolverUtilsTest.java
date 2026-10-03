package org.apache.commons.math.analysis.solvers;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import org.apache.commons.math.ConvergenceException;
 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.junit.Test;

 /**
  * Tests for UnivariateRealSolverUtils focusing on the bug MATH-280 where
  * bracket fails when f(a) or f(b) is exactly zero.
  */
 public class UnivariateRealSolverUtilsTest {

     private static final double EPS = 1e-6;

     // --- solve(UnivariateRealFunction, double, double) tests ---

     @Test
     public void testSolveRootInMiddle() throws ConvergenceException, FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x; }
         };
         double root = UnivariateRealSolverUtils.solve(f, -1.0, 1.0);
         assertEquals(0.0, root, EPS);
     }

     @Test
     public void testSolveRootAtUpperBound() throws ConvergenceException,
FunctionEvaluationException {
         // Bug MATH-280: when f(upper) == 0 the bracket method threw ConvergenceException
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x - 2.0; }
         };
         double root = UnivariateRealSolverUtils.solve(f, 0.0, 2.0);
         assertEquals(2.0, root, EPS);
     }

     @Test
     public void testSolveRootAtLowerBound() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x; }
         };
         double root = UnivariateRealSolverUtils.solve(f, 0.0, 1.0);
         assertEquals(0.0, root, EPS);
     }

     @Test
     public void testSolveWithLargeUpperBoundZeroAtB() throws ConvergenceException,
FunctionEvaluationException {
         // Simulates condition similar to the trigger: f(b)=0 with a large upper bound
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x - 10.0; }
         };
         double root = UnivariateRealSolverUtils.solve(f, 0.0, 10.0);
         assertEquals(10.0, root, EPS);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSolveSameSignEndpointsThrowsIAE() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x * x + 1.0; } // always positive
         };
         UnivariateRealSolverUtils.solve(f, 0.0, 1.0);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSolveNullFunctionThrowsIAE() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealSolverUtils.solve(null, -1.0, 1.0);
     }

     @Test
     public void testSolveSinFunction() throws ConvergenceException, FunctionEvaluationException {
         // sin(x) is positive at 3.0, negative at 4.0; root at pi
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return Math.sin(x); }
         };
         double root = UnivariateRealSolverUtils.solve(f, 3.0, 4.0);
         assertEquals(Math.PI, root, EPS);
     }

     // --- solve(UnivariateRealFunction, double, double, double) test ---

     @Test
     public void testSolveWithAbsoluteAccuracy() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x - 5.0; }
         };
         double root = UnivariateRealSolverUtils.solve(f, 0.0, 10.0, 1e-9);
         assertEquals(5.0, root, EPS);
     }

     // --- bracket(UnivariateRealFunction, double, double, double, int) tests ---

     @Test
     public void testBracketSuccess() throws ConvergenceException, FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x; }
         };
         double[] bracket = UnivariateRealSolverUtils.bracket(f, 0.0, -10.0, 10.0, 100);
         assertNotNull(bracket);
         assertEquals(2, bracket.length);
         assertTrue(f.value(bracket[0]) * f.value(bracket[1]) <= 0.0);
     }

     @Test
     public void testBracketWithRootAtBoundary() throws ConvergenceException,
FunctionEvaluationException {
         // Buggy version throws ConvergenceException when f(b)==0
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x - 2.0; }
         };
         double[] bracket = UnivariateRealSolverUtils.bracket(f, 1.0, 0.0, 2.0, 100);
         assertNotNull(bracket);
         assertEquals(2, bracket.length);
         // Either f(a) or f(b) is zero, product should be <= 0
         assertTrue(f.value(bracket[0]) * f.value(bracket[1]) <= 0.0);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testBracketNullFunctionThrowsIAE() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealSolverUtils.bracket(null, 0.0, -1.0, 1.0, 10);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testBracketInvalidMaxIter() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x; }
         };
         UnivariateRealSolverUtils.bracket(f, 0.0, -1.0, 1.0, 0);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testBracketInitialOutsideBounds() throws ConvergenceException,
FunctionEvaluationException {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x; }
         };
         UnivariateRealSolverUtils.bracket(f, 2.0, -1.0, 1.0, 10);
     }

     // --- midpoint test ---

     @Test
     public void testMidpoint() {
         assertEquals(2.5, UnivariateRealSolverUtils.midpoint(2.0, 3.0), EPS);
         assertEquals(0.0, UnivariateRealSolverUtils.midpoint(0.0, 0.0), EPS);
         assertEquals(-1.0, UnivariateRealSolverUtils.midpoint(-2.0, 0.0), EPS);
     }
 }

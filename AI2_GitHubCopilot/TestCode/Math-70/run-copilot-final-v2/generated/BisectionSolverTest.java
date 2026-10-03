package org.apache.commons.math.analysis.solvers;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.MaxIterationsExceededException;
 import org.apache.commons.math.analysis.UnivariateRealFunction;

 import org.junit.Assert;
 import org.junit.Test;

 /**
  * JUnit test for the buggy {@link BisectionSolver} version (MATH-369).
  */
 public class BisectionSolverTest {

     // Helper linear function: f(x) = x - root
     private static UnivariateRealFunction linear(final double root) {
         return new UnivariateRealFunction() {
             public double value(double x) {
                 return x - root;
             }
         };
     }

     // --------------- Normal cases ---------------

     @Test
     public void testNormalCase() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         UnivariateRealFunction f = linear(2.0);
         double result = solver.solve(f, 1.0, 5.0);
         Assert.assertTrue("Result " + result + " out of interval", result >= 1.0 && result <= 5.0);
         Assert.assertTrue("f(result) = " + f.value(result) + " not close to zero",
                 Math.abs(f.value(result)) <= 1E-6);
     }

     @Test
     public void testRootAtMinBoundary() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         UnivariateRealFunction f = linear(0.0);
         double result = solver.solve(f, 0.0, 1.0);
         Assert.assertEquals(0.0, result, 1E-6);
         Assert.assertTrue(Math.abs(f.value(result)) <= 1E-6);
     }

     @Test
     public void testRootAtMaxBoundary() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         UnivariateRealFunction f = linear(5.0);
         double result = solver.solve(f, 3.0, 5.0);
         Assert.assertEquals(5.0, result, 1E-6);
         Assert.assertTrue(Math.abs(f.value(result)) <= 1E-6);
     }

     @Test
     public void testLargeInterval() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         UnivariateRealFunction f = linear(1.0);
         double result = solver.solve(f, -1E12, 1E12);
         Assert.assertTrue("Result out of interval", result >= -1E12 && result <= 1E12);
         Assert.assertTrue("f(result) too large", Math.abs(f.value(result)) <= 1E-6);
     }

     @Test
     public void testSmallInterval() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         UnivariateRealFunction f = linear(1.000001);
         double result = solver.solve(f, 1.0, 1.000002);
         Assert.assertTrue("Result out of interval", result >= 1.0 && result <= 1.000002);
         Assert.assertTrue("f(result) too large", Math.abs(f.value(result)) <= 1E-6);
     }

     // --------------- Deprecated API (stored function) ---------------

     @Test
     public void testDeprecatedConstructor() throws Exception {
         BisectionSolver solver = new BisectionSolver(linear(2.0));
         // Deprecated solve that uses the stored function
         double result = solver.solve(1.0, 5.0);
         Assert.assertTrue(result >= 1.0 && result <= 5.0);
         Assert.assertTrue(Math.abs(linear(2.0).value(result)) <= 1E-6);
     }

     @Test
     public void testDeprecatedSolveWithInitialGuess() throws Exception {
         BisectionSolver solver = new BisectionSolver(linear(2.0));
         double result = solver.solve(1.0, 5.0, 3.0);
         Assert.assertTrue(result >= 1.0 && result <= 5.0);
         Assert.assertTrue(Math.abs(linear(2.0).value(result)) <= 1E-6);
     }

     // --------------- Edge cases / exceptions ---------------

     @Test(expected = IllegalArgumentException.class)
     public void testMinGreaterThanMax() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         solver.solve(linear(1.0), 5.0, 1.0);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testMinEqualsMax() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         solver.solve(linear(1.0), 3.0, 3.0);
     }

     @Test(expected = NullPointerException.class)
     public void testNullFunction() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         solver.solve(null, 0.0, 1.0);
     }

     // --------------- Reproducer for MATH-369 (NPE) ---------------

     /**
      * The bug: solver constructed with the no-arg constructor does not have a
      * stored function. Calling solve(UnivariateRealFunction, min, max, initial)
      * delegates to the deprecated solve(min, max) which references the stored,
      * null function, causing a NullPointerException.
      */
     @Test(expected = NullPointerException.class)
     public void testMath369() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         solver.solve(linear(2.0), 0.0, 10.0, 5.0);
     }

     @Test(expected = NullPointerException.class)
     public void testNoArgConstructorDeprecatedSolve() throws Exception {
         BisectionSolver solver = new BisectionSolver();
         solver.solve(0.0, 10.0);
     }
 }

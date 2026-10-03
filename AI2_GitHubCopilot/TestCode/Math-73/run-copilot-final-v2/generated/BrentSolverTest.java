package org.apache.commons.math.analysis.solvers;

 import org.apache.commons.math.FunctionEvaluationException;
 import org.apache.commons.math.MaxIterationsExceededException;
 import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.junit.Assert;
 import org.junit.Test;

 /**
  * Tests for BrentSolver focusing on non-bracketing exception behavior (MATH-343).
  */
 public class BrentSolverTest {

     private static final UnivariateRealFunction QUADRATIC = new UnivariateRealFunction() {
         public double value(double x) throws FunctionEvaluationException {
             return x * x - 4.0; // roots at -2 and 2
         }
     };

     private static final UnivariateRealFunction CUBIC = new UnivariateRealFunction() {
         public double value(double x) throws FunctionEvaluationException {
             return x * x * x; // root at 0
         }
     };

     private static final UnivariateRealFunction IDENTITY = new UnivariateRealFunction() {
         public double value(double x) throws FunctionEvaluationException {
             return x; // root at 0
         }
     };

     private static final UnivariateRealFunction NO_ROOT = new UnivariateRealFunction() {
         public double value(double x) throws FunctionEvaluationException {
             return x * x + 1.0; // no real roots
         }
     };

     private static final double EPS = 1e-10;

     /**
      * Non-bracketing endpoints for solve(f,min,max):
      * f(min)*f(max) > 0 must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testBadEndpoints_solveFunction2Arg() throws Exception {
         BrentSolver solver = new BrentSolver();
         solver.solve(QUADRATIC, 0.0, 1.0); // f(0)=-4, f(1)=-3, same sign
     }

     /**
      * Non-bracketing endpoints for deprecated solve(min,max):
      * f(min)*f(max) > 0 must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testBadEndpoints_solve2ArgDeprecated() throws Exception {
         BrentSolver solver = new BrentSolver(QUADRATIC);
         solver.solve(0.0, 1.0); // non-bracketing
     }

     /**
      * All three points same sign for solve(f,min,max,initial):
      * must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testBadEndpoints_solveFunction3Arg() throws Exception {
         BrentSolver solver = new BrentSolver();
         solver.solve(QUADRATIC, 0.0, 1.0, 0.5); // all same sign
     }

     /**
      * All three points same sign for deprecated solve(min,max,initial):
      * must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testBadEndpoints_solve3ArgDeprecated() throws Exception {
         BrentSolver solver = new BrentSolver(QUADRATIC);
         solver.solve(0.0, 1.0, 0.5); // all same sign
     }

     /**
      * Valid bracketing with solve(f,min,max) finds correct root.
      */
     @Test
     public void testValidBracketing_solveFunction2Arg() throws Exception {
         BrentSolver solver = new BrentSolver();
         double root = solver.solve(QUADRATIC, 0.0, 4.0); // root at 2
         Assert.assertEquals(2.0, root, EPS);
     }

     /**
      * Valid bracketing with solve(f,min,max,initial) finds correct root.
      */
     @Test
     public void testValidBracketing_solveFunction3Arg() throws Exception {
         BrentSolver solver = new BrentSolver();
         double root = solver.solve(QUADRATIC, 0.0, 4.0, 1.5); // root at 2
         Assert.assertEquals(2.0, root, EPS);
     }

     /**
      * Root exactly at min boundary: f(min)=0 returns min.
      */
     @Test
     public void testRootAtMinBoundary() throws Exception {
         BrentSolver solver = new BrentSolver();
         double root = solver.solve(IDENTITY, 0.0, 5.0); // f(0)=0
         Assert.assertEquals(0.0, root, EPS);
     }

     /**
      * Root exactly at max boundary: f(max)=0 returns max.
      */
     @Test
     public void testRootAtMaxBoundary() throws Exception {
         BrentSolver solver = new BrentSolver();
         double root = solver.solve(IDENTITY, -5.0, 0.0); // f(0)=0
         Assert.assertEquals(0.0, root, EPS);
     }

     /**
      * min > max must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testMinGreaterThanMax() throws Exception {
         BrentSolver solver = new BrentSolver();
         solver.solve(QUADRATIC, 5.0, 1.0);
     }

     /**
      * min == max must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testMinEqualsMax() throws Exception {
         BrentSolver solver = new BrentSolver();
         solver.solve(QUADRATIC, 2.0, 2.0);
     }

     /**
      * Initial guess outside [min,max] must throw IllegalArgumentException.
      */
     @Test(expected = IllegalArgumentException.class)
     public void testInitialOutsideInterval() throws Exception {
         BrentSolver solver = new BrentSolver();
         solver.solve(QUADRATIC, 1.0, 5.0, 0.0); // initial=0 not in [1,5]
     }

     /**
      * Large domain with valid bracketing still converges to root.
      */
     @Test
     public void testLargeDomain() throws Exception {
         BrentSolver solver = new BrentSolver();
         double root = solver.solve(IDENTITY, -1000.0, 1000.0);
         Assert.assertEquals(0.0, root, EPS);
     }
 }

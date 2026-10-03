package org.apache.commons.math.analysis.solvers;

 import org.apache.commons.math.analysis.UnivariateFunction;
 import org.apache.commons.math.exception.NoBracketingException;
 import org.apache.commons.math.exception.NumberIsTooSmallException;
 import org.apache.commons.math.exception.TooManyEvaluationsException;
 import org.junit.Assert;
 import org.junit.Test;

 public class BracketingNthOrderBrentSolverTest {

     /**
      * Exercises the primary bug MATH-716: solver must converge within
      * maxEvaluations for a function that stresses the polynomial
      * interpolation and bracket‑narrowing logic.
      * Uses a steep sigmoid that is nearly flat away from the root
      * and nearly vertical at the root.
      */
     @Test
     public void testIssue716() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) {
                 // f(x) = atan(500*(x-0.4)), root at 0.4
                 return Math.atan(500.0 * (x - 0.4));
             }
         };
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-12, 1e-12, 1e-12, 5);
         double root = solver.solve(2000, f, 0.0, 1.0);
         Assert.assertEquals(0.4, root, 1e-8);
         Assert.assertTrue("evaluations must not exceed max",
             solver.getEvaluations() <= 2000);
         Assert.assertTrue("|f(root)| <= functionValueAccuracy",
             Math.abs(f.value(root)) <= solver.getFunctionValueAccuracy());
     }

     @Test
     public void testSimpleRoot() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x - 3.0; }
         };
         BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
         double root = solver.solve(100, f, 0.0, 5.0);
         Assert.assertEquals(3.0, root, solver.getAbsoluteAccuracy());
     }

     @Test(expected = NoBracketingException.class)
     public void testNoBracketingThrows() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x * x + 4.0; }
         };
         new BracketingNthOrderBrentSolver().solve(100, f, -2.0, 2.0);
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testConstructorOrderZero() {
         new BracketingNthOrderBrentSolver(1e-6, 0);
     }

     @Test(expected = NumberIsTooSmallException.class)
     public void testConstructorOrderOne() {
         new BracketingNthOrderBrentSolver(1e-6, 1);
     }

     @Test
     public void testRootNearBracketBoundary() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x - 0.001; }
         };
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-3, 1e-3, 1e-6, 5);
         double root = solver.solve(100, f, -0.001, 0.001);
         Assert.assertEquals(0.001, root, 1e-3);
         Assert.assertTrue(Math.abs(f.value(root)) <= 1e-6);
     }

     @Test
     public void testOrderTwoConvergence() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x * x - 9.0; }
         };
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-10, 2);
         Assert.assertEquals(2, solver.getMaximalOrder());
         double root = solver.solve(100, f, 0.0, 5.0);
         Assert.assertEquals(3.0, root, 1e-8);
     }

     @Test
     public void testFunctionValueAccuracyRespected() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return Math.exp(x) - 7.0; }
         };
         double funcAcc = 1e-7;
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-10, 1e-10, funcAcc, 5);
         double root = solver.solve(200, f, 0.0, 3.0);
         Assert.assertTrue("|f(root)| must be <= functionValueAccuracy",
             Math.abs(f.value(root)) <= funcAcc);
     }

     @Test
     public void testAllowedSolutionAnySide() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x * x - 5.0; }
         };
         BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
         double root = solver.solve(100, f, 0.0, 3.0, AllowedSolution.ANY_SIDE);
         Assert.assertEquals(Math.sqrt(5.0), root, solver.getAbsoluteAccuracy());
     }

     @Test
     public void testAllowedSolutionLeftSide() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x * x - 5.0; }
         };
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-12, 1e-10, 1e-10, 5);
         double root = solver.solve(200, f, 0.0, 3.0, AllowedSolution.LEFT_SIDE);
         double exact = Math.sqrt(5.0);
         Assert.assertTrue("LEFT_SIDE root must be <= exact",
             root <= exact + 1e-10);
         Assert.assertTrue("|f(root)| <= functionValueAccuracy",
             Math.abs(f.value(root)) <= solver.getFunctionValueAccuracy());
     }

     @Test
     public void testAllowedSolutionRightSide() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x * x - 5.0; }
         };
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-12, 1e-10, 1e-10, 5);
         double root = solver.solve(200, f, 0.0, 3.0, AllowedSolution.RIGHT_SIDE);
         double exact = Math.sqrt(5.0);
         Assert.assertTrue("RIGHT_SIDE root must be >= exact",
             root >= exact - 1e-10);
         Assert.assertTrue("|f(root)| <= functionValueAccuracy",
             Math.abs(f.value(root)) <= solver.getFunctionValueAccuracy());
     }

     @Test
     public void testExactRootAtEndpoint() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x - 2.0; }
         };
         BracketingNthOrderBrentSolver solver = new BracketingNthOrderBrentSolver();
         double root = solver.solve(100, f, 2.0, 4.0);
         Assert.assertEquals(2.0, root, solver.getAbsoluteAccuracy());
         Assert.assertEquals(0.0, f.value(root), 0.0);
     }

     @Test
     public void testTooManyEvaluations() {
         UnivariateFunction f = new UnivariateFunction() {
             public double value(double x) { return x - 0.5; }
         };
         BracketingNthOrderBrentSolver solver =
             new BracketingNthOrderBrentSolver(1e-15, 3);
         try {
             double root = solver.solve(3, f, 0.0, 1.0);
             Assert.assertTrue(solver.getEvaluations() <= 3);
             Assert.assertEquals(0.5, root, 1e-10);
         } catch (TooManyEvaluationsException e) {
             // acceptable if it cannot converge within the tiny budget
         }
     }
 }

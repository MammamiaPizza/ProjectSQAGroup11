package org.apache.commons.math.analysis.solvers;

 import org.junit.Test;
 import static org.junit.Assert.*;
 import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.apache.commons.math.exception.NoBracketingException;
 import org.apache.commons.math.exception.TooManyEvaluationsException;

 public class BaseSecantSolverMATH631Test {

     /** Simple cubic: f(x) = x^3 - 1, root at x=1. */
     private static final UnivariateRealFunction CUBIC = new UnivariateRealFunction() {
         public double value(double x) {
             return x * x * x - 1.0;
         }
     };

     /** Linear: f(x) = x - 3, root at 3. */
     private static final UnivariateRealFunction LINEAR = new UnivariateRealFunction() {
         public double value(double x) {
             return x - 3.0;
         }
     };

     /** Always positive except at one point that never appears – used to force no convergence. */
     private static final UnivariateRealFunction SLOW = new UnivariateRealFunction() {
         public double value(double x) {
             return Math.tanh(x - 1.0) - 0.5; // crosses zero at ~1.549 ? Actually tanh(0)=0, shift.
         }
     };

     // ---------- REGULA FALSI : normal ----------

     @Test
     public void testNormalConvergenceCubic() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0);
         assertEquals(1.0, root, 1e-6);
     }

     @Test
     public void testNormalConvergenceLinear() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, LINEAR, 2.0, 4.0);
         assertEquals(3.0, root, 1e-6);
     }

     @Test
     public void testExactRootAtMin() {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x; }
         };
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, f, 0.0, 1.0); // min is root
         assertEquals(0.0, root, 1e-15);
     }

     @Test
     public void testExactRootAtMax() {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x - 2.0; }
         };
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, f, 1.0, 2.0);
         assertEquals(2.0, root, 1e-15);
     }

     // ---------- REGULA FALSI : exceptions ----------

     @Test(expected = NoBracketingException.class)
     public void testNoBracketingException() {
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) { return x * x + 1.0; } // always positive
         };
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         solver.solve(100, f, -2.0, 2.0); // no sign change
     }

     /**
      * MATH-631: The solver does not check the evaluation count inside doSolve(),
      * so when max evaluations are reached the loop never terminates properly.
      * A correct implementation must throw TooManyEvaluationsException.
      */
     @Test(timeout = 2000, expected = TooManyEvaluationsException.class)
     public void testTooManyEvaluationsException() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         // maxEval = 3 forces the exception after the third evaluation.
         solver.solve(3, CUBIC, 0.0, 2.0);
     }

     @Test
     public void testMaxEvalSufficient() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0);
         assertEquals(1.0, root, 1e-6);
     }

     // ---------- REGULA FALSI : AllowedSolution ----------

     @Test
     public void testAllowedSolutionLeftSide() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0, 1.0, AllowedSolution.LEFT_SIDE);
         // With this simple function both sides typically converge to the root.
         assertTrue(root <= 1.0 + 1e-6);
     }

     @Test
     public void testAllowedSolutionRightSide() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0, 1.0, AllowedSolution.RIGHT_SIDE);
         assertTrue(root >= 1.0 - 1e-5);
     }

     @Test
     public void testAllowedSolutionBelowSide() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0, 1.0, AllowedSolution.BELOW_SIDE);
         assertTrue(root <= 1.0 + 1e-6);
     }

     @Test
     public void testAllowedSolutionAboveSide() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0, 1.0, AllowedSolution.ABOVE_SIDE);
         assertTrue(root >= 1.0 - 1e-5);
     }

     // ---------- OTHER SECANT SOLVERS (sanity) ----------

     @Test
     public void testIllinoisSolverNormal() {
         IllinoisSolver solver = new IllinoisSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0);
         assertEquals(1.0, root, 1e-6);
     }

     @Test
     public void testPegasusSolverNormal() {
         PegasusSolver solver = new PegasusSolver();
         double root = solver.solve(100, CUBIC, 0.0, 2.0);
         assertEquals(1.0, root, 1e-6);
     }
 }
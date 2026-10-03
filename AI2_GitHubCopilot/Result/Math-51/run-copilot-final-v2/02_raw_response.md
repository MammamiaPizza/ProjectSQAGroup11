import org.apache.commons.math.analysis.UnivariateRealFunction;
 import org.apache.commons.math.analysis.solvers.RegulaFalsiSolver;
 import org.apache.commons.math.analysis.solvers.IllinoisSolver;
 import org.apache.commons.math.analysis.solvers.PegasusSolver;
 import org.apache.commons.math.exception.NoBracketingException;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link org.apache.commons.math.analysis.solvers.BaseSecantSolver}
  * through its concrete subclasses, targeting the slow-convergence issue (MATH-631).
  */
 public class BaseSecantSolverTest {

     @Test
     public void testRegulaFalsiNormalConvergence() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x * x - 2;
             }
         };
         double root = solver.solve(100, f, 1, 2);
         assertEquals(Math.sqrt(2), root, 1e-6);
     }

     @Test
     public void testIllinoisNormalConvergence() {
         IllinoisSolver solver = new IllinoisSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x * x - 2;
             }
         };
         double root = solver.solve(100, f, 1, 2);
         assertEquals(Math.sqrt(2), root, 1e-6);
     }

     @Test
     public void testPegasusNormalConvergence() {
         PegasusSolver solver = new PegasusSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x * x - 2;
             }
         };
         double root = solver.solve(100, f, 1, 2);
         assertEquals(Math.sqrt(2), root, 1e-6);
     }

     @Test
     public void testRegulaFalsiSlowConvergenceMath631() {
         // This test exposes the bug described in MATH-631: Regula Falsi stalls
         // when one endpoint's function value is much larger than the other.
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return Math.exp(10 * x) - Math.exp(5.0);
             }
         };
         double root = solver.solve(5000, f, 0, 1);
         assertEquals(0.5, root, 1e-3);
     }

     @Test
     public void testIllinoisSlowConvergenceDoesNotThrow() {
         // Illinois method adjusts f0, so it should converge faster.
         IllinoisSolver solver = new IllinoisSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return Math.exp(10 * x) - Math.exp(5.0);
             }
         };
         double root = solver.solve(5000, f, 0, 1);
         assertEquals(0.5, root, 1e-3);
     }

     @Test
     public void testPegasusSlowConvergenceDoesNotThrow() {
         // Pegasus method adjusts f0, so it should converge faster.
         PegasusSolver solver = new PegasusSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return Math.exp(10 * x) - Math.exp(5.0);
             }
         };
         double root = solver.solve(5000, f, 0, 1);
         assertEquals(0.5, root, 1e-3);
     }

     @Test(expected = NoBracketingException.class)
     public void testNoSignChangeThrowsException() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x * x + 1;
             }
         };
         solver.solve(100, f, -1, 1);
     }

     @Test
     public void testExactRootAtLeftBoundary() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x - 2;
             }
         };
         double root = solver.solve(100, f, 2, 5);
         assertEquals(2.0, root, 1e-15);
     }

     @Test
     public void testExactRootAtRightBoundary() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x - 4;
             }
         };
         double root = solver.solve(100, f, -1, 4);
         assertEquals(4.0, root, 1e-15);
     }

     @Test
     public void testSolveWithStartValue() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x * x - 2;
             }
         };
         double root = solver.solve(100, f, 1, 2, 1.5);
         assertEquals(Math.sqrt(2), root, 1e-6);
     }

     @Test
     public void testAllowedSolutionAnySideDefault() {
         RegulaFalsiSolver solver = new RegulaFalsiSolver();
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x - 1;
             }
         };
         double root = solver.solve(100, f, 0, 2);
         assertEquals(1.0, root, 1e-15);
     }

     @Test
     public void testFunctionValueAccuracyConvergence() {
         // Verify that the solver terminates when function value is within accuracy.
         RegulaFalsiSolver solver = new RegulaFalsiSolver(1e-10, 1e-6, 1e-2);
         UnivariateRealFunction f = new UnivariateRealFunction() {
             public double value(double x) {
                 return x - 2;
             }
         };
         double root = solver.solve(100, f, 1, 3);
         assertTrue(Math.abs(root - 2.0) < 1e-2);
     }
 }
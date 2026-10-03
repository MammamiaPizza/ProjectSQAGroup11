package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.ConvergenceException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**

 - Tests for {@link BaseSecantSolver}, targeting the MATH-631 bug:
 - {@code doSolve()} must throw {@link ConvergenceException} when the
 - maximum number of evaluations is exceeded without finding a root,
 - instead of the raw {@link org.apache.commons.math.exception.TooManyEvaluationsException}.
  */
 public class BaseSecantSolverTest {
  private BaseSecantSolver createRegulaFalsiSolver() {
  return new RegulaFalsiSolver();
  }
  private BaseSecantSolver createRegulaFalsiSolver(double rel, double abs) {
  return new RegulaFalsiSolver(rel, abs);
  }
  // ----------------------------------------------------------------
  // Fault-related: MATH-631
  // ----------------------------------------------------------------
  @Test(expected = ConvergenceException.class)
  public void testExceedMaxEvaluationsThrowsConvergenceException() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x
  * x - 2;
      }
  };
  // 3 evaluations are not enough to converge; the buggy version
  // throws TooManyEvaluationsException, but the contract requires
  // ConvergenceException.
  solver.solve(3, f, 0, 2, 1.5);
  }
  // ----------------------------------------------------------------
  // Normal convergent cases
  // ----------------------------------------------------------------
  @Test
  public void testNormalConvergence() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x
  * x - 2;
      }
  };
  double root = solver.solve(200, f, 0, 3);
  Assert.assertEquals(Math.sqrt(2), root, solver.getAbsoluteAccuracy());
  }
  @Test
  public void testEndpointIsRootAtUpperBound() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x - 3;
      }
  };
  double root = solver.solve(100, f, 1, 3, 1.5);
  Assert.assertEquals(3.0, root, 1e-15);
  }
  @Test
  public void testEndpointIsRootAtLowerBound() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x;
      }
  };
  double root = solver.solve(100, f, 0, 2, 0.5);
  Assert.assertEquals(0.0, root, 1e-15);
  }
  // ----------------------------------------------------------------
  // Accuracy / boundary tests
  // ----------------------------------------------------------------
  @Test
  public void testTightAbsoluteAccuracy() {
  BaseSecantSolver solver = createRegulaFalsiSolver(1e-14, 1e-12);
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x
  * x - 3;
      }
  };
  double root = solver.solve(200, f, 1, 2);
  Assert.assertEquals(Math.sqrt(3), root, 1e-10);
  }
  @Test
  public void testZeroRelativeAccuracy() {
  BaseSecantSolver solver = new RegulaFalsiSolver(0.0, 1e-6);
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x - 9;
      }
  };
  double root = solver.solve(100, f, 0, 18);
  Assert.assertEquals(9.0, root, 1e-6);
  }
  @Test
  public void testFlatFunctionNearRoot() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          if (FastMath.abs(x - 1.0) < 1e-3) {
              return 0.0;
          }
          return x - 1.0;
      }
  };
  double root = solver.solve(200, f, 0, 5);
  Assert.assertEquals(1.0, root, solver.getAbsoluteAccuracy());
  }
  // ----------------------------------------------------------------
  // Allowed solution sides
  // ----------------------------------------------------------------
  @Test
  public void testAllowedSolutionLeftSide() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x - 1;
      }
  };
  double root = solver.solve(100, f, 0, 2, 1.0, AllowedSolution.LEFT_SIDE);
  Assert.assertTrue(root <= 1.0);
  Assert.assertTrue(FastMath.abs(f.value(root)) < 1e-5);
  }
  @Test
  public void testAllowedSolutionRightSide() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x - 1;
      }
  };
  double root = solver.solve(100, f, 0, 2, 1.0, AllowedSolution.RIGHT_SIDE);
  Assert.assertTrue(root >= 1.0);
  Assert.assertTrue(FastMath.abs(f.value(root)) < 1e-5);
  }
  @Test
  public void testAllowedSolutionBelowSide() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x - 2.5;
      }
  };
  double root = solver.solve(100, f, 0, 5, 2.0, AllowedSolution.BELOW_SIDE);
  Assert.assertTrue(f.value(root) <= 1e-5);
  }
  @Test
  public void testAllowedSolutionAboveSide() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x - 2.5;
      }
  };
  double root = solver.solve(100, f, 0, 5, 2.0, AllowedSolution.ABOVE_SIDE);
  Assert.assertTrue(f.value(root) >= -1e-5);
  }
  // ----------------------------------------------------------------
  // Invalid / exception cases
  // ----------------------------------------------------------------
  @Test(expected = MathIllegalArgumentException.class)
  public void testNonBracketingThrowsException() {
  BaseSecantSolver solver = createRegulaFalsiSolver();
  UnivariateRealFunction f = new UnivariateRealFunction() {
      public double value(double x) {
          return x
  * x + 1;
      }
  };
  // Same sign at endpoints, no bracketing.
  solver.solve(100, f, 0, 2, 1.0);
  }

}

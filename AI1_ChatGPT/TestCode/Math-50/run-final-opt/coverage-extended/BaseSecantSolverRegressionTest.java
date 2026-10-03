package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseSecantSolverRegressionTest {

    @Test(expected = TooManyEvaluationsException.class)
    public void regulaFalsiReportsEvaluationLimitForStagnatingFunction() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x * x * x * x * x * x * x * x * x - 1.0;
            }
        };

        solver.solve(100, function, 0.0, 2.0);
    }

    @Test
    public void solvesBracketedLinearRoot() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 0.25;
            }
        };

        double root = solver.solve(100, function, 0.0, 1.0);

        assertEquals(0.25, root, 0.0);
    }

    @Test
    public void returnsExactEndpointRootRegardlessOfAllowedSide() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x;
            }
        };

        double root = solver.solve(100, function, 0.0, 2.0,
                                   AllowedSolution.RIGHT_SIDE);

        assertEquals(0.0, root, 0.0);
    }

    @Test
    public void honorsBelowSideAllowedSolution() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        };

        double root = solver.solve(100, function, 0.0, 2.0,
                                   AllowedSolution.BELOW_SIDE);

        assertTrue(function.value(root) <= 0.0);
        assertTrue(root >= 0.0 && root <= 2.0);
    }

    @Test(expected = NoBracketingException.class)
    public void rejectsIntervalWithoutBracketedRoot() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x + 1.0;
            }
        };

        solver.solve(100, function, -1.0, 1.0);
    }

@Test
public void returnsExactUpperEndpointRoot() {
    final org.apache.commons.math.analysis.solvers.BaseSecantSolver solver =
        new org.apache.commons.math.analysis.solvers.BaseSecantSolver(
            1.0e-12,
            org.apache.commons.math.analysis.solvers.BaseSecantSolver.Method.ILLINOIS) {
        };

    final double root = solver.solve(10,
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x - 2.0;
            }
        },
        0.0, 2.0);

    assertEquals(2.0, root, 0.0);
}

@Test
public void illinoisMethodSolvesNonlinearBracket() {
    final org.apache.commons.math.analysis.solvers.BaseSecantSolver solver =
        new org.apache.commons.math.analysis.solvers.BaseSecantSolver(
            1.0e-12,
            org.apache.commons.math.analysis.solvers.BaseSecantSolver.Method.ILLINOIS) {
        };

    final double root = solver.solve(100,
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x * x - 2.0;
            }
        },
        0.0, 2.0);

    assertEquals(Math.sqrt(2.0), root, 1.0e-8);
}

@Test
public void pegasusMethodHonorsRightSideSolution() {
    final org.apache.commons.math.analysis.solvers.BaseSecantSolver solver =
        new org.apache.commons.math.analysis.solvers.BaseSecantSolver(
            1.0e-12,
            org.apache.commons.math.analysis.solvers.BaseSecantSolver.Method.PEGASUS) {
        };
    final org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x * x - 2.0;
            }
        };

    final double root = solver.solve(100, function, 0.0, 2.0,
        org.apache.commons.math.analysis.solvers.AllowedSolution.RIGHT_SIDE);

    assertTrue(root >= 0.0 && root <= 2.0);
    assertTrue(function.value(root) >= 0.0);
}
}

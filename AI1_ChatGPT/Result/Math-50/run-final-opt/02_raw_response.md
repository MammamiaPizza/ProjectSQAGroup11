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
}
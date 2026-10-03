package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.ConvergenceException;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseSecantSolverGeneratedTest {

    @Test(expected = ConvergenceException.class)
    public void testRegulaFalsiDetectsRepeatedInterpolationPoint() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();

        solver.solve(20, new UnivariateRealFunction() {
            public double value(double x) {
                return x == 0.0 ? -Double.MAX_VALUE : 1.0;
            }
        }, 0.0, 1.0);
    }

    @Test
    public void testSolvesNonLinearBracketedFunction() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();

        double result = solver.solve(100, new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        }, 1.0, 2.0);

        assertEquals(Math.sqrt(2.0), result, 2.0e-6);
    }

    @Test
    public void testReturnsExactLowerEndpointRoot() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();

        double result = solver.solve(10, new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        }, 2.0, 5.0);

        assertEquals(2.0, result, 0.0);
    }

    @Test(expected = NoBracketingException.class)
    public void testRejectsIntervalWithoutBracketedRoot() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();

        solver.solve(10, new UnivariateRealFunction() {
            public double value(double x) {
                return x * x + 1.0;
            }
        }, -1.0, 1.0);
    }

    @Test(expected = TooManyEvaluationsException.class)
    public void testHonorsMaximumEvaluationCountForOrdinaryIteration() {
        RegulaFalsiSolver solver = new RegulaFalsiSolver();

        solver.solve(2, new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        }, 1.0, 2.0);
    }
}

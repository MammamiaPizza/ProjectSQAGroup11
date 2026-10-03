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

@org.junit.Test
public void testReturnsExactUpperEndpointRoot() throws Exception {
    final org.apache.commons.math.analysis.solvers.RegulaFalsiSolver solver =
        new org.apache.commons.math.analysis.solvers.RegulaFalsiSolver();

    final double result = solver.solve(100,
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x - 2.0;
            }
        },
        0.0, 2.0);

    org.junit.Assert.assertEquals(2.0, result, 0.0);
}

@org.junit.Test
public void testReturnsExactInterpolatedRoot() throws Exception {
    final org.apache.commons.math.analysis.solvers.RegulaFalsiSolver solver =
        new org.apache.commons.math.analysis.solvers.RegulaFalsiSolver();

    final double result = solver.solve(100,
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x - 0.5;
            }
        },
        0.0, 1.0);

    org.junit.Assert.assertEquals(0.5, result, 0.0);
}

@org.junit.Test
public void testIllinoisSolverConvergesForNonLinearFunction() throws Exception {
    final org.apache.commons.math.analysis.solvers.IllinoisSolver solver =
        new org.apache.commons.math.analysis.solvers.IllinoisSolver();

    final double result = solver.solve(100,
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x * x - 2.0;
            }
        },
        0.0, 2.0);

    org.junit.Assert.assertEquals(Math.sqrt(2.0), result, 2.0e-6);
}

@org.junit.Test
public void testPegasusSolverConvergesForNonLinearFunction() throws Exception {
    final org.apache.commons.math.analysis.solvers.PegasusSolver solver =
        new org.apache.commons.math.analysis.solvers.PegasusSolver();

    final double result = solver.solve(100,
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x * x - 2.0;
            }
        },
        0.0, 2.0);

    org.junit.Assert.assertEquals(Math.sqrt(2.0), result, 2.0e-6);
}
}

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BracketingNthOrderBrentSolverGeneratedTest {

    @Test
    public void testIssue716ConvergesWithinEvaluationLimit() {
        final UnivariateFunction function = new UnivariateFunction() {
            public double value(final double x) {
                return Math.sin(x) - x / 2.0;
            }
        };

        final BracketingNthOrderBrentSolver solver =
                new BracketingNthOrderBrentSolver(1.0e-12, 5);

        final double root = solver.solve(100, function, 1.0, 4.0);

        assertEquals(1.895494267033981, root, 1.0e-10);
        assertTrue("solver must converge before exhausting the evaluation budget",
                   solver.getEvaluations() < 100);
    }

    @Test
    public void testSolvesSmoothNonlinearBracketedRoot() {
        final UnivariateFunction function = new UnivariateFunction() {
            public double value(final double x) {
                return Math.exp(x) - 3.0;
            }
        };

        final BracketingNthOrderBrentSolver solver =
                new BracketingNthOrderBrentSolver(1.0e-12, 5);

        final double root = solver.solve(100, function, 0.0, 2.0);

        assertEquals(Math.log(3.0), root, 1.0e-10);
        assertTrue(solver.getEvaluations() < 100);
    }

    @Test
    public void testReturnsRootAtLowerEndpoint() {
        final UnivariateFunction function = new UnivariateFunction() {
            public double value(final double x) {
                return x * x;
            }
        };

        final BracketingNthOrderBrentSolver solver =
                new BracketingNthOrderBrentSolver(1.0e-12, 5);

        assertEquals(0.0, solver.solve(20, function, 0.0, 2.0), 0.0);
    }

    @Test
    public void testReturnsRootAtUpperEndpoint() {
        final UnivariateFunction function = new UnivariateFunction() {
            public double value(final double x) {
                return x - 2.0;
            }
        };

        final BracketingNthOrderBrentSolver solver =
                new BracketingNthOrderBrentSolver(1.0e-12, 5);

        assertEquals(2.0, solver.solve(20, function, 0.0, 2.0), 0.0);
    }

    @Test(expected = NoBracketingException.class)
    public void testRejectsIntervalWithoutSignChange() {
        final UnivariateFunction function = new UnivariateFunction() {
            public double value(final double x) {
                return x * x + 1.0;
            }
        };

        final BracketingNthOrderBrentSolver solver =
                new BracketingNthOrderBrentSolver(1.0e-12, 5);

        solver.solve(20, function, -1.0, 1.0);
    }

    @Test
    public void testMaximalOrderIsExposedAndTooSmallOrderIsRejected() {
        final BracketingNthOrderBrentSolver solver =
                new BracketingNthOrderBrentSolver(1.0e-10, 7);

        assertEquals(7, solver.getMaximalOrder());

        try {
            new BracketingNthOrderBrentSolver(1.0e-10, 1);
            fail("maximal interpolation order below two must be rejected");
        } catch (NumberIsTooSmallException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

@Test
public void testAllowedSolutionsRespectRequestedBracketSide() {
    final org.apache.commons.math.analysis.UnivariateFunction function =
        new org.apache.commons.math.analysis.UnivariateFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        };
    final double root = Math.sqrt(2.0);

    final double left = new BracketingNthOrderBrentSolver().solve(
        100, function, 1.0, 2.0,
        org.apache.commons.math.analysis.solvers.AllowedSolution.LEFT_SIDE);
    final double right = new BracketingNthOrderBrentSolver().solve(
        100, function, 1.0, 2.0,
        org.apache.commons.math.analysis.solvers.AllowedSolution.RIGHT_SIDE);
    final double below = new BracketingNthOrderBrentSolver().solve(
        100, function, 1.0, 2.0,
        org.apache.commons.math.analysis.solvers.AllowedSolution.BELOW_SIDE);
    final double above = new BracketingNthOrderBrentSolver().solve(
        100, function, 1.0, 2.0,
        org.apache.commons.math.analysis.solvers.AllowedSolution.ABOVE_SIDE);

    assertTrue(left <= root);
    assertTrue(right >= root);
    assertTrue(function.value(below) <= 0.0);
    assertTrue(function.value(above) >= 0.0);
}

@Test
public void testReturnsExactInitialGuess() {
    final org.apache.commons.math.analysis.UnivariateFunction function =
        new org.apache.commons.math.analysis.UnivariateFunction() {
            public double value(double x) {
                return x - 1.5;
            }
        };

    final double root = new BracketingNthOrderBrentSolver().solve(
        20, function, 0.0, 2.0, 1.5);

    assertEquals(1.5, root, 0.0);
}

@Test
public void testFullAccuracyConstructorAcceptsConfiguredOrder() {
    final BracketingNthOrderBrentSolver solver =
        new BracketingNthOrderBrentSolver(1.0e-12, 1.0e-10, 1.0e-14, 4);

    assertEquals(4, solver.getMaximalOrder());
}
}

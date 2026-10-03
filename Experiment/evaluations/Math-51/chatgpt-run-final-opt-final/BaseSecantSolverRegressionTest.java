package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.junit.Assert;
import org.junit.Test;

public class BaseSecantSolverRegressionTest {

    @Test(expected = TooManyEvaluationsException.class)
    public void testRegulaFalsiDetectsStagnationInsteadOfExhaustingEvaluations() {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                final double y = 1.0 - x;
                return 1.0 - 1.0e20 * y * y;
            }
        };

        new RegulaFalsiSolver().solve(3624, function, 0.0, 1.0);
    }

    @Test
    public void testReturnsExactRootAtEitherInitialEndpoint() {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        };

        Assert.assertEquals(2.0,
                            new RegulaFalsiSolver().solve(10, function, 2.0, 5.0),
                            0.0);
        Assert.assertEquals(2.0,
                            new RegulaFalsiSolver().solve(10, function, -1.0, 2.0),
                            0.0);
    }

    @Test
    public void testIllinoisSolverConvergesOnBracketedNonlinearRoot() {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        };

        double root = new IllinoisSolver().solve(100, function, 0.0, 2.0);

        Assert.assertEquals(Math.sqrt(2.0), root, 2.0e-6);
        Assert.assertTrue(Math.abs(function.value(root)) <= 1.0e-5);
    }

    @Test
    public void testPegasusSolverConvergesOnBracketedNonlinearRoot() {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        };

        double root = new PegasusSolver().solve(100, function, 0.0, 2.0);

        Assert.assertEquals(Math.sqrt(2.0), root, 2.0e-6);
        Assert.assertTrue(Math.abs(function.value(root)) <= 1.0e-5);
    }

    @Test
    public void testAllowedSolutionBelowSideReturnsNonPositiveFunctionValue() {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x - 2.0;
            }
        };

        double root = new IllinoisSolver().solve(100, function, 0.0, 2.0,
                                                 AllowedSolution.BELOW_SIDE);

        Assert.assertTrue(function.value(root) <= 0.0);
        Assert.assertEquals(Math.sqrt(2.0), root, 2.0e-6);
    }

    @Test(expected = NoBracketingException.class)
    public void testRejectsInitialIntervalWithoutOppositeSigns() {
        UnivariateRealFunction function = new UnivariateRealFunction() {
            public double value(double x) {
                return x * x + 1.0;
            }
        };

        new RegulaFalsiSolver().solve(20, function, -1.0, 1.0);
    }

@org.junit.Test
public void testRegulaFalsiReturnsAnInterpolatedExactRoot() {
    final org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x - 0.25;
            }
        };

    final double root = new RegulaFalsiSolver().solve(10, function, 0.0, 1.0);

    org.junit.Assert.assertEquals(0.25, root, 0.0);
    org.junit.Assert.assertEquals(0.0, function.value(root), 0.0);
}

@org.junit.Test
public void testAllowedSolutionAboveSideReturnsNonNegativeFunctionValue() {
    final org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return 1.0e-14 * (x * x - 2.0);
            }
        };

    final double root = new IllinoisSolver().solve(1000, function, 1.0, 2.0,
                                                   org.apache.commons.math.analysis.solvers.AllowedSolution.ABOVE_SIDE);

    org.junit.Assert.assertTrue(function.value(root) >= 0.0);
}

@org.junit.Test
public void testAllowedSolutionLeftAndRightSidesRespectIntervalOrder() {
    final org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return 1.0e10 * (x * x - 2.0);
            }
        };

    final double left = new IllinoisSolver().solve(1000, function, 1.0, 2.0,
                                                   org.apache.commons.math.analysis.solvers.AllowedSolution.LEFT_SIDE);
    final double right = new IllinoisSolver().solve(1000, function, 1.0, 2.0,
                                                    org.apache.commons.math.analysis.solvers.AllowedSolution.RIGHT_SIDE);

    org.junit.Assert.assertTrue(left <= Math.sqrt(2.0));
    org.junit.Assert.assertTrue(right >= Math.sqrt(2.0));
}
}

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
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
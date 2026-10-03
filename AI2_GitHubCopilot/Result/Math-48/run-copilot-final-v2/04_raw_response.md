@Test
public void testIllinoisMethodConverges() {
    // Covers the ILLINOIS branch (lines 181-182) in BaseSecantSolver.doSolve()
    IllinoisSolver solver = new IllinoisSolver();
    UnivariateFunction f = new UnivariateFunction() {
        public double value(double x) { return x * x - 4.0; }
    };
    double root = solver.solve(100, f, 0.0, 3.0);
    Assert.assertEquals(2.0, root, 1e-6);
}

@Test
public void testPegasusMethodConverges() {
    // Covers the PEGASUS branch (lines 184-185) in BaseSecantSolver.doSolve()
    PegasusSolver solver = new PegasusSolver();
    UnivariateFunction f = new UnivariateFunction() {
        public double value(double x) { return x * x - 9.0; }
    };
    double root = solver.solve(100, f, 0.0, 5.0);
    Assert.assertEquals(3.0, root, 1e-6);
}

@Test(expected = org.apache.commons.math.exception.ConvergenceException.class)
public void testRegulaFalsiExceedsMaxEvaluationsThrowsConvergenceException() {
    // Verifies the fault-related behavior of MATH-631: exceeding evaluations must
    // throw ConvergenceException, not TooManyEvaluationsException.
    // Also incidentally covers the 4-argument constructor (lines 101-104).
    UnivariateFunction f = new UnivariateFunction() {
        public double value(double x) { return x * x - 2.0; }
    };
    RegulaFalsiSolver solver = new RegulaFalsiSolver(1e-10, 1e-15, 1e-14);
    solver.setMaxEvaluations(5);
    solver.solve(100, f, 1.0, 2.0);
}

@Test
public void testAllowedSolutionRightSideWhenDecreasing() {
    // Covers the RIGHT_SIDE branch when the function is decreasing (inverted interval),
    // exercising lines 210-215 (break without returning) and subsequent convergence logic.
    UnivariateFunction f = new UnivariateFunction() {
        public double value(double x) { return 2.0 - x; }
    };
    IllinoisSolver solver = new IllinoisSolver();
    double root = solver.solve(100, f, 0.0, 5.0, 2.5,
            org.apache.commons.math.analysis.solvers.AllowedSolution.RIGHT_SIDE);
    Assert.assertTrue("root should be >= 2.0", root >= 2.0);
    Assert.assertEquals(0.0, f.value(root), 1e-5);
}
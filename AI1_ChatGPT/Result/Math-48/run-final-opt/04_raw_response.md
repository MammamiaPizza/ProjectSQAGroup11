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
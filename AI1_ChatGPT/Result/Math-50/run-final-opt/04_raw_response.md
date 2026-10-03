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
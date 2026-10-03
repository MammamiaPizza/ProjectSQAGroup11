@Test
public void solveWithInitialReturnsMinEndpointWhenItIsARoot() {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        };

    BrentSolver solver = new BrentSolver(function);
    assertEquals(2.0, solver.solve(2.0, 4.0, 3.0), 0.0);
}

@Test
public void solveWithInitialReturnsMaxEndpointWhenItIsARoot() {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return x - 3.0;
            }
        };

    BrentSolver solver = new BrentSolver(function);
    assertEquals(3.0, solver.solve(1.0, 3.0, 2.0), 0.0);
}

@Test
public void solveWithInitialUsesInitialToMaxBracketWhenEndpointsHaveSameSign() {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return (x - 2.0) * (x - 4.0);
            }
        };

    BrentSolver solver = new BrentSolver(function);
    double root = solver.solve(0.0, 5.0, 3.0);

    assertTrue(Math.abs(function.value(root)) <= 1.0e-6);
    assertEquals(4.0, root, 1.0e-6);
}
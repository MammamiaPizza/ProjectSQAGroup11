@Test
public void solveWithoutInitialReturnsMinEndpointCoordinateForNumericalZero() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return Math.sin(x);
            }
        };

    BrentSolver solver = new BrentSolver();
    assertEquals(Math.PI, solver.solve(function, Math.PI, 2.0 * Math.PI), 0.0);
}

@Test
public void solveWithoutInitialReturnsMaxEndpointCoordinateForNumericalZero() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return Math.sin(x);
            }
        };

    BrentSolver solver = new BrentSolver();
    assertEquals(Math.PI, solver.solve(function, 3.0, Math.PI), 0.0);
}

@Test
public void solveWithInitialUsesMinToInitialBracket() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return x - 1.0;
            }
        };

    BrentSolver solver = new BrentSolver();
    assertEquals(1.0, solver.solve(function, 0.0, 4.0, 2.0), 0.0);
}

@Test
public void solveWithInitialUsesInitialToMaxBracket() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return x - 3.0;
            }
        };

    BrentSolver solver = new BrentSolver();
    assertEquals(3.0, solver.solve(function, 0.0, 4.0, 2.0), 0.0);
}
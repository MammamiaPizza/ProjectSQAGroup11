@Test
public void testInitialGuessExactRootIsReturned() throws Exception {
    org.apache.commons.math.analysis.BrentSolver solver =
        new org.apache.commons.math.analysis.BrentSolver(
            new org.apache.commons.math.analysis.UnivariateRealFunction() {
                public double value(double x) {
                    return x - 3.0;
                }
            });

    org.junit.Assert.assertEquals(3.0, solver.solve(0.0, 5.0, 3.0), 0.0);
}

@Test
public void testInitialGuessVariantReturnsLowerEndpointRoot() throws Exception {
    org.apache.commons.math.analysis.BrentSolver solver =
        new org.apache.commons.math.analysis.BrentSolver(
            new org.apache.commons.math.analysis.UnivariateRealFunction() {
                public double value(double x) {
                    return x - 2.0;
                }
            });

    org.junit.Assert.assertEquals(2.0, solver.solve(2.0, 4.0, 3.0), 0.0);
}

@Test
public void testInitialGuessVariantReturnsUpperEndpointRoot() throws Exception {
    org.apache.commons.math.analysis.BrentSolver solver =
        new org.apache.commons.math.analysis.BrentSolver(
            new org.apache.commons.math.analysis.UnivariateRealFunction() {
                public double value(double x) {
                    return x - 4.0;
                }
            });

    org.junit.Assert.assertEquals(4.0, solver.solve(2.0, 4.0, 3.0), 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testInitialGuessVariantRejectsThreeSameSignValues() throws Exception {
    org.apache.commons.math.analysis.BrentSolver solver =
        new org.apache.commons.math.analysis.BrentSolver(
            new org.apache.commons.math.analysis.UnivariateRealFunction() {
                public double value(double x) {
                    return x * x + 1.0;
                }
            });

    solver.solve(-1.0, 1.0, 0.0);
}
@Test
public void testDefaultBracketOverloadAcceptsExactUpperEndpointRoot() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return x - 2.0;
            }
        };

    double[] bracket = UnivariateRealSolverUtils.bracket(
        function, 1.0, 0.0, Double.MAX_VALUE);

    assertEquals(0.0, function.value(bracket[1]), 0.0);
}

@Test(expected = IllegalArgumentException.class)
public void testBracketRejectsNullFunction() throws Exception {
    UnivariateRealSolverUtils.bracket(
        (org.apache.commons.math.analysis.UnivariateRealFunction) null,
        0.0, -1.0, 1.0, 1);
}

@Test
public void testBracketRejectsInvalidInitialAndBounds() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction function =
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(double x) {
                return x;
            }
        };

    try {
        UnivariateRealSolverUtils.bracket(function, -1.0, 0.0, 1.0, 1);
        org.junit.Assert.fail("Expected invalid initial value to be rejected");
    } catch (IllegalArgumentException expected) {
    }

    try {
        UnivariateRealSolverUtils.bracket(function, 1.0, 1.0, 1.0, 1);
        org.junit.Assert.fail("Expected invalid bounds to be rejected");
    } catch (IllegalArgumentException expected) {
    }
}
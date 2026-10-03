@Test
public void testBracketSimpleFourParam() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction f = new
org.apache.commons.math.analysis.UnivariateRealFunction() {
        public double value(double x) { return x - 5.0; }
    };
    double[] bracket = org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(f,
1.0, 0.0, 10.0);
    assertNotNull(bracket);
    assertEquals(2, bracket.length);
    assertTrue(f.value(bracket[0]) * f.value(bracket[1]) <= 0.0);
}

@Test(expected = org.apache.commons.math.ConvergenceException.class)
public void testBracketThrowsWhenFBEqualsZero() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction f = new
org.apache.commons.math.analysis.UnivariateRealFunction() {
        public double value(double x) { return x - 2.0; }
    };
    org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(f, 1.0, 0.0, 2.0,
1000);
}

@Test(expected = org.apache.commons.math.ConvergenceException.class)
public void testBracketThrowsWhenNoOppositeSigns() throws Exception {
    org.apache.commons.math.analysis.UnivariateRealFunction f = new
org.apache.commons.math.analysis.UnivariateRealFunction() {
        public double value(double x) { return 1.0; }
    };
    org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(f, 0.0, -100.0,
100.0, 1000);
}

@Test(expected = IllegalArgumentException.class)
public void testBracketLowerBoundGTEUpperBound() {
    org.apache.commons.math.analysis.UnivariateRealFunction f = new
org.apache.commons.math.analysis.UnivariateRealFunction() {
        public double value(double x) { return x - 1.0; }
    };
    org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils.bracket(f, 1.0, 5.0, 5.0,
1000);
}
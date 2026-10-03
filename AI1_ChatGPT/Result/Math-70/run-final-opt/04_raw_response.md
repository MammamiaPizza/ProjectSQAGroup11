@org.junit.Test(expected = org.apache.commons.math.MaxIterationsExceededException.class)
public void throwsWhenBisectionCannotConvergeWithinMaximumIterations() {
    new BisectionSolver().solve(
        new org.apache.commons.math.analysis.UnivariateRealFunction() {
            public double value(final double x) {
                return x - 1.0;
            }
        },
        -1.0e100,
        1.0e100);
}
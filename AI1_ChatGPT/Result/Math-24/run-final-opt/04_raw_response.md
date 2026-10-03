@org.junit.Test
public void testMinimizationWithReversedBoundsFindsInteriorMinimum() {
    final BrentOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
    final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        optimizer.optimize(100,
                           new org.apache.commons.math3.analysis.UnivariateFunction() {
                               public double value(double x) {
                                   final double d = x - 2.0;
                                   return d * d;
                               }
                           },
                           org.apache.commons.math3.optimization.GoalType.MINIMIZE,
                           4.0, -4.0, 3.0);

    org.junit.Assert.assertEquals(2.0, result.getPoint(), 1e-8);
    org.junit.Assert.assertEquals(0.0, result.getValue(), 1e-16);
}

@org.junit.Test
public void testImmediateConvergenceCheckerAcceptsInitialPreviousNull() {
    final boolean[] sawNullPrevious = new boolean[1];
    final BrentOptimizer optimizer = new BrentOptimizer(
        1e-10, 1e-14,
        new org.apache.commons.math3.optimization.ConvergenceChecker<org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair>() {
            public boolean converged(
                int iteration,
                org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair previous,
                org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair current) {
                sawNullPrevious[0] = previous == null;
                return true;
            }
        });

    final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        optimizer.optimize(100,
                           new org.apache.commons.math3.analysis.UnivariateFunction() {
                               public double value(double x) {
                                   return x * x;
                               }
                           },
                           org.apache.commons.math3.optimization.GoalType.MINIMIZE,
                           -1.0, 1.0, 0.0);

    org.junit.Assert.assertTrue(sawNullPrevious[0]);
    org.junit.Assert.assertNotNull(result);
    org.junit.Assert.assertEquals(result.getPoint() * result.getPoint(),
                                  result.getValue(), 0.0);
}

@org.junit.Test
public void testMaximizationCheckerReturnsBetterPreviousCandidate() {
    final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair[] expected =
        new org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair[1];
    final BrentOptimizer optimizer = new BrentOptimizer(
        1e-10, 1e-14,
        new org.apache.commons.math3.optimization.ConvergenceChecker<org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair>() {
            public boolean converged(
                int iteration,
                org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair previous,
                org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair current) {
                if (previous != null && current.getValue() < previous.getValue()) {
                    expected[0] = previous;
                    return true;
                }
                return false;
            }
        });

    final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        optimizer.optimize(100,
                           new org.apache.commons.math3.analysis.UnivariateFunction() {
                               public double value(double x) {
                                   return x;
                               }
                           },
                           org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
                           -1.0, 1.0, 0.0);

    org.junit.Assert.assertNotNull(expected[0]);
    org.junit.Assert.assertEquals(expected[0].getPoint(), result.getPoint(), 0.0);
    org.junit.Assert.assertEquals(expected[0].getValue(), result.getValue(), 0.0);
}
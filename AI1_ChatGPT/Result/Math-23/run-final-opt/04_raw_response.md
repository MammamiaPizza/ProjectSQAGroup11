@org.junit.Test
public void testCheckerRetainsImprovedPointForMinimization() {
    final org.apache.commons.math3.optimization.ConvergenceChecker<org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair> checker =
        new org.apache.commons.math3.optimization.ConvergenceChecker<org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair>() {
            public boolean converged(final int iteration,
                                     final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair previous,
                                     final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair current) {
                return true;
            }
        };

    final org.apache.commons.math3.optimization.univariate.BrentOptimizer optimizer =
        new org.apache.commons.math3.optimization.univariate.BrentOptimizer(1e-10, 1e-14, checker);
    final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        optimizer.optimize(100,
                           new org.apache.commons.math3.analysis.UnivariateFunction() {
                               public double value(final double x) {
                                   return -x;
                               }
                           },
                           org.apache.commons.math3.optimization.GoalType.MINIMIZE,
                           0.0, 1.0, 0.0);

    org.junit.Assert.assertTrue(result.getPoint() > 0.0);
    org.junit.Assert.assertTrue(result.getValue() < 0.0);
}

@org.junit.Test
public void testCheckerRetainsImprovedPointForMaximization() {
    final org.apache.commons.math3.optimization.ConvergenceChecker<org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair> checker =
        new org.apache.commons.math3.optimization.ConvergenceChecker<org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair>() {
            public boolean converged(final int iteration,
                                     final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair previous,
                                     final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair current) {
                return true;
            }
        };

    final org.apache.commons.math3.optimization.univariate.BrentOptimizer optimizer =
        new org.apache.commons.math3.optimization.univariate.BrentOptimizer(1e-10, 1e-14, checker);
    final org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        optimizer.optimize(100,
                           new org.apache.commons.math3.analysis.UnivariateFunction() {
                               public double value(final double x) {
                                   return x;
                               }
                           },
                           org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
                           0.0, 1.0, 0.0);

    org.junit.Assert.assertTrue(result.getPoint() > 0.0);
    org.junit.Assert.assertTrue(result.getValue() > 0.0);
}
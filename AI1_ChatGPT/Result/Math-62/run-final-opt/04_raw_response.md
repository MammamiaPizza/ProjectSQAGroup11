@org.junit.Test
public void testConfigurationIsForwardedToUnderlyingOptimizer() {
    final org.apache.commons.math.optimization.univariate.MultiStartUnivariateRealOptimizer<
        org.apache.commons.math.analysis.UnivariateRealFunction> optimizer =
        new org.apache.commons.math.optimization.univariate.MultiStartUnivariateRealOptimizer<
            org.apache.commons.math.analysis.UnivariateRealFunction>(
            new org.apache.commons.math.optimization.univariate.BrentOptimizer(),
            2,
            new org.apache.commons.math.random.JDKRandomGenerator());

    final org.apache.commons.math.optimization.ConvergenceChecker<
        org.apache.commons.math.optimization.UnivariateRealPointValuePair> checker =
        new org.apache.commons.math.optimization.ConvergenceChecker<
            org.apache.commons.math.optimization.UnivariateRealPointValuePair>() {
            public boolean converged(
                    int iteration,
                    org.apache.commons.math.optimization.UnivariateRealPointValuePair previous,
                    org.apache.commons.math.optimization.UnivariateRealPointValuePair current) {
                return false;
            }
        };

    optimizer.setConvergenceChecker(checker);
    optimizer.setMaxEvaluations(37);

    org.junit.Assert.assertSame(checker, optimizer.getConvergenceChecker());
    org.junit.Assert.assertEquals(37, optimizer.getMaxEvaluations());
}

@org.junit.Test(expected = org.apache.commons.math.ConvergenceException.class)
public void testThrowsConvergenceExceptionWhenEveryStartFails() throws Exception {
    final org.apache.commons.math.optimization.univariate.MultiStartUnivariateRealOptimizer<
        org.apache.commons.math.analysis.UnivariateRealFunction> optimizer =
        new org.apache.commons.math.optimization.univariate.MultiStartUnivariateRealOptimizer<
            org.apache.commons.math.analysis.UnivariateRealFunction>(
            new org.apache.commons.math.optimization.univariate.BrentOptimizer(),
            3,
            new org.apache.commons.math.random.JDKRandomGenerator());

    optimizer.optimize(new org.apache.commons.math.analysis.UnivariateRealFunction() {
        public double value(double x) throws org.apache.commons.math.FunctionEvaluationException {
            throw new org.apache.commons.math.FunctionEvaluationException(x, "forced failure");
        }
    }, org.apache.commons.math.optimization.GoalType.MINIMIZE, -1.0, 1.0);
}
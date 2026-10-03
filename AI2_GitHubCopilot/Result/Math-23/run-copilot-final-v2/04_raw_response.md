@Test
public void testSwapInterval() {
    org.apache.commons.math3.analysis.UnivariateFunction f =
        new org.apache.commons.math3.analysis.UnivariateFunction() {
            @Override
            public double value(double x) {
                return x * x;
            }
        };
    org.apache.commons.math3.optimization.univariate.BrentOptimizer opt =
        new org.apache.commons.math3.optimization.univariate.BrentOptimizer(1e-10, 1e-14);
    org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        opt.optimize(100, f, org.apache.commons.math3.optimization.GoalType.MINIMIZE, 5, -5, 0);
    Assert.assertTrue("Result should be within reversed bounds",
        result.getPoint() >= -5 && result.getPoint() <= 5);
    Assert.assertTrue("Result value should be <= function at initial guess",
        result.getValue() <= f.value(0));
}

@Test
public void testMinimizationKeepInitBestAtRightBound() {
    org.apache.commons.math3.analysis.UnivariateFunction f =
        new org.apache.commons.math3.analysis.UnivariateFunction() {
            @Override
            public double value(double x) {
                double t = x - 10;
                return t * t;
            }
        };
    org.apache.commons.math3.optimization.univariate.BrentOptimizer opt =
        new org.apache.commons.math3.optimization.univariate.BrentOptimizer(1e-10, 1e-14);
    org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        opt.optimize(100, f, org.apache.commons.math3.optimization.GoalType.MINIMIZE, 0, 20, 10);
    Assert.assertEquals(10.0, result.getPoint(), 1e-8);
    Assert.assertEquals(0.0, result.getValue(), 1e-12);
}

@Test
public void testMaximizationKeepInitBestAtRightBound() {
    org.apache.commons.math3.analysis.UnivariateFunction f =
        new org.apache.commons.math3.analysis.UnivariateFunction() {
            @Override
            public double value(double x) {
                double t = x - 10;
                return -t * t;
            }
        };
    org.apache.commons.math3.optimization.univariate.BrentOptimizer opt =
        new org.apache.commons.math3.optimization.univariate.BrentOptimizer(1e-10, 1e-14);
    org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        opt.optimize(100, f, org.apache.commons.math3.optimization.GoalType.MAXIMIZE, 0, 20, 10);
    Assert.assertEquals(10.0, result.getPoint(), 1e-8);
    Assert.assertEquals(0.0, result.getValue(), 1e-12);
}

@Test
public void testBrentStepNegativeSmall() {
    org.apache.commons.math3.analysis.UnivariateFunction f =
        new org.apache.commons.math3.analysis.UnivariateFunction() {
            @Override
            public double value(double x) {
                double t = x - 1;
                return t * t;
            }
        };
    org.apache.commons.math3.optimization.univariate.BrentOptimizer opt =
        new org.apache.commons.math3.optimization.univariate.BrentOptimizer(1e-10, 1e-14);
    org.apache.commons.math3.optimization.univariate.UnivariatePointValuePair result =
        opt.optimize(100, f, org.apache.commons.math3.optimization.GoalType.MINIMIZE, -5, 5, 5);
    Assert.assertEquals(1.0, result.getPoint(), 1e-8);
    Assert.assertEquals(0.0, result.getValue(), 1e-12);
}
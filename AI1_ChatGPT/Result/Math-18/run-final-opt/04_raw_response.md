@Test(expected = org.apache.commons.math3.exception.MathUnsupportedOperationException.class)
public void testMixedFiniteAndInfiniteBoundsAreRejected() {
    new CMAESOptimizer().optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 1.0 },
            new double[] { 0.0 },
            new double[] { Double.POSITIVE_INFINITY });
}

@Test(expected = org.apache.commons.math3.exception.DimensionMismatchException.class)
public void testInputSigmaDimensionMustMatchStartPointDimension() {
    new CMAESOptimizer(0, new double[] { 0.1, 0.1 }).optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.0 },
            new double[] { Double.NEGATIVE_INFINITY },
            new double[] { Double.POSITIVE_INFINITY });
}

@Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
public void testNegativeInputSigmaIsRejected() {
    new CMAESOptimizer(0, new double[] { -0.1 }).optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.0 },
            new double[] { -1.0 },
            new double[] { 1.0 });
}

@Test(expected = org.apache.commons.math3.exception.OutOfRangeException.class)
public void testInputSigmaLargerThanBoundedRangeIsRejected() {
    new CMAESOptimizer(0, new double[] { 2.0 }).optimize(10,
            new org.apache.commons.math3.analysis.MultivariateFunction() {
                public double value(double[] point) {
                    return point[0] * point[0];
                }
            },
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5 },
            new double[] { 0.0 },
            new double[] { 1.0 });
}
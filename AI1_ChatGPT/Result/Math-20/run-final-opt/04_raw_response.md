@Test(expected = org.apache.commons.math3.exception.MathUnsupportedOperationException.class)
public void rejectsMixedFiniteAndInfiniteBounds() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { 0.1, 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.0 },
            new double[] { 0.0, Double.NEGATIVE_INFINITY },
            new double[] { 1.0, Double.POSITIVE_INFINITY });
}

@Test(expected = org.apache.commons.math3.exception.DimensionMismatchException.class)
public void rejectsSigmaWithWrongDimension() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.5 },
            new double[] { 0.0, 0.0 },
            new double[] { 1.0, 1.0 });
}

@Test(expected = org.apache.commons.math3.exception.NotPositiveException.class)
public void rejectsNegativeSigma() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { -0.1, 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.5 },
            new double[] { 0.0, 0.0 },
            new double[] { 1.0, 1.0 });
}

@Test(expected = org.apache.commons.math3.exception.OutOfRangeException.class)
public void rejectsSigmaLargerThanBoundedRange() {
    new org.apache.commons.math3.optimization.direct.CMAESOptimizer(4,
            new double[] { 1.1, 0.1 }).optimize(10, cmaesValidationObjective(),
            org.apache.commons.math3.optimization.GoalType.MINIMIZE,
            new double[] { 0.5, 0.5 },
            new double[] { 0.0, 0.0 },
            new double[] { 1.0, 1.0 });
}

private org.apache.commons.math3.analysis.MultivariateFunction cmaesValidationObjective() {
    return new org.apache.commons.math3.analysis.MultivariateFunction() {
        public double value(double[] point) {
            return 0.0;
        }
    };
}
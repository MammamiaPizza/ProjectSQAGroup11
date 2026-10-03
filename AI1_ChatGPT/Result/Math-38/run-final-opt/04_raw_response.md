@org.junit.Test
public void optimizationFromOppositeBoundsConvergesToAnInteriorMinimum() {
    final org.apache.commons.math.optimization.direct.BOBYQAOptimizer optimizer =
        new org.apache.commons.math.optimization.direct.BOBYQAOptimizer(6);
    final org.apache.commons.math.optimization.RealPointValuePair result =
        optimizer.optimize(10000,
                           new org.apache.commons.math.analysis.MultivariateFunction() {
                               public double value(double[] point) {
                                   final double dx = point[0] - 0.75;
                                   final double dy = point[1] + 0.25;
                                   return dx * dx + dy * dy;
                               }
                           },
                           org.apache.commons.math.optimization.GoalType.MINIMIZE,
                           new double[] { 0.0, 2.0 },
                           new double[] { 0.0, -1.0 },
                           new double[] { 2.0, 2.0 });

    final double[] point = result.getPoint();
    org.junit.Assert.assertEquals(0.0, result.getValue(), 1.0e-8);
    org.junit.Assert.assertEquals(0.75, point[0], 1.0e-4);
    org.junit.Assert.assertEquals(-0.25, point[1], 1.0e-4);
}

@org.junit.Test
public void maximizationWithBoundsFindsInteriorPeak() {
    final org.apache.commons.math.optimization.direct.BOBYQAOptimizer optimizer =
        new org.apache.commons.math.optimization.direct.BOBYQAOptimizer(5);
    final org.apache.commons.math.optimization.RealPointValuePair result =
        optimizer.optimize(10000,
                           new org.apache.commons.math.analysis.MultivariateFunction() {
                               public double value(double[] point) {
                                   final double dx = point[0] - 0.4;
                                   final double dy = point[1] + 0.3;
                                   return 3.0 - dx * dx - dy * dy;
                               }
                           },
                           org.apache.commons.math.optimization.GoalType.MAXIMIZE,
                           new double[] { -1.0, 1.0 },
                           new double[] { -1.0, -1.0 },
                           new double[] { 1.0, 1.0 });

    final double[] point = result.getPoint();
    org.junit.Assert.assertEquals(3.0, result.getValue(), 1.0e-8);
    org.junit.Assert.assertEquals(0.4, point[0], 1.0e-4);
    org.junit.Assert.assertEquals(-0.3, point[1], 1.0e-4);
}
@Test
public void testCustomCoefficientsOptimizeShiftedQuadratic() throws Exception {
    final org.apache.commons.math.optimization.direct.MultiDirectional optimizer =
        new org.apache.commons.math.optimization.direct.MultiDirectional(3.0, 0.25);
    optimizer.setStartConfiguration(new double[] { 1.0 });

    final org.apache.commons.math.optimization.RealPointValuePair optimum =
        optimizer.optimize(new org.apache.commons.math.analysis.MultivariateRealFunction() {
            public double value(final double[] point) {
                final double difference = point[0] - 3.0;
                return difference * difference;
            }
        }, org.apache.commons.math.optimization.GoalType.MINIMIZE, new double[] { 0.0 });

    assertEquals(3.0, optimum.getPoint()[0], 1.0e-5);
    assertEquals(0.0, optimum.getValue(), 1.0e-9);
}
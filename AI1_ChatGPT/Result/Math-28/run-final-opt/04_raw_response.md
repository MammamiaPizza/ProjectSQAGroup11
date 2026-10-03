@Test
public void testPhaseOneTiedRatioRemovesArtificialVariableFromBasis() {
    final org.apache.commons.math3.optimization.linear.SimplexSolver solver =
            new org.apache.commons.math3.optimization.linear.SimplexSolver();
    final java.util.Collection<org.apache.commons.math3.optimization.linear.LinearConstraint> constraints =
            new java.util.ArrayList<org.apache.commons.math3.optimization.linear.LinearConstraint>();
    constraints.add(new org.apache.commons.math3.optimization.linear.LinearConstraint(
            new double[] { 1.0 },
            org.apache.commons.math3.optimization.linear.Relationship.EQ,
            1.0));
    constraints.add(new org.apache.commons.math3.optimization.linear.LinearConstraint(
            new double[] { 1.0 },
            org.apache.commons.math3.optimization.linear.Relationship.LEQ,
            1.0));

    final org.apache.commons.math3.optimization.PointValuePair solution = solver.optimize(
            new org.apache.commons.math3.optimization.linear.LinearObjectiveFunction(
                    new double[] { 1.0 }, 0.0),
            constraints,
            org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
            true);

    assertEquals(1.0, solution.getPoint()[0], 1.0e-8);
    assertEquals(1.0, solution.getValue(), 1.0e-8);
}
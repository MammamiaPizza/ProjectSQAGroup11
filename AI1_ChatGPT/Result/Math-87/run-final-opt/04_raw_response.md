@org.junit.Test
public void testNormalizedConstraintsFlipNegativeRightHandSide() {
    final org.apache.commons.math.optimization.linear.SimplexTableau tableau =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 0.0),
            java.util.Collections.singletonList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 2.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    -6.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            true,
            1.0e-6);

    final org.apache.commons.math.optimization.linear.LinearConstraint normalized =
        tableau.getNormalizedConstraints().get(0);
    org.junit.Assert.assertEquals(
        org.apache.commons.math.optimization.linear.Relationship.GEQ,
        normalized.getRelationship());
    org.junit.Assert.assertEquals(-2.0, normalized.getCoefficients().getEntry(0), 0.0);
    org.junit.Assert.assertEquals(6.0, normalized.getValue(), 0.0);
}

@org.junit.Test
public void testTableauTracksArtificialVariablesForGeqAndEqualityConstraints() {
    final org.apache.commons.math.optimization.linear.SimplexTableau tableau =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 0.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    10.0),
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.GEQ,
                    2.0),
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.EQ,
                    5.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            true,
            1.0e-6);

    org.junit.Assert.assertEquals(2, tableau.getNumObjectiveFunctions());
    org.junit.Assert.assertEquals(2, tableau.getNumSlackVariables());
    org.junit.Assert.assertEquals(2, tableau.getNumArtificialVariables());
    org.junit.Assert.assertEquals(8, tableau.getWidth());
    org.junit.Assert.assertEquals(5, tableau.getHeight());

    tableau.discardArtificialVariables();

    org.junit.Assert.assertEquals(0, tableau.getNumArtificialVariables());
    org.junit.Assert.assertEquals(6, tableau.getWidth());
    org.junit.Assert.assertEquals(4, tableau.getHeight());
}

@org.junit.Test
public void testEqualityConstraintOptimizationUsesArtificialVariablePhase() throws Exception {
    final org.apache.commons.math.optimization.RealPointValuePair solution =
        new org.apache.commons.math.optimization.linear.SimplexSolver().optimize(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 0.0),
            java.util.Collections.singletonList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.EQ,
                    7.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            true);

    org.junit.Assert.assertEquals(7.0, solution.getPoint()[0], 1.0e-6);
    org.junit.Assert.assertEquals(7.0, solution.getValue(), 1.0e-6);
}

@org.junit.Test
public void testUnrestrictedDecisionVariableOptimization() throws Exception {
    final org.apache.commons.math.optimization.RealPointValuePair solution =
        new org.apache.commons.math.optimization.linear.SimplexSolver().optimize(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 0.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    4.0),
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.GEQ,
                    -2.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            false);

    org.junit.Assert.assertEquals(4.0, solution.getPoint()[0], 1.0e-6);
    org.junit.Assert.assertEquals(4.0, solution.getValue(), 1.0e-6);
}
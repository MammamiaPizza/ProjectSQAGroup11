@Test
public void testNormalizeConstraintsFlipsNegativeRightHandSide() {
    org.apache.commons.math.optimization.linear.SimplexTableau tableau =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 0.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    1.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);

    java.util.List<org.apache.commons.math.optimization.linear.LinearConstraint> normalized =
        tableau.normalizeConstraints(java.util.Arrays.asList(
            new org.apache.commons.math.optimization.linear.LinearConstraint(
                new double[] { -2.0 },
                org.apache.commons.math.optimization.linear.Relationship.LEQ,
                -4.0)));

    org.apache.commons.math.optimization.linear.LinearConstraint constraint =
        normalized.get(0);
    org.junit.Assert.assertEquals(
        org.apache.commons.math.optimization.linear.Relationship.GEQ,
        constraint.getRelationship());
    org.junit.Assert.assertEquals(2.0, constraint.getCoefficients().getEntry(0), 0.0);
    org.junit.Assert.assertEquals(4.0, constraint.getValue(), 0.0);
}

@Test
public void testTableauEqualityAndHashCodeForEquivalentTableaux() {
    org.apache.commons.math.optimization.linear.SimplexTableau first =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 2.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    3.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);
    org.apache.commons.math.optimization.linear.SimplexTableau equivalent =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 2.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    3.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);
    org.apache.commons.math.optimization.linear.SimplexTableau unrestricted =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 2.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.LEQ,
                    3.0)),
            org.apache.commons.math.optimization.GoalType.MAXIMIZE,
            false, 1.0e-6, 10);

    org.junit.Assert.assertTrue(first.equals(first));
    org.junit.Assert.assertTrue(first.equals(equivalent));
    org.junit.Assert.assertEquals(first.hashCode(), equivalent.hashCode());
    org.junit.Assert.assertFalse(first.equals(unrestricted));
    org.junit.Assert.assertFalse(first.equals("not a tableau"));
}

@Test
public void testTableauSerializationPreservesEquality() throws Exception {
    org.apache.commons.math.optimization.linear.SimplexTableau tableau =
        new org.apache.commons.math.optimization.linear.SimplexTableau(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 2.0 }, 1.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.GEQ,
                    2.0)),
            org.apache.commons.math.optimization.GoalType.MINIMIZE,
            true, 1.0e-6, 10);

    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(tableau);
    output.close();

    java.io.ObjectInputStream input = new java.io.ObjectInputStream(
        new java.io.ByteArrayInputStream(bytes.toByteArray()));
    org.apache.commons.math.optimization.linear.SimplexTableau restored =
        (org.apache.commons.math.optimization.linear.SimplexTableau) input.readObject();
    input.close();

    org.junit.Assert.assertEquals(tableau, restored);
    org.junit.Assert.assertEquals(tableau.hashCode(), restored.hashCode());
}

@Test
public void testEqualityConstraintOptimizationUsesPhaseOneTableau() throws Exception {
    org.apache.commons.math.optimization.RealPointValuePair solution =
        new org.apache.commons.math.optimization.linear.SimplexSolver().optimize(
            new org.apache.commons.math.optimization.linear.LinearObjectiveFunction(
                new double[] { 1.0 }, 0.0),
            java.util.Arrays.asList(
                new org.apache.commons.math.optimization.linear.LinearConstraint(
                    new double[] { 1.0 },
                    org.apache.commons.math.optimization.linear.Relationship.EQ,
                    2.0)),
            org.apache.commons.math.optimization.GoalType.MINIMIZE,
            true);

    org.junit.Assert.assertEquals(2.0, solution.getPoint()[0], 1.0e-6);
    org.junit.Assert.assertEquals(2.0, solution.getValue(), 1.0e-6);
}
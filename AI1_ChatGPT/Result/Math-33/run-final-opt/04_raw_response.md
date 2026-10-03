@org.junit.Test
public void testDropPhaseOneObjectiveAlsoDropsPositiveArtificialColumn() {
    SimplexTableau tableau = new SimplexTableau(
            new org.apache.commons.math3.optimization.linear.LinearObjectiveFunction(
                    new double[] { 1.0 }, 0.0),
            java.util.Collections.singletonList(
                    new org.apache.commons.math3.optimization.linear.LinearConstraint(
                            new double[] { 1.0 },
                            org.apache.commons.math3.optimization.linear.Relationship.GEQ,
                            1.0)),
            org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);

    final int height = tableau.getHeight();
    final int width = tableau.getWidth();
    tableau.setEntry(0, tableau.getArtificialVariableOffset(), 1.0);

    tableau.dropPhase1Objective();

    org.junit.Assert.assertEquals(height - 1, tableau.getHeight());
    org.junit.Assert.assertEquals(width - 1, tableau.getWidth());
}

@org.junit.Test
public void testGetSolutionUsesZeroForUnconstrainedDecisionVariables() {
    SimplexTableau tableau = new SimplexTableau(
            new org.apache.commons.math3.optimization.linear.LinearObjectiveFunction(
                    new double[] { 2.0, -3.0 }, 7.0),
            java.util.Collections.<org.apache.commons.math3.optimization.linear.LinearConstraint>emptyList(),
            org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);

    org.apache.commons.math3.optimization.PointValuePair solution = tableau.getSolution();

    org.junit.Assert.assertArrayEquals(new double[] { 0.0, 0.0 }, solution.getPoint(), 0.0);
    org.junit.Assert.assertEquals(7.0, solution.getValue(), 0.0);
}

@org.junit.Test
public void testEqualsAndHashCodeForEquivalentTableaux() {
    SimplexTableau tableau = new SimplexTableau(
            new org.apache.commons.math3.optimization.linear.LinearObjectiveFunction(
                    new double[] { 1.0 }, 0.0),
            java.util.Collections.singletonList(
                    new org.apache.commons.math3.optimization.linear.LinearConstraint(
                            new double[] { 1.0 },
                            org.apache.commons.math3.optimization.linear.Relationship.LEQ,
                            2.0)),
            org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);
    SimplexTableau equivalent = new SimplexTableau(
            new org.apache.commons.math3.optimization.linear.LinearObjectiveFunction(
                    new double[] { 1.0 }, 0.0),
            java.util.Collections.singletonList(
                    new org.apache.commons.math3.optimization.linear.LinearConstraint(
                            new double[] { 1.0 },
                            org.apache.commons.math3.optimization.linear.Relationship.LEQ,
                            2.0)),
            org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);

    org.junit.Assert.assertTrue(tableau.equals(tableau));
    org.junit.Assert.assertTrue(tableau.equals(equivalent));
    org.junit.Assert.assertEquals(tableau.hashCode(), equivalent.hashCode());
    org.junit.Assert.assertFalse(tableau.equals("not a tableau"));
}

@org.junit.Test
public void testSerializationRestoresTableauState() throws java.io.IOException, ClassNotFoundException {
    SimplexTableau tableau = new SimplexTableau(
            new org.apache.commons.math3.optimization.linear.LinearObjectiveFunction(
                    new double[] { 1.0 }, 3.0),
            java.util.Collections.singletonList(
                    new org.apache.commons.math3.optimization.linear.LinearConstraint(
                            new double[] { 1.0 },
                            org.apache.commons.math3.optimization.linear.Relationship.LEQ,
                            2.0)),
            org.apache.commons.math3.optimization.GoalType.MAXIMIZE,
            true, 1.0e-6, 10);

    java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
    java.io.ObjectOutputStream output = new java.io.ObjectOutputStream(bytes);
    output.writeObject(tableau);
    output.close();

    java.io.ObjectInputStream input = new java.io.ObjectInputStream(
            new java.io.ByteArrayInputStream(bytes.toByteArray()));
    SimplexTableau restored = (SimplexTableau) input.readObject();
    input.close();

    org.junit.Assert.assertEquals(tableau, restored);
    org.junit.Assert.assertEquals(tableau.getHeight(), restored.getHeight());
    org.junit.Assert.assertEquals(tableau.getWidth(), restored.getWidth());
}
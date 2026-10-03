package org.apache.commons.math.optimization.linear;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SimplexTableauBasisExtractionRegressionTest {

    private static final double EPSILON = 1.0e-9;

    private SimplexTableau createTableau(double[] objective,
                                         Collection<LinearConstraint> constraints) {
        return new SimplexTableau(
                new LinearObjectiveFunction(objective, 0.0),
                constraints,
                GoalType.MAXIMIZE,
                true,
                EPSILON);
    }

    private void clear(SimplexTableau tableau) {
        for (int row = 0; row < tableau.getHeight(); row++) {
            for (int column = 0; column < tableau.getWidth(); column++) {
                tableau.setEntry(row, column, 0.0);
            }
        }
    }

    @Test
    public void testSolutionDoesNotTreatNegativeSingletonColumnAsBasicVariable() {
        SimplexTableau tableau = createTableau(
                new double[] { 1.0 },
                Arrays.asList(new LinearConstraint(
                        new double[] { 1.0 }, Relationship.LEQ, 2.0)));

        clear(tableau);

        int constraintRow = tableau.getNumObjectiveFunctions();
        int decisionColumn = tableau.getNumObjectiveFunctions();
        int slackColumn = tableau.getSlackVariableOffset();

        tableau.setEntry(constraintRow, decisionColumn, -1.0);
        tableau.setEntry(constraintRow, slackColumn, 1.0);
        tableau.setEntry(constraintRow, tableau.getRhsOffset(), 2.0);

        RealPointValuePair solution = tableau.getSolution();

        assertArrayEquals(new double[] { 0.0 }, solution.getPoint(), EPSILON);
        assertEquals(0.0, solution.getValue(), EPSILON);
    }

    @Test
    public void testSolutionExtractsIdentityBasicVariablesAndLeavesNonBasicVariableZero() {
        SimplexTableau tableau = createTableau(
                new double[] { 2.0, -1.0, 4.0 },
                Arrays.asList(
                        new LinearConstraint(new double[] { 1.0, 0.0, 0.0 },
                                             Relationship.LEQ, 2.0),
                        new LinearConstraint(new double[] { 0.0, 1.0, 0.0 },
                                             Relationship.LEQ, 3.0)));

        clear(tableau);

        int firstConstraintRow = tableau.getNumObjectiveFunctions();
        int secondConstraintRow = firstConstraintRow + 1;
        int firstDecisionColumn = tableau.getNumObjectiveFunctions();

        tableau.setEntry(firstConstraintRow, firstDecisionColumn, 1.0);
        tableau.setEntry(firstConstraintRow, tableau.getRhsOffset(), 2.0);
        tableau.setEntry(secondConstraintRow, firstDecisionColumn + 1, 1.0);
        tableau.setEntry(secondConstraintRow, tableau.getRhsOffset(), 3.0);

        RealPointValuePair solution = tableau.getSolution();

        assertArrayEquals(new double[] { 2.0, 3.0, 0.0 }, solution.getPoint(), EPSILON);
        assertEquals(1.0, solution.getValue(), EPSILON);
    }

    @Test
    public void testSolutionKeepsOnlyFirstVariableForDuplicateUnitBasicColumns() {
        SimplexTableau tableau = createTableau(
                new double[] { 1.0, 1.0 },
                Arrays.asList(new LinearConstraint(
                        new double[] { 1.0, 1.0 }, Relationship.LEQ, 4.0)));

        clear(tableau);

        int constraintRow = tableau.getNumObjectiveFunctions();
        int firstDecisionColumn = tableau.getNumObjectiveFunctions();

        tableau.setEntry(constraintRow, firstDecisionColumn, 1.0);
        tableau.setEntry(constraintRow, firstDecisionColumn + 1, 1.0);
        tableau.setEntry(constraintRow, tableau.getRhsOffset(), 4.0);

        RealPointValuePair solution = tableau.getSolution();

        assertArrayEquals(new double[] { 4.0, 0.0 }, solution.getPoint(), EPSILON);
        assertEquals(4.0, solution.getValue(), EPSILON);
    }

    @Test
    public void testNegativeRightHandSidesAreNormalizedWithOppositeRelationships() {
        SimplexTableau tableau = createTableau(
                new double[] { 1.0 },
                Arrays.asList(
                        new LinearConstraint(new double[] { 1.0 }, Relationship.LEQ, -2.0),
                        new LinearConstraint(new double[] { 2.0 }, Relationship.GEQ, -3.0),
                        new LinearConstraint(new double[] { 3.0 }, Relationship.EQ, -4.0)));

        List<LinearConstraint> normalized = tableau.getNormalizedConstraints();

        assertEquals(3, normalized.size());

        assertEquals(Relationship.GEQ, normalized.get(0).getRelationship());
        assertEquals(2.0, normalized.get(0).getValue(), EPSILON);
        assertArrayEquals(new double[] { -1.0 },
                          normalized.get(0).getCoefficients().getData(), EPSILON);

        assertEquals(Relationship.LEQ, normalized.get(1).getRelationship());
        assertEquals(3.0, normalized.get(1).getValue(), EPSILON);
        assertArrayEquals(new double[] { -2.0 },
                          normalized.get(1).getCoefficients().getData(), EPSILON);

        assertEquals(Relationship.EQ, normalized.get(2).getRelationship());
        assertEquals(4.0, normalized.get(2).getValue(), EPSILON);
        assertArrayEquals(new double[] { -3.0 },
                          normalized.get(2).getCoefficients().getData(), EPSILON);
    }
}
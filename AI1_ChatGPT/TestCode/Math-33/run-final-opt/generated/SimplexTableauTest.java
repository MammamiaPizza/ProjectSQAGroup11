package org.apache.commons.math3.optimization.linear;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.junit.Test;

public class SimplexTableauTest {

    private static final double EPS = 1e-9;

    @Test
    public void testNormalizesNegativeRightHandSideAndReversesRelationship() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0, 2.0 }, 0.0);
        LinearConstraint original =
                new LinearConstraint(new double[] { 3.0, -4.0 }, Relationship.LEQ, -5.0);

        SimplexTableau tableau = new SimplexTableau(
                objective, Arrays.asList(original), GoalType.MAXIMIZE, true, EPS);

        List<LinearConstraint> normalized = tableau.normalizeConstraints(Arrays.asList(original));
        assertEquals(1, normalized.size());
        assertEquals(Relationship.GEQ, normalized.get(0).getRelationship());
        assertEquals(5.0, normalized.get(0).getValue(), EPS);
        assertArrayEquals(new double[] { -3.0, 4.0 },
                          normalized.get(0).getCoefficients().toArray(), EPS);
    }

    @Test
    public void testMaximizationTableauContainsObjectiveSlackAndRhs() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 3.0 }, 2.0);
        LinearConstraint constraint =
                new LinearConstraint(new double[] { 4.0 }, Relationship.LEQ, 7.0);

        SimplexTableau tableau = new SimplexTableau(
                objective, Arrays.asList(constraint), GoalType.MAXIMIZE, true, EPS);

        assertEquals(2, tableau.getHeight());
        assertEquals(4, tableau.getWidth());
        assertEquals(1, tableau.getNumObjectiveFunctions());
        assertEquals(1, tableau.getNumDecisionVariables());
        assertEquals(1, tableau.getNumSlackVariables());
        assertEquals(0, tableau.getNumArtificialVariables());
        assertEquals(3.0, tableau.getEntry(0, 0), EPS);
        assertEquals(-3.0, tableau.getEntry(0, 1), EPS);
        assertEquals(2.0, tableau.getEntry(0, tableau.getRhsOffset()), EPS);
        assertEquals(4.0, tableau.getEntry(1, 1), EPS);
        assertEquals(1.0, tableau.getEntry(1, tableau.getSlackVariableOffset()), EPS);
        assertEquals(7.0, tableau.getEntry(1, tableau.getRhsOffset()), EPS);
    }

    @Test
    public void testGeqConstraintCreatesPhaseOneObjectiveAndArtificialVariable() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        LinearConstraint constraint =
                new LinearConstraint(new double[] { 2.0 }, Relationship.GEQ, 3.0);

        SimplexTableau tableau = new SimplexTableau(
                objective, Arrays.asList(constraint), GoalType.MINIMIZE, true, EPS);

        assertEquals(2, tableau.getNumObjectiveFunctions());
        assertEquals(1, tableau.getNumSlackVariables());
        assertEquals(1, tableau.getNumArtificialVariables());
        assertEquals(3, tableau.getHeight());
        assertEquals(6, tableau.getWidth());
        assertEquals(-1.0, tableau.getEntry(0, 0), EPS);
        assertEquals(-1.0, tableau.getEntry(1, 1), EPS);
        assertEquals(-1.0, tableau.getEntry(2, tableau.getSlackVariableOffset()), EPS);
        assertEquals(1.0, tableau.getEntry(2, tableau.getArtificialVariableOffset()), EPS);
        assertEquals(3.0, tableau.getEntry(2, tableau.getRhsOffset()), EPS);
    }

    @Test
    public void testBasicRowRecognizesUnitColumnsOnly() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        LinearConstraint constraint =
                new LinearConstraint(new double[] { 2.0 }, Relationship.LEQ, 3.0);

        SimplexTableau tableau = new SimplexTableau(
                objective, Arrays.asList(constraint), GoalType.MAXIMIZE, true, EPS);

        assertEquals(Integer.valueOf(1), tableau.getBasicRow(tableau.getSlackVariableOffset()));
        assertNull(tableau.getBasicRow(1));
        assertNull(tableau.getBasicRow(tableau.getRhsOffset()));
    }

    @Test
    public void testDropPhaseOneObjectiveRemovesPhaseOneRow() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        LinearConstraint constraint =
                new LinearConstraint(new double[] { 1.0 }, Relationship.EQ, 2.0);

        SimplexTableau tableau = new SimplexTableau(
                objective, Arrays.asList(constraint), GoalType.MAXIMIZE, true, EPS);

        assertEquals(2, tableau.getNumObjectiveFunctions());
        tableau.dropPhase1Objective();

        assertEquals(1, tableau.getNumObjectiveFunctions());
        assertEquals(0, tableau.getNumArtificialVariables());
        assertEquals(2, tableau.getHeight());
        assertEquals(4, tableau.getWidth());
    }

    @Test
    public void testSolutionExtractionDoesNotAssignSameBasicRowToTwoVariables() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 0.0, 0.0 }, 0.0);
        LinearConstraint constraint =
                new LinearConstraint(new double[] { 1.0, 1.0 }, Relationship.EQ, 1.0);

        SimplexTableau tableau = new SimplexTableau(
                objective, Arrays.asList(constraint), GoalType.MAXIMIZE, true, EPS);
        tableau.dropPhase1Objective();

        PointValuePair solution = tableau.getSolution();

        assertArrayEquals(new double[] { 1.0, 0.0 }, solution.getPoint(), EPS);
        assertEquals(0.0, solution.getValue(), EPS);
        assertEquals(1.0, solution.getPoint()[0] + solution.getPoint()[1], EPS);
    }

    @Test
    public void testSolverHandlesMixedEqualityAndInequalityConstraints() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 3.0, 2.0 }, 0.0);

        PointValuePair solution = new SimplexSolver().optimize(
                objective,
                Arrays.asList(
                        new LinearConstraint(new double[] { 1.0, 1.0 }, Relationship.EQ, 4.0),
                        new LinearConstraint(new double[] { 1.0, 0.0 }, Relationship.GEQ, 1.0),
                        new LinearConstraint(new double[] { 0.0, 1.0 }, Relationship.GEQ, 1.0)),
                GoalType.MAXIMIZE,
                true);

        assertNotNull(solution);
        assertArrayEquals(new double[] { 3.0, 1.0 }, solution.getPoint(), EPS);
        assertEquals(11.0, solution.getValue(), EPS);
    }

    @Test
    public void testSolverReturnsNegativeValueForUnrestrictedVariable() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);

        PointValuePair solution = new SimplexSolver().optimize(
                objective,
                Arrays.asList(
                        new LinearConstraint(new double[] { 1.0 }, Relationship.GEQ, -2.0),
                        new LinearConstraint(new double[] { 1.0 }, Relationship.LEQ, 5.0)),
                GoalType.MINIMIZE,
                false);

        assertNotNull(solution);
        assertEquals(-2.0, solution.getPoint()[0], EPS);
        assertEquals(-2.0, solution.getValue(), EPS);
        assertTrue(solution.getPoint()[0] >= -2.0 - EPS);
        assertTrue(solution.getPoint()[0] <= 5.0 + EPS);
        assertFalse(solution.getPoint()[0] >= 0.0);
    }
}

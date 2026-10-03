package org.apache.commons.math.optimization.linear;

import java.util.Collections;
import java.util.List;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimplexTableauMath286Test {

    private static final double EPSILON = 1.0e-9;

    @Test
    public void testGetSolutionIgnoresObjectiveRowWhenFindingBasicDecisionVariable()
        throws Exception {
        LinearObjectiveFunction objective =
            new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        LinearConstraint constraint =
            new LinearConstraint(new double[] { 1.0 }, Relationship.LEQ, 6.9);

        SimplexTableau tableau = new SimplexTableau(
            objective,
            Collections.singletonList(constraint),
            GoalType.MINIMIZE,
            true,
            EPSILON);

        clear(tableau);

        tableau.setEntry(0, 0, -1.0);
        tableau.setEntry(0, 1, 1.0);
        tableau.setEntry(1, 1, 1.0);
        tableau.setEntry(1, tableau.getRhsOffset(), 6.9);

        RealPointValuePair solution = tableau.getSolution();

        assertEquals(6.9, solution.getPoint()[0], EPSILON);
        assertEquals(6.9, solution.getValue(), EPSILON);
    }

    @Test
    public void testSimplexSolverFindsObjectiveValueSixPointNine()
        throws Exception {
        LinearObjectiveFunction objective =
            new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        LinearConstraint constraint =
            new LinearConstraint(new double[] { 1.0 }, Relationship.LEQ, 6.9);

        RealPointValuePair solution = new SimplexSolver().optimize(
            objective,
            Collections.singletonList(constraint),
            GoalType.MAXIMIZE,
            true);

        assertEquals(6.9, solution.getValue(), EPSILON);
        assertEquals(6.9, solution.getPoint()[0], EPSILON);
    }

    @Test
    public void testNegativeRightHandSideIsNormalizedWithOppositeRelationship() {
        LinearObjectiveFunction objective =
            new LinearObjectiveFunction(new double[] { 0.0 }, 0.0);
        LinearConstraint constraint =
            new LinearConstraint(new double[] { 2.0 }, Relationship.LEQ, -3.0);

        SimplexTableau tableau = new SimplexTableau(
            objective,
            Collections.singletonList(constraint),
            GoalType.MINIMIZE,
            true,
            EPSILON);

        List<LinearConstraint> normalized = tableau.getNormalizedConstraints();

        assertEquals(1, normalized.size());
        assertEquals(Relationship.GEQ, normalized.get(0).getRelationship());
        assertEquals(-2.0, normalized.get(0).getCoefficients().getEntry(0), EPSILON);
        assertEquals(3.0, normalized.get(0).getValue(), EPSILON);
    }

    private void clear(SimplexTableau tableau) {
        for (int row = 0; row < tableau.getHeight(); row++) {
            for (int column = 0; column < tableau.getWidth(); column++) {
                tableau.setEntry(row, column, 0.0);
            }
        }
    }
}

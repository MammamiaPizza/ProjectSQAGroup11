package org.apache.commons.math.optimization.linear;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

public class SimplexTableauMath713Test {

    @Test
    public void testMath713NegativeVariableOptimization() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        Collection<LinearConstraint> constraints =
                new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.LEQ, -1.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, false);

        assertEquals(-1.0, solution.getPoint()[0], 1.0e-6);
        assertEquals(-1.0, solution.getValue(), 1.0e-6);
    }

    @Test
    public void testUnrestrictedSolutionUsesNegativeVariableBasicRowShift() {
        SimplexTableau tableau = createUnrestrictedSingleVariableTableau();
        clear(tableau);

        int negativeColumn = tableau.getSlackVariableOffset() - 1;
        int rhs = tableau.getRhsOffset();

        tableau.setEntry(1, negativeColumn, 1.0);
        tableau.setEntry(1, rhs, 3.0);

        RealPointValuePair solution = tableau.getSolution();

        assertEquals(-3.0, solution.getPoint()[0], 0.0);
        assertEquals(-3.0, solution.getValue(), 0.0);
    }

    @Test
    public void testUnrestrictedSolutionDoesNotUseObjectiveRowAsNegativeVariableValue() {
        SimplexTableau tableau = createUnrestrictedSingleVariableTableau();
        clear(tableau);

        int decisionColumn = tableau.getNumObjectiveFunctions();
        int negativeColumn = tableau.getSlackVariableOffset() - 1;
        int rhs = tableau.getRhsOffset();

        tableau.setEntry(0, negativeColumn, 1.0);
        tableau.setEntry(0, rhs, 100.0);
        tableau.setEntry(1, decisionColumn, 1.0);
        tableau.setEntry(1, rhs, 4.0);

        RealPointValuePair solution = tableau.getSolution();

        assertEquals(4.0, solution.getPoint()[0], 0.0);
        assertEquals(4.0, solution.getValue(), 0.0);
    }

    @Test
    public void testNonNegativeOptimizationReturnsBasicDecisionVariableValue() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 2.0 }, 1.0);
        Collection<LinearConstraint> constraints =
                new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.LEQ, 4.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(4.0, solution.getPoint()[0], 1.0e-6);
        assertEquals(9.0, solution.getValue(), 1.0e-6);
    }

    private SimplexTableau createUnrestrictedSingleVariableTableau() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        Collection<LinearConstraint> constraints =
                new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.LEQ, 0.0));
        return new SimplexTableau(
                objective, constraints, GoalType.MAXIMIZE, false, 1.0e-6);
    }

    private void clear(SimplexTableau tableau) {
        for (int row = 0; row < tableau.getHeight(); row++) {
            for (int column = 0; column < tableau.getWidth(); column++) {
                tableau.setEntry(row, column, 0.0);
            }
        }
    }
}
package org.apache.commons.math.optimization.linear;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimplexTableauTest {

    private static final double EPSILON = 1.0e-6;

    @Test
    public void testSingleVariableAndConstraintOptimizationReturnsUpperBound() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.LEQ, 10.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(10.0, solution.getPoint()[0], EPSILON);
        assertEquals(10.0, solution.getValue(), EPSILON);
    }

    @Test
    public void testGetSolutionReturnsRhsForUnitBasicDecisionVariable() {
        SimplexTableau tableau = createSingleVariableTableau();
        clear(tableau);

        int row = tableau.getNumObjectiveFunctions();
        int variableColumn = tableau.getNumObjectiveFunctions();
        tableau.setEntry(row, variableColumn, 1.0);
        tableau.setEntry(row, tableau.getRhsOffset(), 10.0);

        RealPointValuePair solution = tableau.getSolution();

        assertEquals(10.0, solution.getPoint()[0], EPSILON);
        assertEquals(10.0, solution.getValue(), EPSILON);
    }

    @Test
    public void testGetSolutionDoesNotTreatNonUnitColumnAsBasicVariable() {
        SimplexTableau tableau = createSingleVariableTableau();
        clear(tableau);

        int row = tableau.getNumObjectiveFunctions();
        int variableColumn = tableau.getNumObjectiveFunctions();
        tableau.setEntry(row, variableColumn, -1.0);
        tableau.setEntry(row, tableau.getSlackVariableOffset(), 1.0);
        tableau.setEntry(row, tableau.getRhsOffset(), 10.0);

        RealPointValuePair solution = tableau.getSolution();

        assertEquals(0.0, solution.getPoint()[0], EPSILON);
        assertEquals(0.0, solution.getValue(), EPSILON);
    }

    @Test
    public void testGetSolutionReturnsZeroForDecisionVariableWithoutBasicRow() {
        SimplexTableau tableau = createSingleVariableTableau();
        clear(tableau);

        int row = tableau.getNumObjectiveFunctions();
        tableau.setEntry(row, tableau.getSlackVariableOffset(), 1.0);
        tableau.setEntry(row, tableau.getRhsOffset(), 10.0);

        RealPointValuePair solution = tableau.getSolution();

        assertEquals(0.0, solution.getPoint()[0], EPSILON);
        assertEquals(0.0, solution.getValue(), EPSILON);
    }

    private SimplexTableau createSingleVariableTableau() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.LEQ, 10.0));
        return new SimplexTableau(
                objective, constraints, GoalType.MAXIMIZE, true, EPSILON);
    }

    private void clear(SimplexTableau tableau) {
        for (int row = 0; row < tableau.getHeight(); row++) {
            for (int column = 0; column < tableau.getWidth(); column++) {
                tableau.setEntry(row, column, 0.0);
            }
        }
    }
}
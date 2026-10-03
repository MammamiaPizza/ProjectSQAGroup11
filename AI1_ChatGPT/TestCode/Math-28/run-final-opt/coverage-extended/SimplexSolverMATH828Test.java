package org.apache.commons.math3.optimization.linear;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SimplexSolverMATH828Test {

    @Test
    public void testMath828CycleTerminatesWithOptimalSolution() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 10, -57, -9, -24 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 0.5, -5.5, -2.5, 9 }, Relationship.LEQ, 0));
        constraints.add(new LinearConstraint(
                new double[] { 0.5, -1.5, -0.5, 1 }, Relationship.LEQ, 0));
        constraints.add(new LinearConstraint(
                new double[] { 1, 0, 0, 0 }, Relationship.LEQ, 1));

        SimplexSolver solver = new SimplexSolver();
        solver.setMaxIterations(100);

        try {
            solver.optimize(objective, constraints, GoalType.MAXIMIZE, true);
            fail("Expected the cycling problem to exceed the iteration limit");
        } catch (MaxCountExceededException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testMaximizationFindsUniqueCornerSolution() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 3, 2 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1, 1 }, Relationship.LEQ, 4));
        constraints.add(new LinearConstraint(
                new double[] { 1, 0 }, Relationship.LEQ, 2));
        constraints.add(new LinearConstraint(
                new double[] { 0, 1 }, Relationship.LEQ, 3));

        PointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(10.0, solution.getValue(), 1.0e-8);
        assertEquals(2.0, solution.getPoint()[0], 1.0e-8);
        assertEquals(2.0, solution.getPoint()[1], 1.0e-8);
    }

    @Test
    public void testMinimizationWithEqualityConstraintUsesFeasiblePhase() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1, 1 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1, 2 }, Relationship.EQ, 4));

        PointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MINIMIZE, true);

        assertEquals(2.0, solution.getValue(), 1.0e-8);
        assertEquals(0.0, solution.getPoint()[0], 1.0e-8);
        assertEquals(2.0, solution.getPoint()[1], 1.0e-8);
    }

    @Test
    public void testDegenerateTiedRatioProblemReturnsFeasibleOptimum() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1, 1 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1, 0 }, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(
                new double[] { 0, 1 }, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(
                new double[] { 1, 1 }, Relationship.LEQ, 1));

        PointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(1.0, solution.getValue(), 1.0e-8);
        assertTrue(solution.getPoint()[0] >= -1.0e-8);
        assertTrue(solution.getPoint()[1] >= -1.0e-8);
        assertTrue(solution.getPoint()[0] <= 1.0 + 1.0e-8);
        assertTrue(solution.getPoint()[1] <= 1.0 + 1.0e-8);
        assertEquals(1.0, solution.getPoint()[0] + solution.getPoint()[1], 1.0e-8);
    }

    @Test
    public void testUnrestrictedVariableCanTakeNegativeOptimalValue() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1 }, Relationship.GEQ, -3));
        constraints.add(new LinearConstraint(
                new double[] { 1 }, Relationship.LEQ, 2));

        PointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MINIMIZE, false);

        assertEquals(-3.0, solution.getValue(), 1.0e-8);
        assertEquals(-3.0, solution.getPoint()[0], 1.0e-8);
    }

    @Test
    public void testUnboundedProblemThrowsUnboundedSolutionException() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1 }, Relationship.GEQ, 0));

        try {
            new SimplexSolver().optimize(
                    objective, constraints, GoalType.MAXIMIZE, true);
            fail("Expected an unbounded solution exception");
        } catch (UnboundedSolutionException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testInfeasibleProblemThrowsNoFeasibleSolutionException() {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1 }, Relationship.LEQ, 1));
        constraints.add(new LinearConstraint(
                new double[] { 1 }, Relationship.GEQ, 2));

        try {
            new SimplexSolver().optimize(
                    objective, constraints, GoalType.MAXIMIZE, true);
            fail("Expected a no feasible solution exception");
        } catch (NoFeasibleSolutionException expected) {
            assertNotNull(expected);
        }
    }

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
}

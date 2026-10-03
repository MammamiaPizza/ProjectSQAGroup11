package org.apache.commons.math.optimization.linear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimplexSolverGeneratedTest {

    private static final double EPS = 1.0e-7;

    @Test
    public void testMath288PhaseOneThenOptimizationFindsValueTen() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 3.0, 2.0 }, 0.0);
        Collection<LinearConstraint> constraints = Arrays.asList(
                new LinearConstraint(new double[] { 1.0, 1.0 }, Relationship.GEQ, 2.0),
                new LinearConstraint(new double[] { 1.0, 2.0 }, Relationship.LEQ, 6.0),
                new LinearConstraint(new double[] { 2.0, 1.0 }, Relationship.LEQ, 6.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(10.0, solution.getValue(), EPS);
        assertEquals(2.0, solution.getPoint()[0], EPS);
        assertEquals(2.0, solution.getPoint()[1], EPS);
    }

    @Test
    public void testMaximumWithTiedMinimumRatioRows() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0, 1.0 }, 0.0);
        Collection<LinearConstraint> constraints = Arrays.asList(
                new LinearConstraint(new double[] { 1.0, 0.0 }, Relationship.LEQ, 2.0),
                new LinearConstraint(new double[] { 1.0, 1.0 }, Relationship.LEQ, 2.0),
                new LinearConstraint(new double[] { 0.0, 1.0 }, Relationship.LEQ, 2.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(2.0, solution.getValue(), EPS);
        assertEquals(2.0, solution.getPoint()[0] + solution.getPoint()[1], EPS);
    }

    @Test
    public void testEqualityConstraintIsHandledThroughPhaseOne() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 3.0, 1.0 }, 0.0);
        Collection<LinearConstraint> constraints = Arrays.asList(
                new LinearConstraint(new double[] { 1.0, 1.0 }, Relationship.EQ, 4.0),
                new LinearConstraint(new double[] { 1.0, 0.0 }, Relationship.LEQ, 3.0),
                new LinearConstraint(new double[] { 0.0, 1.0 }, Relationship.LEQ, 3.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(10.0, solution.getValue(), EPS);
        assertEquals(3.0, solution.getPoint()[0], EPS);
        assertEquals(1.0, solution.getPoint()[1], EPS);
    }

    @Test
    public void testMinimizationWithGreaterThanConstraint() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0, 1.0 }, 0.0);
        Collection<LinearConstraint> constraints = Arrays.asList(
                new LinearConstraint(new double[] { 1.0, 1.0 }, Relationship.GEQ, 4.0),
                new LinearConstraint(new double[] { 1.0, 0.0 }, Relationship.LEQ, 5.0),
                new LinearConstraint(new double[] { 0.0, 1.0 }, Relationship.LEQ, 5.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MINIMIZE, true);

        assertEquals(4.0, solution.getValue(), EPS);
        assertEquals(4.0, solution.getPoint()[0] + solution.getPoint()[1], EPS);
    }

    @Test
    public void testObjectiveConstantIsIncludedInReturnedValue() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 2.0, 1.0 }, 5.0);
        Collection<LinearConstraint> constraints = Arrays.asList(
                new LinearConstraint(new double[] { 1.0, 0.0 }, Relationship.LEQ, 2.0),
                new LinearConstraint(new double[] { 0.0, 1.0 }, Relationship.LEQ, 3.0));

        RealPointValuePair solution = new SimplexSolver().optimize(
                objective, constraints, GoalType.MAXIMIZE, true);

        assertEquals(12.0, solution.getValue(), EPS);
        assertEquals(2.0, solution.getPoint()[0], EPS);
        assertEquals(3.0, solution.getPoint()[1], EPS);
    }

    @Test(expected = NoFeasibleSolutionException.class)
    public void testInconsistentConstraintsAreReportedAsInfeasible() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);
        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.GEQ, 2.0));
        constraints.add(new LinearConstraint(
                new double[] { 1.0 }, Relationship.LEQ, 1.0));

        new SimplexSolver().optimize(objective, constraints, GoalType.MAXIMIZE, true);
    }

    @Test(expected = UnboundedSolutionException.class)
    public void testUnboundedMaximumIsReported() throws Exception {
        LinearObjectiveFunction objective =
                new LinearObjectiveFunction(new double[] { 1.0 }, 0.0);

        new SimplexSolver().optimize(
                objective,
                Collections.<LinearConstraint>emptyList(),
                GoalType.MAXIMIZE,
                true);
    }
}
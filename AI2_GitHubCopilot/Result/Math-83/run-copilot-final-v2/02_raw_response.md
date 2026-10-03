package org.apache.commons.math.optimization.linear;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimplexTableauTest {

 private static final double EPSILON = 1e-10;

 /* Reproduces the MATH-286 bug: expected optimal value 6.9, buggy returns 4.6 */
 @Test
 public void testMath286Bug() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{0.8, 0.2, 0.7, 0.3, 0.6, 0.4}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1,0,1,0,1,0}, Relationship.EQ, 23.0));
     constraints.add(new LinearConstraint(new double[]{0,1,0,1,0,1}, Relationship.EQ, 23.0));
     constraints.add(new LinearConstraint(new double[]{1,0,0,0,0,0}, Relationship.GEQ, 10.0));
     constraints.add(new LinearConstraint(new double[]{0,0,1,0,0,0}, Relationship.GEQ, 8.0));
     constraints.add(new LinearConstraint(new double[]{0,0,0,0,1,0}, Relationship.GEQ, 5.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(6.9, solution.getValue(), EPSILON);
 }

 /* Basic feasible LP with only LEQ constraints, maximize */
 @Test
 public void testMaximizeSimple() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{1, 2}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.LEQ, 5));
     constraints.add(new LinearConstraint(new double[]{0, 1}, Relationship.LEQ, 3));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(11.0, solution.getValue(), EPSILON);
     assertArrayEquals(new double[]{5, 3}, solution.getPoint(), EPSILON);
 }

 /* Minimize LP with equality constraint */
 @Test
 public void testMinimizeWithEquality() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{1, 2}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.EQ, 10));
     constraints.add(new LinearConstraint(new double[]{1, -1}, Relationship.LEQ, 4));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
     // min 1*x1+2*x2  s.t. x1+x2=10, x1-x2<=4, x1,x2>=0  -> x1=7,x2=3, obj=13
     assertEquals(13.0, solution.getValue(), EPSILON);
 }

 /* LP with mix of LEQ, GEQ, EQ constraints, maximize */
 @Test
 public void testMixedConstraintsMaximize() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{3, 4}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 2}, Relationship.LEQ, 8));
     constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.GEQ, 2));
     constraints.add(new LinearConstraint(new double[]{0, 1}, Relationship.GEQ, 1));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     // Optimum at x1=6,x2=1, obj=3*6+4*1=22
     assertEquals(22.0, solution.getValue(), EPSILON);
 }

 /* Infeasible LP */
 @Test(expected = NoFeasibleSolutionException.class)
 public void testInfeasibleLP() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{1, 0}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.LEQ, 1));
     constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.GEQ, 2));
     SimplexSolver solver = new SimplexSolver();
     solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
 }

 /* Unbounded LP */
 @Test(expected = UnboundedSolutionException.class)
 public void testUnboundedLP() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{1, 0}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, -1}, Relationship.LEQ, 0));
     SimplexSolver solver = new SimplexSolver();
     solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
 }

 /* LP with large coefficients – verifies numeric stability */
 @Test
 public void testLargeCoefficients() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{1e10, 2e10}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{3e10, 4e10}, Relationship.LEQ, 7e10));
     constraints.add(new LinearConstraint(new double[]{1e10, 0}, Relationship.EQ, 1e10));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertTrue(solution.getValue() > 0);
 }

 /* Check tableau dimensions after construction */
 @Test
 public void testTableauDimensions() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{2, 3}, 5);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.LEQ, 10));
     constraints.add(new LinearConstraint(new double[]{4, 6}, Relationship.LEQ, 24));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true, EPSILON);
     assertEquals(2, tableau.getNumDecisionVariables());
     assertEquals(2, tableau.getNumSlackVariables());
     assertEquals(0, tableau.getNumArtificialVariables());
     assertEquals(3, tableau.getHeight()); // 2 constraints + 1 objective
     assertEquals(2 + 2 + 1 + 1, tableau.getWidth());
 }

 /* Divide a row and verify the RHS entry changes correctly */
 @Test
 public void testDivideRow() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{1}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{2}, Relationship.LEQ, 6));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true, EPSILON);
     int rhsOffset = tableau.getRhsOffset();
     double originalRHS = tableau.getEntry(1, rhsOffset);
     tableau.divideRow(1, 2.0);
     assertEquals(originalRHS / 2.0, tableau.getEntry(1, rhsOffset), EPSILON);
 }

 /* After solving a simple LP, the returned point satisfies constraints */
 @Test
 public void testSolutionSatisfiesConstraints() {
     LinearObjectiveFunction f =
         new LinearObjectiveFunction(new double[]{2, 3}, 0);
     List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.LEQ, 5));
     constraints.add(new LinearConstraint(new double[]{2, 1}, Relationship.LEQ, 8));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     double[] point = solution.getPoint();
     for (final LinearConstraint c : constraints) {
         double lhs = 0;
         for (int i = 0; i < point.length; i++) {
             lhs += c.getCoefficients().getEntry(i) * point[i];
         }
         if (c.getRelationship() == Relationship.LEQ) {
             assertTrue(lhs <= c.getValue() + EPSILON);
         } else if (c.getRelationship() == Relationship.GEQ) {
             assertTrue(lhs >= c.getValue() - EPSILON);
         } else {
             assertEquals(c.getValue(), lhs, EPSILON);
         }
     }
 }

}
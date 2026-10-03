package org.apache.commons.math.optimization.linear;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.optimization.GoalType;
import org.junit.Test;

public class SimplexTableauTest {

 private static final double EPSILON = 1e-8;

 @Test
 public void testTableauAlignmentWithArtificialVars() {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{2.0, 3.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 1.0}),
             Relationship.EQ, 5.0));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true, 1e-6);
     double[][] data = tableau.getData();
     int objRows = tableau.getNumObjectiveFunctions();
     assertEquals(2, objRows);
     int row = objRows; // first constraint row
     int firstDecisionCol = objRows; // after objective function columns
     // decision variable coefficients must appear in the correct column block
     assertEquals(1.0, data[row][firstDecisionCol], EPSILON);
     assertEquals(1.0, data[row][firstDecisionCol + 1], EPSILON);
     // ensure they are NOT mistakenly placed at column 1 (the objective cell)
     assertEquals(0.0, data[row][1], EPSILON);
 }

 @Test
 public void testTableauAlignmentWithoutArtificialVars() {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0, 1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 0.0}),
             Relationship.LEQ, 2.0));
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{0.0, 1.0}),
             Relationship.LEQ, 3.0));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true, 1e-6);
     double[][] data = tableau.getData();
     int objRows = tableau.getNumObjectiveFunctions();
     assertEquals(1, objRows);
     // constraint rows decision variables start at col 1 (the only objective function col is 0)
     int row = objRows; // first constraint row
     assertEquals(1.0, data[row][1], EPSILON); // x1 coefficient
     assertEquals(0.0, data[row][2], EPSILON); // x2 coefficient
 }

 @Test
 public void testNormalizeNegativeRHS() {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0}),
             Relationship.LEQ, -2.0));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true, 1e-6);
     List<LinearConstraint> normalized = tableau.getNormalizedConstraints();
     assertEquals(1, normalized.size());
     LinearConstraint nc = normalized.get(0);
     assertEquals(Relationship.GEQ, nc.getRelationship());
     assertEquals(2.0, nc.getValue(), EPSILON);
     assertEquals(-1.0, nc.getCoefficients().getEntry(0), EPSILON);
 }

 @Test
 public void testGetBasicRowForSlackVariable() {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0}),
             Relationship.LEQ, 5.0));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true, 1e-6);
     int slackCol = tableau.getSlackVariableOffset(); // after decision vars
     Integer basicRow = tableau.getBasicRow(slackCol);
     assertNotNull(basicRow);
     // the slack variable is basic, so the entry should be 1
     assertEquals(1.0, tableau.getEntry(basicRow, slackCol), EPSILON);
 }

 @Test
 public void testGetSolutionMaximizeWithEquality() throws Exception {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0, 0.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 1.0}),
             Relationship.EQ, 1.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(1.0, solution.getValue(), 1e-6);
     double[] point = solution.getPoint();
     assertEquals(2, point.length);
     assertTrue(Math.abs(point[0] + point[1] - 1.0) < 1e-6);
 }

 @Test
 public void testGetSolutionMinimizeWithGEQ() throws Exception {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0}),
             Relationship.GEQ, 2.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true);
     assertEquals(2.0, solution.getValue(), 1e-6);
     assertEquals(2.0, solution.getPoint()[0], 1e-6);
 }

 @Test
 public void testGetSolutionSingleLessThanConstraint() throws Exception {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0}),
             Relationship.LEQ, 3.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(3.0, solution.getValue(), 1e-6);
     assertEquals(3.0, solution.getPoint()[0], 1e-6);
 }

 @Test
 public void testGetSolutionRedundantConstraints() throws Exception {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{2.0, 3.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 0.0}),
             Relationship.LEQ, 4.0));
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 0.0}),
             Relationship.LEQ, 4.0));
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{0.0, 1.0}),
             Relationship.LEQ, 5.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(23.0, solution.getValue(), 1e-6); // 2*4 + 3*5 = 23
 }

 @Test
 public void testGetSolutionZeroRHS() throws Exception {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0, 1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 1.0}),
             Relationship.EQ, 0.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(0.0, solution.getValue(), 1e-6);
     assertEquals(0.0, solution.getPoint()[0], 1e-6);
     assertEquals(0.0, solution.getPoint()[1], 1e-6);
 }

 @Test
 public void testDiscardArtificialVariables() {
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0, 1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 0.0}),
             Relationship.GEQ, 2.0));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MINIMIZE, true, 1e-6);
     int origWidth = tableau.getWidth();
     int origHeight = tableau.getHeight();
     int origArtificial = tableau.getNumArtificialVariables();
     assertTrue(origArtificial > 0);
     tableau.discardArtificialVariables();
     assertEquals(origArtificial, 0);
     assertEquals(origHeight - 1, tableau.getHeight());
     assertEquals(origWidth - origArtificial - 1, tableau.getWidth());
 }

 @Test
 public void testMath272() throws Exception {
     // reproduce MATH-272: expected objective value 1.0, not 0.0
     // maximize x1 subject to x1 + x2 = 1, x1,x2 >= 0  (artificial variables required)
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0, 0.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 1.0}),
             Relationship.EQ, 1.0));
     SimplexSolver solver = new SimplexSolver();
     RealPointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true);
     assertEquals(1.0, solution.getValue(), 1e-6);
 }

 @Test
 public void testTableauColumnOffsets() {
     // verify that slack and artificial offsets are computed correctly
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1.0, 1.0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{1.0, 0.0}),
             Relationship.LEQ, 2.0));
     constraints.add(new LinearConstraint(new ArrayRealVector(new double[]{0.0, 1.0}),
             Relationship.GEQ, 3.0));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MINIMIZE, true, 1e-6);
     int objRows = tableau.getNumObjectiveFunctions();
     int numDecision = tableau.getNumDecisionVariables();
     int numSlack = tableau.getNumSlackVariables();
     int numArtificial = tableau.getNumArtificialVariables();
     // slack offset = objRows + numDecision
     assertEquals(objRows + numDecision, tableau.getSlackVariableOffset());
     // artificial offset = objRows + numDecision + numSlack
     assertEquals(objRows + numDecision + numSlack, tableau.getArtificialVariableOffset());
     // RHS offset = width - 1
     assertEquals(tableau.getWidth() - 1, tableau.getRhsOffset());
 }

}
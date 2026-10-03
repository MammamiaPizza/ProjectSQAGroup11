package org.apache.commons.math.optimization.linear;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.assertFalse;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;

 import org.apache.commons.math.linear.ArrayRealVector;
 import org.apache.commons.math.linear.RealVector;
 import org.apache.commons.math.optimization.GoalType;
 import org.apache.commons.math.optimization.RealPointValuePair;
 import org.junit.Test;

 public class SimplexTableauTest {

     /** Helper to expose protected members for testing. */
     private static class TestableSimplexTableau extends SimplexTableau {
         private static final long serialVersionUID = 1L;

         public TestableSimplexTableau(LinearObjectiveFunction f,
                                       Collection<LinearConstraint> constraints,
                                       GoalType goalType,
                                       boolean restrictToNonNegative,
                                       double epsilon) {
             super(f, constraints, goalType, restrictToNonNegative, epsilon);
         }

         @Override
         public double[][] getData() {
             return super.getData();
         }

         @Override
         public int getWidth() {
             return super.getWidth();
         }

         @Override
         public int getHeight() {
             return super.getHeight();
         }

         @Override
         public double getEntry(int row, int col) {
             return super.getEntry(row, col);
         }

         @Override
         public int getNumObjectiveFunctions() {
             return super.getNumObjectiveFunctions();
         }

         @Override
         public int getNumDecisionVariables() {
             return super.getNumDecisionVariables();
         }

         @Override
         public int getNumSlackVariables() {
             return super.getNumSlackVariables();
         }

         @Override
         public int getNumArtificialVariables() {
             return super.getNumArtificialVariables();
         }

         @Override
         public int getSlackVariableOffset() {
             return super.getSlackVariableOffset();
         }

         @Override
         public int getArtificialVariableOffset() {
             return super.getArtificialVariableOffset();
         }

         @Override
         public int getRhsOffset() {
             return super.getRhsOffset();
         }

         @Override
         public double[][] createTableau(final boolean maximize) {
             return super.createTableau(maximize);
         }

         @Override
         public Integer getBasicRow(int col) {
             return super.getBasicRow(col);
         }

         @Override
         public RealPointValuePair getSolution() {
             return super.getSolution();
         }

         @Override
         public void discardArtificialVariables() {
             super.discardArtificialVariables();
         }
     }

     // -------- MATH-273 reproduction and solution tests --------

     @Test
     public void testGetSolutionSingleVariableAndConstraint() {
         // exactly the Math-273 failing scenario
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0 }, 0.0);
         LinearConstraint c = new LinearConstraint(
             new double[] { 1.0 }, Relationship.LEQ, 10.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(c);

         SimplexSolver solver = new SimplexSolver();
         RealPointValuePair solution = solver.optimize(
             f, constraints, GoalType.MAXIMIZE, true);

         assertEquals(10.0, solution.getPoint()[0], 1e-12);
         assertEquals(10.0, solution.getValue(), 1e-12);
     }

     @Test
     public void testGetSolutionMaximizationWithSlack() {
         // max 3x + 4y  s.t.  x + y <= 10, 2x + y <= 20
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 3.0, 4.0 }, 0.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1.0, 1.0 },
                 Relationship.LEQ, 10.0));
         constraints.add(new LinearConstraint(new double[] { 2.0, 1.0 },
                 Relationship.LEQ, 20.0));

         SimplexSolver solver = new SimplexSolver();
         RealPointValuePair solution = solver.optimize(
             f, constraints, GoalType.MAXIMIZE, true);

         // optimum: x=0, y=10, value=40
         assertEquals(0.0, solution.getPoint()[0], 1e-12);
         assertEquals(10.0, solution.getPoint()[1], 1e-12);
         assertEquals(40.0, solution.getValue(), 1e-12);
     }

     @Test
     public void testGetSolutionMinimization() {
         // minimize x + y  s.t.  x >= 2, y >= 3
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0, 1.0 }, 0.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1.0, 0.0 },
                 Relationship.GEQ, 2.0));
         constraints.add(new LinearConstraint(new double[] { 0.0, 1.0 },
                 Relationship.GEQ, 3.0));

         SimplexSolver solver = new SimplexSolver();
         RealPointValuePair solution = solver.optimize(
             f, constraints, GoalType.MINIMIZE, true);

         assertEquals(2.0, solution.getPoint()[0], 1e-12);
         assertEquals(3.0, solution.getPoint()[1], 1e-12);
         assertEquals(5.0, solution.getValue(), 1e-12);
     }

     // -------- Tableau construction tests --------

     @Test
     public void testCreateTableauStructureNoArtificials() {
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 5.0, 8.0 }, 7.0);
         LinearConstraint c = new LinearConstraint(
             new double[] { 1.0, 1.0 }, Relationship.LEQ, 10.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(c);

         TestableSimplexTableau tableau = new TestableSimplexTableau(
             f, constraints, GoalType.MAXIMIZE, true, 1e-6);
         double[][] data = tableau.getData();

         // width = numDecisionVars(2) + numSlackVars(1) + numArtificialVars(0) +
         // numObjectiveFuncs(1) + 1(RHS) = 5
         assertEquals(2, tableau.getHeight()); // objective + 1 constraint
         assertEquals(5, tableau.getWidth());

         // objective row (row 0): the code puts a 1 at [0][0] for maximize, then
         // negated coefficients (because maximize), then constant 7.0 (positive because maximize)
         // Columns: [obj0][x1][x2][slack1][RHS]
         assertEquals(1.0,  data[0][0], 1e-12);              // obj index
         assertEquals(-5.0, data[0][1], 1e-12);              // -coeff for max
         assertEquals(-8.0, data[0][2], 1e-12);
         assertEquals(0.0,  data[0][3], 1e-12);              // slack column
         assertEquals(7.0,  data[0][4], 1e-12);              // RHS = f.constantTerm

         // constraint row
         assertEquals(0.0,  data[1][0], 1e-12);
         assertEquals(1.0,  data[1][1], 1e-12);
         assertEquals(1.0,  data[1][2], 1e-12);
         assertEquals(1.0,  data[1][3], 1e-12);              // slack
         assertEquals(10.0, data[1][4], 1e-12);
     }

     @Test
     public void testCreateTableauWithArtificialsAndInit() {
         // Objective has two artificial rows because of EQ and GEQ constraints
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0 }, 0.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1.0 },
                 Relationship.EQ, 4.0));
         constraints.add(new LinearConstraint(new double[] { 1.0 },
                 Relationship.GEQ, 1.0));

         TestableSimplexTableau tableau = new TestableSimplexTableau(
             f, constraints, GoalType.MINIMIZE, true, 1e-6);

         // Two objective functions (row 0 artificial objectives, row 1 real objective)
         assertEquals(2, tableau.getNumObjectiveFunctions());
         assertEquals(2, tableau.getNumArtificialVariables());
         assertEquals(1, tableau.getNumDecisionVariables());
         assertEquals(1, tableau.getNumSlackVariables()); // one excess for GEQ

         double[][] data = tableau.getData();

         // After initialize() the artificial objective coefficients (row 0)
         // for the artificial variable columns should have been zeroed.
         int artOffset = tableau.getArtificialVariableOffset();
         for (int a = 0; a < 2; a++) {
             assertEquals("artificial variable column should be zero in row 0",
                     0.0, data[0][artOffset + a], 1e-12);
         }
     }

     // -------- getBasicRow and related tests --------

     @Test
     public void testGetBasicRowForSlackInSimpleTableau() {
         // Construct a tableau where only a slack variable is basic and check detection
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0 }, 0.0);
         LinearConstraint c = new LinearConstraint(
             new double[] { 1.0 }, Relationship.LEQ, 10.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(c);

         TestableSimplexTableau tableau = new TestableSimplexTableau(
             f, constraints, GoalType.MAXIMIZE, true, 1e-6);

         // In the initial tableau the slack column (index 2 = numObjFuncs(1)+numDecisionVars(1))
         // should have a unique 1 in row 1 (constraint row).
         int slackCol = tableau.getSlackVariableOffset();
         Integer basic = tableau.getBasicRow(slackCol);
         assertNotNull(basic);
         assertEquals(1, basic.intValue());
     }

     @Test
     public void testGetBasicRowReturnsNullForAmbiguousColumn() {
         // when a column has multiple non-zero entries among constraint rows
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0, 1.0 }, 0.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1.0, 1.0 },
                 Relationship.LEQ, 5.0));
         constraints.add(new LinearConstraint(new double[] { 1.0, 1.0 },
                 Relationship.LEQ, 3.0));

         TestableSimplexTableau tableau = new TestableSimplexTableau(
             f, constraints, GoalType.MAXIMIZE, true, 1e-6);

         // The first decision variable column (index 1 after obj func) has
         // coefficient 1 in both constraint rows -> getBasicRow should return null
         int x1Col = tableau.getNumObjectiveFunctions();
         Integer basic = tableau.getBasicRow(x1Col);
         assertEquals(null, basic);
     }

     // -------- discardArtificialVariables test --------

     @Test
     public void testDiscardArtificialVariablesAfterConstruction() {
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0 }, 0.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1.0 },
                 Relationship.GEQ, 2.0));
         constraints.add(new LinearConstraint(new double[] { 1.0 },
                 Relationship.LEQ, 5.0));

         TestableSimplexTableau tableau = new TestableSimplexTableau(
             f, constraints, GoalType.MAXIMIZE, true, 1e-6);
         // At least one artificial variable from GEQ
         assertTrue(tableau.getNumArtificialVariables() > 0);
         int origWidth = tableau.getWidth();
         int origHeight = tableau.getHeight();

         tableau.discardArtificialVariables();

         assertEquals(0, tableau.getNumArtificialVariables());
         assertEquals(origHeight - 1, tableau.getHeight()); // top artificial row removed
         assertEquals(origWidth - tableau.getNumArtificialVariables() - 1,
                 tableau.getWidth());
     }

     // -------- getInvertedCoeffiecientSum static helper --------

     @Test
     public void testGetInvertedCoefficientSum() {
         RealVector v = new ArrayRealVector(new double[] { 3.0, -2.0, 5.0 });
         double sum = SimplexTableau.getInvertedCoeffiecientSum(v);
         // sum = -(3) + -(-2) + -(5) = -3 + 2 - 5 = -6
         assertEquals(-6.0, sum, 1e-12);
     }

     // -------- equality and hash consistency --------

     @Test
     public void testEqualsAndHashCodeConsistency() {
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 1.0 }, 0.0);
         LinearConstraint c = new LinearConstraint(
             new double[] { 1.0 }, Relationship.LEQ, 5.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(c);

         SimplexTableau t1 = new SimplexTableau(f, constraints,
                 GoalType.MAXIMIZE, true, 1e-6);
         SimplexTableau t2 = new SimplexTableau(f, constraints,
                 GoalType.MAXIMIZE, true, 1e-6);
         SimplexTableau t3 = new SimplexTableau(
                 new LinearObjectiveFunction(new double[] { 1.0 }, 1.0),
                 constraints, GoalType.MAXIMIZE, true, 1e-6);

         assertEquals(t1, t2);
         assertEquals(t1.hashCode(), t2.hashCode());
         assertFalse(t1.equals(t3));
     }

     // -------- boundary / zero solution --------

     @Test
     public void testGetSolutionWhenOptimumIsZero() {
         // maximize 0*x subject to x <= 10 -> value = 0
         LinearObjectiveFunction f = new LinearObjectiveFunction(
             new double[] { 0.0 }, 0.0);
         LinearConstraint c = new LinearConstraint(
             new double[] { 1.0 }, Relationship.LEQ, 10.0);
         List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(c);

         SimplexSolver solver = new SimplexSolver();
         RealPointValuePair solution = solver.optimize(
             f, constraints, GoalType.MAXIMIZE, true);

         assertEquals(0.0, solution.getValue(), 1e-12);
         // x could be zero (the cheapest basic feasible solution)
         assertTrue(solution.getPoint()[0] >= -1e-12);
     }
 }

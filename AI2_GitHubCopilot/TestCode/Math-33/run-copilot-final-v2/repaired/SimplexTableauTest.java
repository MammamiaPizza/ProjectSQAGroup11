package org.apache.commons.math3.optimization.linear;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**

 - Tests for {@link SimplexTableau} that exercise the bug described in MATH-781
 - as well as related normal, boundary and exception branches.
  */
 public class SimplexTableauTest {
  private static final double DELTA = 1e-10;
  // ---- restricted (non‑negative) baseline tests ----
  @Test
  public void testMaximizeLEQ() {
  // max 2x1+3x2 s.t. x1+x2 <= 4, x1,x2>=0  → (0,4) val=12
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{2, 3}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.LEQ, 4));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(12.0, solution.getValue(), DELTA);
  assertArrayEquals(new double[]{0, 4}, solution.getPoint(), DELTA);
  }
  @Test
  public void testMinimizeGEQ() {
  // min 2x1+3x2 s.t. x1+x2 >=4, x1,x2>=0  → (4,0) val=8
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{2, 3}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1, 1}, Relationship.GEQ, 4));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(8.0, solution.getValue(), DELTA);
  assertArrayEquals(new double[]{4, 0}, solution.getPoint(), DELTA);
  }
  // ---- unrestricted variable tests – triggers MATH-781 ----
  @Test
  public void testUnrestrictedMinimizeGEQ() {
  // min x s.t. x >=1, x unrestricted  → x=1, val=1
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1}, Relationship.GEQ, 1));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, false,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(1.0, solution.getValue(), DELTA);
  assertEquals(1.0, solution.getPoint()[0], DELTA);
  }
  @Test
  public void testUnrestrictedMaximizeLEQ() {
  // max x s.t. x <=5, x unrestricted  → x=5, val=5
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1}, Relationship.LEQ, 5));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, false,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(5.0, solution.getValue(), DELTA);
  assertEquals(5.0, solution.getPoint()[0], DELTA);
  }
  @Test
  public void testEqualityUnrestricted() {
  // min x s.t. x = 5, x unrestricted  → x=5, val=5
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1}, Relationship.EQ, 5));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, false,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(5.0, solution.getValue(), DELTA);
  assertEquals(5.0, solution.getPoint()[0], DELTA);
  }
  // ---- phase‑1 (artificial variables) tests ----
  @Test
  public void testPhaseOneRestricted() {
  // min x1+x2 s.t. x1-x2 >=2, x1,x2>=0  → (2,0) val=2
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1, 1}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1, -1}, Relationship.GEQ, 2));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(2.0, solution.getValue(), DELTA);
  assertArrayEquals(new double[]{2, 0}, solution.getPoint(), DELTA);
  }
  @Test
  public void testPhaseOneUnrestricted() {
  // min x s.t. x >=1, x unrestricted (needs artificial)  → x=1, val=1
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1}, Relationship.GEQ, 1));
  SimplexSolver solver = new SimplexSolver();
  PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, false,
                                            SimplexSolver.DEFAULT_EPSILON);
  assertEquals(1.0, solution.getValue(), DELTA);
  assertEquals(1.0, solution.getPoint()[0], DELTA);
  }
  // ---- direct tableau tests (protected members accessible from same package) ----
  @Test
  public void testGetBasicRow() {
  // simple max x, x<=5, x>=0: slack column should be basic in constraint row (row 1)
  LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1}, 0);
  Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
  constraints.add(new LinearConstraint(new double[]{1}, Relationship.LEQ,5));
  SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true,

1e-10);
        Integer basicRow = tableu.getBasicRow(tableau.getSlackVariableOffset());
        assertNotNull("slack column should be basic", basicRow);
        assertEquals(1, basicRow.intValue());
    }

 @Test
 public void testNormalizeConstraintsNegativeRHS() {
     // ²x = -4  → after normalization  -2x =4
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1},0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{2}, Relationship.EQ, -4);
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true,

1e-10);
        int xCol = tableu.getNumObjectiveFunctions();                // column of x0
        assertEquals(-2.0, tableu.getEntry(1, xCol), DELTA);        // coefficient flipped
        assertEquals(4.0, tableu.getEntry(1, tableu.getWidth() - 1), DELTA); // RHS positive
    }

 @Test
 public void testInitializeColumnLabelsWidth() {
     // ¹ obj + ² decision + ¹ slack + ⁰ artificial + ¹ RHS = ⁵
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1, 2},0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1, 0}, Relationship.LEQ,2));
     SimplexTableau tableau = new SimplexTableau(f, constraints, GoalType.MAXIMIZE, true,

1e-10);
        assertEquals(5, tableu.getWidth());
    }

 // ---- edge cases ----

 @Test
 public void testSolutionWithZeroDecisionVariables() {
     // only a constant objective → value equals constant, point is empty
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{},5);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     SimplexSolver solver = new SimplexSolver();
     PointValuePair solution = solver.optimize(f, constraints, GoalType.MINIMIZE, true,
                                               SimplexSolver.DEFAUL_EPSILON);
     assertEquals(5.0, solution.getValue(), DELTA);
     assertEquals(0, solution.getPoint().length);
 }

 @Test
 public void testSolutionMaximizeZeroCoefficients() {
     // max 0*x, x<=3, x>=0 → x=0 is optimal
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{0}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1}, Relationship.LEQ, 3));
     SimplexSolver solver = new SimplexSolver();
     PointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true,
                                               SimplexSolver.DEFAULT_EPSILON);
     assertEquals(0.0, solution.getValue(), DELTA);
     assertEquals(0.0, solution.getPoint()[0], DELTA);
 }

 @Test
 public void testDuplicateConstraint() {
     // duplicate rows should not break the solver
     LinearObjectiveFunction f = new LinearObjectiveFunction(new double[]{1}, 0);
     Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
     constraints.add(new LinearConstraint(new double[]{1}, Relationship.LEQ, 3));
     constraints.add(new LinearConstraint(new double[]{1}, Relationship.LEQ, 3));
     SimplexSolver solver = new SimplexSolver();
     PointValuePair solution = solver.optimize(f, constraints, GoalType.MAXIMIZE, true,
                                               SimplexSolver.DEFAULT_EPSILON);
     assertEquals(3.0, solution.getValue(), DELTA);
     assertEquals(3.0, solution.getPoint()[0], DELTA);
 }

}


 I messed up again. I need to generate the correct code without introducing new errors. I'll
carefully reconstruct the corrected version from the original prompt, fixing only the known issues.

 The original prompt's test class, with line numbers, had these errors:

 1. Line ~109: missing `)` and `;` after `new LinearObjectiveFunction(new double[]{1},0`. Should be
`new LinearObjectiveFunction(new double[]{1}, 0);` and the next line starts with
`Collection<LinearConstraint> constraints = ...`.

 2. In `testEqualityUnrestricted`: `GoalType.MNIMIZE` -> `GoalType.MINIMIZE`;
`SimplexSolver.DEFAUL_EPSILON` -> `SimplexSolver.DEFAULT_EPSILON`.

 3. In `testSolutionMaximizeZeroCoefficients`: `GoalType.MXIMIZE` -> `GoalType.MAXIMIZE`;
`SimplexSolver.DEFAUL_EPSILON` -> `SimplexSolver.DEFAULT_EPSILON`.

 4. In `testDuplicateConstraint`: `GoalType.MXIMIZE` -> `GoalType.MAXIMIZE`;
`SimplexSolver.DEFAUL_EPSILON` -> `SimplexSolver.DEFAULT_EPSILON`.

 Also, there might be a typo in `testSolutionMaximizeZeroCoefficients` where `DEFAUL_EPSILON` is
used, similarly `testDuplicateConstraint`. Also check `testSolutionMaximizeZeroCoefficients` uses
`SimplexSolver.DEFAUL_EPSILON` but the correct is `DEFAULT_EPSILON`. Yes.

 Also, `testEqualityUnrestricted` uses `DEFAUL_EPSILON`, fix.

 Now, I'll output the exact corrected Java code, preserving everything else, including comments and
formatting. Let me write it cleanly

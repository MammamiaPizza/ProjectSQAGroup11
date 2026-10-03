package org.apache.commons.math.optimization.linear;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;

 import java.util.ArrayList;
 import java.util.Collection;

 import org.apache.commons.math.optimization.GoalType;
 import org.apache.commons.math.optimization.RealPointValuePair;
 import org.junit.Test;

 /**
  * Tests for SimplexTableau focusing on normalization of negative variable lower bounds
  * (MATH-713).  The bug causes incorrect tableau entries when variables are not forced
  * non‑negative and have negative lower bounds.
  */
 public class SimplexTableauTest {

     private static final double EPS = 1e-10;

     // --- single variable with negative lower bound ---
     @Test
     public void testSingleVarNegativeLbMinimize() {
         // min x  s.t.  x >= -10,  x <= 5  -> optimum at lower bound
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.GEQ, -10));
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ,  5));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(-10.0, sol.getPoint()[0], EPS);
     }

     @Test
     public void testSingleVarNegativeLbMaximize() {
         // max x  s.t.  x >= -10,  x <= 5  -> optimum at upper bound
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.GEQ, -10));
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ,  5));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MAXIMIZE, false);
         assertEquals(5.0, sol.getPoint()[0], EPS);
     }

     // --- two variables, both negative lower bounds ---
     @Test
     public void testTwoVarsAllNegativeLb() {
         // min x1 + x2  s.t.  x1 >= -10, x2 >= -5, x1 <= 10, x2 <= 10
         // optimum at (-10, -5)
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, -10));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, -5));
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.LEQ,  10));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.LEQ,  10));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(-10.0, sol.getPoint()[0], EPS);
         assertEquals(-5.0,  sol.getPoint()[1], EPS);
     }

     // --- mixed lower bounds: one negative, one non-negative ---
     @Test
     public void testMixedSignLbs() {
         // min x1 + x2  s.t.  x1 >= -8, x2 >= 0, x1 <= 6, x2 <= 6
         // optimum at (-8, 0)
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, -8));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ,  0));
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.LEQ,  6));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.LEQ,  6));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(-8.0, sol.getPoint()[0], EPS);
         assertEquals( 0.0, sol.getPoint()[1], EPS);
     }

     // --- equality constraint with variable having negative lower bound ---
     @Test
     public void testEqualityWithNegativeLb() {
         // min x  s.t.  x = -5,  x >= -10  (redundant),  x <= 2
         // optimum at x = -5
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.EQ, -5));
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.GEQ, -10));
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ,  2));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(-5.0, sol.getPoint()[0], EPS);
     }

     // --- zero lower bound (no normalization needed) ---
     @Test
     public void testZeroLb() {
         // min x  s.t.  x >= 0,  x <= 3  -> optimum at 0
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.GEQ, 0));
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ,  3));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(0.0, sol.getPoint()[0], EPS);
     }

     // --- two variables, one with a tight equality that forces a negative value ---
     @Test
     public void testNegativeVarFromEqualityAndInequality() {
         // min x1 + x2  s.t.  x1 + x2 = -7,  x1 >= -10, x2 >= -5,  x1 <= 3, x2 <= 3
         // feasible optimum at (-10, 3)?  Actually -10+3 = -7, but x2 <=3, so (-10,3) gives -7.
Another:
         // (-2,-5) also gives -7.
         // objective x1+x2 = -7 constant? The objective equals -7 for all feasible points, so any
feasible
         // point is fine.
         // We just need the solver to return a feasible point. We'll check that x1+x2 = -7 and
bounds
         // respected.
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1, 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.EQ, -7));
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, -10));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, -5));
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.LEQ,  3));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.LEQ,  3));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         double x1 = sol.getPoint()[0];
         double x2 = sol.getPoint()[1];
         assertTrue("x1 out of bounds", -10 - EPS <= x1 && x1 <= 3 + EPS);
         assertTrue("x2 out of bounds", -5  - EPS <= x2 && x2 <= 3 + EPS);
         assertEquals(-7.0, x1 + x2, EPS);
     }

     // --- multiple variables with negative lower bounds and a regular optimum ---
     @Test
     public void testMultipleVarsNegativeLbs() {
         // min -x1 - x2  (i.e. maximize x1 + x2)
         // s.t. x1 >= -5, x2 >= -5, x1+x2 <= 10
         // optimum at (10,0) or (5,5) etc. value = 10.
         // So solution point should satisfy constraints and have objective value -10.
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { -1, -1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1, 0 }, Relationship.GEQ, -5));
         constraints.add(new LinearConstraint(new double[] { 0, 1 }, Relationship.GEQ, -5));
         constraints.add(new LinearConstraint(new double[] { 1, 1 }, Relationship.LEQ, 10));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(-10.0, sol.getValue(), EPS);
         assertTrue("x1 < -5", sol.getPoint()[0] >= -5 - EPS);
         assertTrue("x2 < -5", sol.getPoint()[1] >= -5 - EPS);
         assertTrue("x1 + x2 > 10", sol.getPoint()[0] + sol.getPoint()[1] <= 10 + EPS);
     }

     // --- negative lower bound tighter than zero, rest non-negative ---
     @Test
     public void testTightNegativeLbAgainstZero() {
         // min x  s.t. x >= -2, x <= 5  -> -2
         SimplexSolver solver = new SimplexSolver();
         LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 1 }, 0);
         Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.GEQ, -2));
         constraints.add(new LinearConstraint(new double[] { 1 }, Relationship.LEQ,  5));
         RealPointValuePair sol = solver.optimize(f, constraints, GoalType.MINIMIZE, false);
         assertEquals(-2.0, sol.getPoint()[0], EPS);
     }

 }
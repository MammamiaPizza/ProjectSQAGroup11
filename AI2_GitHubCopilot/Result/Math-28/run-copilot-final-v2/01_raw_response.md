TARGETS: SimplexSolver.getPivotColumn; getPivotRow; doIteration; solvePhase1; doOptimize
ORACLES: testMath828Cycle expects no MaxCountExceededException; optimal solution found
ORACLES: Expected optimal value from the specific test case problem
CASES: MATH-828 triggering degenerate cycle; tied ratio in pivot row selection
CASES: Small feasible LPs; boundary: infeasible or unbounded problems
RISKS: Only method signatures shown; actual test data and fix are hidden
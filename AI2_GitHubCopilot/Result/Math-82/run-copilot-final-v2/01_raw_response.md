TARGETS: doIteration, getPivotColumn, getPivotRow, solvePhase1, isOptimal, doOptimize
ORACLES: Compare final objective value to known optimum 10.0 (testMath288 assertion)
ORACLES: Tableau optimality condition: all objective-row coefficients >= -epsilon
CASES: Exact LP instance from MATH-288 (triggers the failing pivot choice)
CASES: LP with unique optimum to verify correct simplex termination
CASES: Degenerate LP where multiple entering columns compete (Bland's rule tie-break)
CASES: Boundary: near-zero ratio in getPivotRow, rhs/entry <= epsilon handling
CASES: Empty or infeasible LP to ensure solver reports correct status
RISKS: Unknown LP model coefficients; only objective value mismatch is observed
RISKS: Epsilon sensitivity may affect pivot selection and reproducibility
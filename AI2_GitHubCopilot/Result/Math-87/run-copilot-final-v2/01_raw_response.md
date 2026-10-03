TARGETS: createTableau(boolean) – tableau matrix construction
TARGETS: getSolution() – extracting optimized RealPointValuePair
TARGETS: getBasicRow(int) – basic variable column resolution
TARGETS: discardArtificialVariables() – post-solve cleanup
TARGETS: getInvertedCoeffiecientSum(RealVector) – static helper for objective row
ORACLES: testSingleVariableAndConstraint expects solution value 10.0 (not 0.0)
ORACLES: Same test likely defines one LinearConstraint plus objective; solution from getSolution()
CASES: single decision variable, single constraint (MATH-273 minimal case)
CASES: boundary where solving yields 0.0 due to incorrect basic-row or artificial-variable handling
RISKS: Only SimplexTableau source visible; SimplexSolver orchestration not inspectable
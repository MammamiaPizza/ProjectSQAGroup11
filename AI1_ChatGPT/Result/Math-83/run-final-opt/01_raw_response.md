TARGETS: SimplexTableau.getSolution() and basic-row selection used to extract decision-variable values  
TARGETS: createTableau(), normalize(), and artificial-variable handling affect tableau-derived solutions  
ORACLES: Existing trigger SimplexSolverTest::testMath286 expects objective value 6.9  
ORACLES: Compare returned RealPointValuePair value against solver result asserted by the trigger  
CASES: Reproduce the Math286 linear-program setup through the existing SimplexSolver test path  
CASES: Verify solution extraction after tableau initialization and any artificial-variable removal  
CASES: Cover variables lacking a unique basic row, if represented by the trigger tableau  
RISKS: Context omits the Math286 constraints, objective coefficients, and SimplexTableau constructor signatures  
RISKS: Do not infer exact point coordinates; only the supplied expected objective value is known
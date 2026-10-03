TARGETS: SimplexTableau tableau construction/initialization and getSolution() basic-variable extraction.  
ORACLES: Existing trigger expects SimplexSolver optimum value 10.0, not 0.0.  
CASES: Single decision variable with one constraint, exercised through SimplexSolverTest scenario.  
CASES: Verify solution point/value when the decision variable is basic versus absent from a basic row.  
RISKS: Tableau is package-private; tests may need same package or solver-level coverage.  
RISKS: No constructor signatures or full trigger inputs are provided; avoid assuming constraint/objective APIs.
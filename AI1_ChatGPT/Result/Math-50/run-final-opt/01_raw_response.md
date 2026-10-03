TARGETS: BaseSecantSolver.doSolve() endpoint update, interpolation, and Method-specific convergence handling  
ORACLES: RegulaFalsiSolverTest.testIssue631 expects TooManyEvaluationsException  
CASES: Regula Falsi input that stagnates without satisfying function/position accuracy before max evaluations  
CASES: Normal bracketed root; exact endpoint root; function-value and absolute/relative accuracy boundaries  
RISKS: doSolve is protected; exercise through a concrete solver/public solve API available in the project  
RISKS: Context provides no exact Issue631 function, interval, max-evaluation value, or expected root
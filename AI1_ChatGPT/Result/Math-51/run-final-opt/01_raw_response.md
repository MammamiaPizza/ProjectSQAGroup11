TARGETS: BaseSecantSolver.doSolve() secant update, bracketing endpoint updates, and Method-specific behavior.  
ORACLES: RegulaFalsiSolverTest::testIssue631; convergence must avoid exceeding allowed evaluations.  
CASES: Regula Falsi case that previously exhausts 3,624 evaluations; verify a root result within solver accuracy.  
CASES: Root at an initial endpoint; function-value and absolute/relative accuracy stopping boundaries.  
CASES: Bracket iterations where one endpoint is repeatedly retained, risking false-position stagnation.  
RISKS: Exact expected root/function and solver configuration are not provided in this context.  
RISKS: Available signatures omit public solve overloads and BaseSecantSolver's full bracketing/error behavior.
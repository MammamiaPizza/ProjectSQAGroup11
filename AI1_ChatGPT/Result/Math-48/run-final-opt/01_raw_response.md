TARGETS: BaseSecantSolver.doSolve() regula-falsi iteration and interpolation update behavior  
ORACLES: RegulaFalsiSolverTest::testIssue631 expects ConvergenceException  
ORACLES: Trigger rejects TooManyEvaluationsException for the issue-631 scenario  
CASES: Execute the existing issue-631 regula-falsi input through RegulaFalsiSolver  
CASES: Cover interpolation stagnation when computed x repeats an endpoint  
RISKS: doSolve is protected/final; test through a concrete solver such as RegulaFalsiSolver  
RISKS: Exact issue-631 function, interval, and evaluation limit are not provided here
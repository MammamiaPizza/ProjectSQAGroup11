TARGETS: BaseSecantSolver.doSolve() iteration limit check, TooManyEvaluationsException raising.
ORACLES: RegulaFalsiSolverTest::testIssue631 expects TooManyEvaluationsException for a
non-convergent function.
CASES: Normal root convergence; function causing infinite/slow convergence exceeding max
evaluations; boundary atol/rtol near zero.
RISKS: The test payload (function, solver parameters) is not provided; missing check could be in
loop condition or maxEval guard.
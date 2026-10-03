TARGETS: BracketingNthOrderBrentSolver.doSolve interpolation/bracketing iteration and evaluation counting.
TARGETS: Constructor and getMaximalOrder() only if exposed behavior is asserted by existing tests.
ORACLES: MATH-716 trigger must not throw TooManyEvaluationsException at maximal count 100.
ORACLES: Existing BracketingNthOrderBrentSolverTest::testIssue716 is the expected-result source.
CASES: Regression execution of the exact testIssue716 solve scenario; verify convergence before 100 evaluations.
CASES: Normal bracketed-root solving and tolerance-boundary convergence where available from existing tests.
RISKS: Inputs, expected root/value, constructor parameters, and corrected behavior are not provided in context.
RISKS: Do not derive expectations from another program version or assume undocumented solver APIs.
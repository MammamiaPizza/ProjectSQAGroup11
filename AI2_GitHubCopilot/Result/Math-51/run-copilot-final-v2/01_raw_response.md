TARGETS: BaseSecantSolver.doSolve() secant iteration convergence
ORACLES: Return root (not exception) within configured maxEval
ORACLES: Root accuracy vs known solution from RegulaFalsiSolverTest::testIssue631
CASES: Normal: f(a)*f(b)<0 with simple root; expect convergence
CASES: Boundary: f(a)>>f(b) causing slow secant (MATH-631 scenario)
CASES: Error: interval with no sign change should throw
RISKS: Without fix, secant may diverge; test must set reasonable maxEval
RISKS: Only RegulaFalsiSolverTest::testIssue631 context; other subclasses unknown
RISKS: Limited info on exact tolerance/criteria; rely on test expectation

That's 9 lines. Ensure each <=120 chars. Let's check: longest: "CASES: Boundary: f(a)>>f(b) causing
slow secant (MATH-631 scenario)" is 66 chars. OK.

Now output.TARGETS: BaseSecantSolver.doSolve() secant iteration convergence
ORACLES: Return root (not exception) within configured maxEval
ORACLES: Root accuracy vs known solution from RegulaFalsiSolverTest::testIssue631
CASES: Normal: f(a)*f(b)<0 with simple root; expect convergence
CASES: Boundary: f(a)>>f(b) causing slow secant (MATH-631 scenario)
CASES: Error: interval with no sign change should throw
RISKS: Without fix, secant may diverge; test must set reasonable maxEval
RISKS: Only RegulaFalsiSolverTest::testIssue631 context; other subclasses unknown
RISKS: Limited info on exact tolerance/criteria; rely on test expectation
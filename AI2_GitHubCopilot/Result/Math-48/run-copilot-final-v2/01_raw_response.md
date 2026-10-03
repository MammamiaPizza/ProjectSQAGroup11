TARGETS: BaseSecantSolver.doSolve() exception type when root not found after max evaluations.
ORACLES: testIssue631 expects ConvergenceException, not TooManyEvaluationsException.
ORACLES: TooManyEvaluationsException indicates max evaluations reached before iteration limit.
CASES: solver exceeds max evaluations without converging; normal convergence; endpoint is root.
CASES: function with sign change but very flat; tight absolute accuracy; zero relative accuracy.
RISKS: doSolve() source unavailable; exception hierarchy partially known.
RISKS: TooManyEvaluationsException may originate from computeObjectiveValue() inside the iteration.
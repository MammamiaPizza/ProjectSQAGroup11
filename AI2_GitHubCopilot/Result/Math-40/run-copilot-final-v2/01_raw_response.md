TARGETS: BracketingNthOrderBrentSolver.doSolve loop, convergence check, evaluate‑count guard,
bracket‑narrowing logic.
ORACLES: Must converge within getMaxEvaluations(); accepted root satisfies |f(root)| ≤
getFunctionValueAccuracy().
ORACLES: Expected from math: solver should stop when bracket width < xTol or residual tolerance met.
CASES: testIssue716 input (slow‑converging function); flat/plateau regions with near‑zero
derivative; double root.
CASES: Normal: continuous, sign‑change bracket, default order; boundary: bracket already contains
root within xTol.
CASES: Error: initial bracket without sign change (NoBracketingException), max order 0
(MathIllegalArgumentException).
RISKS: Exact root position unknown—relies on solver’s internal iteration; cannot inspect
intermediate steps.
RISKS: Without fix, TooManyEvaluationsException may still occur for functions that converge slowly
even after patch.
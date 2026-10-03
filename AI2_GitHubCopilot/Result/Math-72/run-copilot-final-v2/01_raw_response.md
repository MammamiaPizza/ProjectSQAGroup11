TARGETS: BrentSolver.solve(min,max), solve(min,max,initial) — endpoint root detection and
convergence.
ORACLES: Mathematical root of UnivariateRealFunction (e.g., sin(x) root at π) yields exact x.
Expected from interval and function behavior.
ORACLES: If f(min)==0, return min; if f(max)==0, return max (endpoint-as-root contract implied by
test).
CASES: Normal: root strictly inside interval (e.g., sign change); ensure convergence to within
default accuracy.
CASES: Boundary: root exactly at min endpoint; root exactly at max endpoint; function touches zero
at endpoint but same sign.
CASES: Error: f(min)*f(max)>0 → IllegalArgumentException for no bracket; invalid interval
(min>=max).
RISKS: Context lacks solver defaults (absolute/relative accuracy); must rely on
UnivariateRealSolverImpl contract.
RISKS: Only buggy-version signatures; no access to fixed version; must infer correct endpoint
handling from test “expected π but got ~0”.
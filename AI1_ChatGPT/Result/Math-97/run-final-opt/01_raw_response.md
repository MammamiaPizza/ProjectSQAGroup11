TARGETS: BrentSolver.solve(double min,double max) endpoint-root handling; solve(min,max,initial) if relevant.
ORACLES: Trigger expects no sign-bracketing error when endpoint function value is effectively/actually root.
CASES: f(x)=sin(x), [3.0, PI]: upper endpoint near zero must return an endpoint root.
CASES: Exact lower/upper endpoint roots; verify returned root and no IllegalArgumentException.
CASES: Normal opposite-sign bracket with interior root; preserve successful convergence.
RISKS: Floating-point near-zero endpoint (sin(PI)) is positive, so strict sign checks reject valid endpoint roots.
RISKS: Context gives only BrentSolver and trigger; no other-version behavior or additional API contracts.
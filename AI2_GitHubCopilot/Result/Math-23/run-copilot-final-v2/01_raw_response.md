TARGETS: BrentOptimizer.optimize() returning UnivariatePointValuePair with best observed point &
value across all evaluations; verify stop condition preserves init if init is best.
ORACLES: Use simple functions (x², cos(x), etc.) with known optimum; compare returned
point=argmin(f), value=min(f); expected from prior evaluations or analytical optimum.
CASES: init=0 at min of x²; init inside interval but not optimal; init at lower bound; init at upper
bound; init far from optimum but function flat near optimum; oscillatory function with local
optima.
RISKS: Bug may stem from m calc using a,b (uninitialized); limited context (snippet may be wrong);
must ensure test catches failure without full impl knowledge.
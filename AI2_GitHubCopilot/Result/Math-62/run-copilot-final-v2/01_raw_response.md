TARGETS: getOptima() returns expected minima (e.g., quintic min -0.2719561293); count equals number
of starts.
ORACLES: Known analytical minimum of quintic; expected -0.2719561293 from test (Math-62b original).
CASES (normal): many random starts, wide bounds, unimodal fn; single start.
CASES (boundary): bounds equal (min==max); maxEvaluations exactly exhausted; zero starts.
CASES (error): no convergence before maxEvaluations; invalid function/bounds.
RISKS: Convergence tolerance not exposed; expected value may be unrealistically precise
(floating-point).
RISKS: Random generator may cause non-deterministic optima; default sortPairs order depends on
comparator.
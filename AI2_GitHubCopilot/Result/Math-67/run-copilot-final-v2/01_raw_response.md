TARGETS: getResult() returns optimum with smallest function value (not absolute) across starts
TARGETS: getOptima() and getOptimaValues() reflect the true best from all starts
ORACLES: Quintic minimum at -0.27195612846834, verified by high-accuracy Brent solver
ORACLES: For minimization, getFunctionValue() <= min(getOptimaValues())
CASES: Normal: multiple random starts, verify getResult() equals expected minimum
CASES: Boundary: single start, min==max bounds, identical starts → returns that optimum
CASES: Error: 0 starts → getOptima() throws IllegalStateException
RISKS: Result sensitive to default iterations/accuracy of decorated optimizer
RISKS: Random bounds may cause non-deterministic failures without a fixed seed
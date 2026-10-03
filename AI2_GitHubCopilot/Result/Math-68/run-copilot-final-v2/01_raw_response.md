TARGETS: LevenbergMarquardtOptimizer.doOptimize convergence accuracy for Minpack problems
TARGETS: qrDecomposition numerical stability when Jacobian is rank-deficient
TARGETS: qTy result correctness after QR factorization
ORACLES: Minpack reference cost/param values: JennrichSampson cost=0.2578330049, FreudensteinRoth
cost=11.4121122022341
ORACLES: Gradient norm at optimum ≈ 0; residual norm matches expected value for each test function
CASES: Normal: JennrichSampson and FreudensteinRoth problems (failing triggers); vary initial
guesses
CASES: Boundary: tolerances (costRelative, parRelative, ortho) set to 1e-12, verify precision holds
CASES: Boundary: non-positive initialStepBoundFactor; expect IllegalArgumentException or default
clamp
CASES: Error: objective returns NaN / Inf; verify exception propagation without silent convergence
RISKS: Exact Minpack convergence path unavailable; small numeric drift from reference is expected
risk area
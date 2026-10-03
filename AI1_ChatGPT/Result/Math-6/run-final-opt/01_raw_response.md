TARGETS: BaseOptimizer optimize/count getters; iteration accounting in all listed scalar/vector optimizers.
ORACLES: Trigger tests provide expected convergence results and expected getIterations behavior.
CASES: Trivial conjugate-gradient optimization; assert convergence and nonzero/correct iteration count.
CASES: CMA-ES benchmark objectives, including bounded constrained Rosen and maximize/minimize goals.
CASES: Powell and simplex benchmark minimization/maximization; preserve returned optimum/value accuracy.
CASES: Gauss-Newton and Levenberg-Marquardt getIterations after vector least-squares optimization.
RISKS: Stochastic CMA-ES can make exact convergence assertions flaky; use existing deterministic test setup.
RISKS: Context omits full test bodies and exact expected iteration values/limits.
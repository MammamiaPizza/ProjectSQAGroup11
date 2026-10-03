TARGETS: UnivariateRealSolverUtils.solve(UnivariateRealFunction,double,double); endpoint-root convergence behavior
TARGETS: midpoint(double,double) only if needed for direct arithmetic boundary coverage
ORACLES: MATH-280 trigger: solve must not throw ConvergenceException when bracket endpoint evaluates to 0
ORACLES: Trigger evidence has f(0)=-0.477 and f(2)=0; expected result source is the exact endpoint value
CASES: Function with opposite signs and exact zero at upper endpoint; interval [0, Double.MAX_VALUE]
CASES: Exact zero at lower endpoint; normal finite sign-changing bracket; midpoint with finite endpoints
RISKS: Only solve/midpoint signatures are available; solver configuration and intended tolerances are not provided
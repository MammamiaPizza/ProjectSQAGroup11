TARGETS: BaseOptimizer.incrementIterationCount() and incrementEvaluationCount()
TARGETS: BaseOptimizer.optimize() calling doOptimize() and parseOptimizationData()
TARGETS: Each optimizer's doOptimize(): convergence loops, iteration/evaluation increments
ORACLES: Known optima for test functions (Rosen: 0 at (1,1), Sphere: 0 at (0,0), etc.)
ORACLES: Iteration count >0; evaluation count >0; convergence within max iterations
ORACLES: Assertions on PointValuePair value and point from ConvergenceChecker result
CASES: Simple quadratic (testTrivial), standard benchmarks (testRosen, testSphere)
CASES: Constrained (testConstrainedRosen), maximize (Simplex), testGetIterations (GN, LM)
CASES: Edge: start at optimum, max evaluations exceeded, zero gradient initial point
RISKS: Bug likely in BaseOptimizer causing skip of incrementIterationCount => zero iterations, no
convergence
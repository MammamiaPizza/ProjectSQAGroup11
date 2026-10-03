TARGETS: SimplexSolver.doOptimize, solvePhase1, doIteration, getPivotColumn/getPivotRow, isOptimal.  
ORACLES: Existing trigger SimplexSolverTest::testMath288 expects objective value 10.0, not 11.5.  
CASES: Reproduce MATH-288 linear program and assert optimized value/result through public optimizer API.  
CASES: Cover pivot selection when multiple eligible rows/columns exist, including tie handling.  
CASES: Cover phase-1 feasibility completion before phase-2 optimization.  
RISKS: Key pivot helpers are private/protected; test observable optimization results rather than internals.  
RISKS: Context omits MATH-288 constraint data and public doOptimize inputs; derive only from existing test/source.
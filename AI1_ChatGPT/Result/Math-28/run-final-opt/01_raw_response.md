TARGETS: SimplexSolver.doOptimize/solvePhase1/doIteration pivot selection and anti-cycling behavior.
ORACLES: Existing trigger test testMath828Cycle; successful optimization must avoid MaxCountExceededException.
CASES: Reproduce the MATH-828 cycling linear program through public SimplexSolver optimization API.
CASES: Assert termination within configured iteration limit and expected PointValuePair from the trigger's known setup.
CASES: Cover degenerate/tied minimum-ratio pivots if expressible using existing LinearConstraint APIs.
RISKS: Private pivot methods require indirect testing via optimize; no trigger input/objective data is provided here.
RISKS: Do not infer numerical optimum, constraints, or iteration count without the existing test source.
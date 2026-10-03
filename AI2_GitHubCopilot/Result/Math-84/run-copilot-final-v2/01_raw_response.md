TARGETS: iterateSimplex convergence and replacement of worst point using evaluateNewSimplex with
1.0/khi/gamma.
TARGETS: Constructor khi/gamma validation and default values; interaction with DirectSearchOptimizer
maxIterations.
ORACLES: For simple convex functions (e.g., sphere), iterateSimplex must converge within default
maxIterations.
ORACLES: Comparator must correctly rank simplex points; best/worst ordering consistent with
optimization direction.
CASES: Normal: optimize 2D/3D sphere and Rosenbrock; Boundary: khi=2.0, gamma=0.5 (defaults) and
edge values like khi=0.5.
CASES: Error: oscillatory or non-convergent function triggers MaxIterationsExceededException with
expected message.
CASES: Boundary: iteration limit = 1, starting simplex already at optimum; ensure no exception and
result equals optimum.
CASES: Boundary: high-dimensional simplex (N=5) to stress evaluateNewSimplex and xOriginal/xSmallest
handling.
RISKS: No reference correct version; expected convergence only from algorithm theory, not from spec.
RISKS: Default khi/gamma may fail to converge for some functions; test assertions may pass
accidentally on buggy code.
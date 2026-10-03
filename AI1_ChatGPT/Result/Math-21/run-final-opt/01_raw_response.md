TARGETS: RectangularCholeskyDecomposition(RealMatrix,double), getRootMatrix(), getRank()
ORACLES: Root reconstruction should match the input covariance/symmetric matrix within test tolerance.
ORACLES: getRank() should report full rank for positive-definite full-rank inputs.
CASES: MATH-789 matrix: verify reconstructed entry expected 0.0 is not 0.0180652917341963.
CASES: Positive-definite full-rank matrix: verify rank and root-based reconstruction.
CASES: Boundary small threshold: pivots near/at threshold and resulting rank/root behavior.
RISKS: Context omits matrix fixtures, exact tolerance, and intended behavior for non-positive pivots.
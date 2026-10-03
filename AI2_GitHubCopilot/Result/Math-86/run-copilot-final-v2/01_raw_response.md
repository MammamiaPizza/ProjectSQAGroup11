TARGETS: Constructor CholeskyDecompositionImpl(RealMatrix) must detect non-positive-definite input
and throw NotPositiveDefiniteMatrixException.
TARGETS: getL(), getLT(), getDeterminant() and solver methods (solve, getInverse) after
decomposition of non-positive-definite matrix.
ORACLES: Expected NotPositiveDefiniteMatrixException as asserted by testMath274 and
testNotPositiveDefinite.
CASES: Input matrices with negative eigenvalue, zero eigenvalue, non-symmetric, singular, or
near-zero pivot.
RISKS: Buggy version may not throw exception; thresholds (e.g., 1e-10 absolute) may allow borderline
non-positive matrices to decompose silently.
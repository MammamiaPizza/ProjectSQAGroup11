TARGETS: CholeskyDecompositionImpl constructors; positivity validation while decomposing symmetric matrices.
ORACLES: Existing triggers require NotPositiveDefiniteMatrixException for non-positive-definite input.
CASES: Symmetric matrix with a zero/negative pivot must throw NotPositiveDefiniteMatrixException.
CASES: MATH-274 regression matrix should throw NotPositiveDefiniteMatrixException.
CASES: Positive-definite symmetric input should construct and expose valid L/LT/determinant/solver behavior.
RISKS: Exact MATH-274 matrix and existing test source are not provided; avoid inventing numeric expectations.
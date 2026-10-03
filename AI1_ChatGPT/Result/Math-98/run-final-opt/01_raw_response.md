TARGETS: BigMatrixImpl.luDecompose and RealMatrixImpl.luDecompose on non-square matrices.
ORACLES: MATH-209 triggers require avoiding ArrayIndexOutOfBoundsException: 2.
ORACLES: InvalidMatrixException is the declared LU-related failure type.
CASES: Tall 3x2 matrices (index 2 exceeds column count) for both numeric representations.
CASES: Square matrices should still LU-decompose without regression.
RISKS: LU-dependent public operations may expose the same rectangular-matrix failure.
RISKS: Context lacks full trigger inputs and complete API bodies; do not infer exact messages.
TARGETS: EigenDecompositionImpl decomposition/eigenvalue computation for symmetric matrices (testMath308 path).
ORACLES: Existing trigger expects construction/decomposition to avoid ArrayIndexOutOfBoundsException(-1).
ORACLES: Public getRealEigenvalues/getD/getV results are available only as API-level observable outputs.
CASES: Reproduce testMath308 matrix input; assert decomposition completes and public results are obtainable.
CASES: Exercise small symmetric blocks (1, 2, 3 rows) and general-block processing boundaries.
CASES: Include split/deflation boundary patterns that may reach negative internal indices.
RISKS: Exact matrix data and intended numerical expected values from testMath308 are not provided.
RISKS: Do not assert undocumented ordering, tolerance, or non-symmetric-matrix behavior.
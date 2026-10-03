TARGETS: multiply(BigMatrix)/multiply(BigMatrixImpl), multiply(RealMatrix)/multiply(RealMatrixImpl)
ORACLES: Dimension mismatch => IllegalArgumentException; compatible dimensions => correct matrix
product per algebra rules
CASES: 2×3 by 4×2 (error, trig AIOOBE), 3×2 by 2×3 (ok), 1×1 by 1×1, zero-row/col if supported
RISKS: Unknown exact message; may affect solve/preMultiply; must test both BigMatrixImpl and
RealMatrixImpl
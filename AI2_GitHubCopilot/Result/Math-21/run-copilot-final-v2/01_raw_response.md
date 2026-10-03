TARGETS: RectangularCholeskyDecomposition(RealMatrix,double), getRootMatrix(), getRank()
ORACLES: For full rank, norm(A-R^T R) < 1e-12; rank = count of processed rows (diag > small)
CASES: Full-rank (identity, random PD), rank-deficient (zero singular), threshold at drop, small=0,
small=large, duplicate rows, zero matrix, m<n
RISKS: Exact rank for near-singular unknown; zero-diag with small=0 may cause NaN; only MATH-789
test confirms expected fix behaviour
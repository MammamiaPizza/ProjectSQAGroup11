TARGETS: getSolver().solve(), getSingularValues(), getU(), getS(), getV(), getRank()
ORACLES: A≈U S V^T, U^T U=I, V^T V=I, singular values nonnegative & sorted descending
ORACLES: solve(b) residual ||Ax - b|| should be ~0 for consistent systems
ORACLES: compare with known SVD from a trusted implementation (e.g., JAMA, manual)
CASES: square nonsingular, rectangular (tall/wide), rank-deficient, nearly singular
CASES: matrix with repeated eigenvalues, zero eigenvalues, large dynamic range
CASES: boundary: matrix of all zeros, identity, ill-conditioned (Hilbert-like)
RISKS: cannot use another implementation version; must rely solely on mathematical properties
RISKS: floating-point tolerance needed; exact zero may be impossible for residual norm
RISKS: bug may only surface in specific matrix structures not covered by simple examples

That's 10 lines if we count them. But ensure <=120 chars per line. Let's check length.

Line1: TARGETS: getSolver().solve(), getSingularValues(), getU(), getS(), getV(), getRank()
That's about 80 chars. Good.

Line2: ORACLES: A≈U S V^T, U^T U=I, V^T V=I, singular values nonnegative & sorted descending
Length: ORACLES: ... that's maybe 100 chars. Check: "A≈U S V^T, U^T U=I, V^T V=I, singular values
nonnegative & sorted descending" maybe 90 chars. Fine.

Line3: ORACLES: solve(b) residual ||Ax - b|| should be ~0 for consistent systems
Maybe 75 chars.

Line4: ORACLES: compare with known SVD from a trusted implementation (e.g., JAMA, manual)
About 90.

Line5: CASES: square nonsingular, rectangular (tall/wide), rank-deficient, nearly singular
~80.

Line6: CASES: matrix with repeated eigenvalues, zero eigenvalues, large dynamic range
~80.

Line7: CASES: boundary: matrix of all zeros, identity, ill-conditioned (Hilbert-like)
~75.

Line8: RISKS: cannot use another implementation version; must rely solely on mathematical properties
~90.

Line9: RISKS: floating-point tolerance needed; exact zero may be impossible for residual norm
~85.

Line10: RISKS: bug may only surface in specific matrix structures not covered by simple examples
~85.

All fit. Deliver in final.TARGETS: getSolver().solve(), getSingularValues(), getU(), getS(), getV(),
getRank()
ORACLES: A≈U S V^T, U^T U=I, V^T V=I, singular values nonnegative & sorted descending
ORACLES: solve(b) residual ||Ax - b|| should be ~0 for consistent systems
ORACLES: compare with known SVD from trusted implementation (e.g., JAMA, manual)
CASES: square nonsingular, rectangular (tall/wide), rank-deficient, nearly singular
CASES: matrix with repeated eigenvalues, zero eigenvalues, large dynamic range
CASES: boundary: matrix of all zeros, identity, ill-conditioned (Hilbert-like)
RISKS: cannot use another implementation version; must rely solely on mathematical properties
RISKS: floating-point tolerance needed; exact zero may be impossible for residual norm
RISKS: bug may only surface in specific matrix structures not covered by simple examples
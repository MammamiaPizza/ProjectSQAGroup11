TARGETS: EigenDecompositionImpl eigenvalue/vector computation exposed by getRealEigenvalue(s), getV/VT/D, determinant.
ORACLES: Existing trigger testMathpbx02 expected eigenvalue 16828.208208485466, not 20654.74511575945.
ORACLES: Decomposition identities A*V≈V*D and V*VT≈I are API-derived numerical checks.
CASES: Reproduce trigger matrix/input from EigenDecompositionImplTest::testMathpbx02 if accessible.
CASES: Symmetric normal matrices; compare returned eigenvalue array, D diagonal, and determinant product.
CASES: 1x1, 2x2, 3x3 blocks exercise specialized block processing paths.
CASES: Repeated/close eigenvalues and split-prone tridiagonal inputs stress splitting and range counting.
RISKS: Eigenvalue ordering is not specified here; avoid assuming an order beyond existing test evidence.
RISKS: Numerical assertions need tolerances; constructor signatures and trigger matrix are absent from context.
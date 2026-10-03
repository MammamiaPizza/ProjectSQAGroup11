TARGETS: SimplexTableau.getSolution(), getBasicRow(), and unrestricted-variable solution reconstruction.
ORACLES: Existing SimplexSolverTest::testMath713NegativeVariable assertion.
CASES: Solve the trigger LP with a negative decision-variable optimum; verify returned point/value.
CASES: Unrestricted-variable case where the negative-variable column is basic; verify RHS-based shift.
CASES: Unrestricted-variable case where that column is nonbasic; verify no unintended objective-RHS shift.
CASES: Nonnegative-restricted solve as regression; verify ordinary basic decision-variable extraction.
RISKS: Tableau is package-private; tests may need same package or exercise it through SimplexSolver.
RISKS: No separate specification beyond the trigger is provided; avoid assumptions about unlisted behavior.
TARGETS: decompose(); getRealEigenvalues(); getEigenvalue(i); getDeterminant(); getEigenvector(i);
getV(); getD(); getVT()
ORACLES: trace(A) ≈ sum(realEigenvalues); determinant(A) ≈ product(realEigenvalues)
ORACLES: For each eigenpair: Av ≈ lambdav; spectral decomposition: A ≈ VDVT
CASES: normal: 2×2, 3×3 symmetric integer-eigenvalue matrices
CASES: boundary: repeated eigenvalues, very close eigenvalues, eigenvalues near zero
CASES: error: non-symmetric matrix → verify isSymmetric check throws expected exception
RISKS: internal findEigenvalues/goodStep/initialSplits logic may mis-count eigenvalues, shifting
wrong
RISKS: testMathpbx02 matrix values unknown; tolerance-sensitive; must use matrices that stress
eigenvalue-range estimation
RISKS: no access to expected eigenvalues from a correct version; rely on mathematical invariants as
partial oracles
TARGETS: EigenDecompositionImpl constructor→ getRealEigenvalues for symmetric matrices triggering
split logic (findEigenvalues, computeSplits, eigenvaluesRange)
ORACLES: V * D * V^T ≈ original matrix; eigenvalues non-increasing; V columns orthonormal (V^TV = I)
ORACLES: ArrayIndexOOBException: -1 in testMath308 implies -1 index from split/range counting; test
boundaries in eigenvaluesRange/countEigenValues
CASES: 3x3 matrix with close diagonal values (e.g., [1,2,2]); 4x4 with three equal stable eigenvalue
count boundaries
CASES: Matrix that triggers process3RowsBlock: 3 continuous rows with similar eigenvalues; border
indices splitting with 1 or 2 blocks
CASES: Degenerate case: 1x1 identity leads to 1 split; 2x2 with equal diagonal causes dqds
degenerate; n=0 splits = empty list
RISKS: Internal private methods not directly testable; only public getRealEigenvalues/getEigenvector
can invoke decompose and reveal -1 OOB
RISKS: Cannot see testMath308 input; must design matrices that create eigenvalue ranges near
splitTolerance to exercise boundary loops
RISKS: Assumes symmetric matrix; isSymmetric check permits only exactly symmetric; non-square matrix
throws; no guidance for non-real eigenvalues
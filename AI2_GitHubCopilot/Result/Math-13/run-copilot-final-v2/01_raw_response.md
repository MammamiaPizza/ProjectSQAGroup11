TARGETS: getCovariances(), getCovariances(threshold), computeWeightedJacobian,
guessParametersErrors, squareRoot.
TARGETS: updateJacobian, updateResidualsAndCost, computeResiduals (affect dimension fields
rows,cols).
ORACLES: Indirect oracle: PolynomialFitterTest.testLargeSample should not OOM; cov matrix positive
semidefinite; chi2>=0.
ORACLES: Memory oracle: after optimization check Runtime.freeMemory() change < X MB for bounded
input size.
CASES: Normal: small fit (order=1, 10 points) passes; Boundary: order=100 with 10k points should
complete.
CASES: Error: negative or zero threshold → expected NumericalIssue?; threshold=0 → possibly singular
matrix.
CASES: Check guessParametersErrors returns array with non-negative lengths; verify RMS matches
residuals.
CASES: Use PolynomialFitter with large random sample to reproduce OOM; assert no exception.
RISKS: Without the fix, OOM might abort test; need @Test(expected=OutOfMemoryError?) but better to
catch Throwable.
RISKS: Large-case memory limits are brittle; CI may have different heap; consider skipping if
insufficient memory detected.
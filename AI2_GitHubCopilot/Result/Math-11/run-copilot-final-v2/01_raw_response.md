TARGETS: density, getExponentTerm, getStandardDeviations, sample, constructor EigenDecomposition.
ORACLES: expected density from multivariate normal PDF formula (manual calc, scipy, or fixed Apache
Commons Math code).
CASES: univariate N(0,1) density at x=1 (approx 0.24197); bivariate identity cov at (0.5,-0.5)
density≈0.0585.
CASES: bivariate diagonal cov diag(2,3) at (1,2); full cov [[1,0.5],[0.5,1]] at (0,0) density≈0.183.
CASES: near-zero variance (1e-10) causing near-singular; dimension mismatch throws
DimensionMismatchException.
CASES: getStandardDeviations returns sqrt of diag entries; sample generates plausible values
(mean/cov check).
RISKS: only partial API visible; expected density needs external computation; bug likely in exponent
term or covariance factor.
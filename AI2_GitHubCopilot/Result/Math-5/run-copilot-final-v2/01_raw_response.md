TARGETS: Complex.reciprocal() (zero case), Complex.divide(Complex) if it delegates to reciprocal
ORACLES: reciprocal(ZERO) expected (NaN,NaN) per failing testReciprocalZero
ORACLES: For non-zero (a,b): expected (a/(a²+b²), -b/(a²+b²)) using mathematical formula
ORACLES: Complex.NaN/INF constants as expected for NaN/INF inputs
CASES: reciprocal(ZERO) -> must be (NaN,NaN) (the reported bug trigger)
CASES: reciprocal(0+1i) -> (0,-1i); reciprocal(2+0i) -> (0.5,0); reciprocal(1+1i) -> (0.5,-0.5)
CASES: reciprocal(Complex.INF) -> likely (0,0); reciprocal(Complex.NaN) -> NaN
CASES: reciprocal(Double.MAX_VALUE,0) -> (1/MAX,0) boundary; reciprocal(0,Double.MIN_VALUE) ->
(0,-1/MIN) borderline
RISKS: Only buggy version; expected for zero from test name not spec; divide by zero may similarly
return INF
RISKS: No reference implementation to validate general behavior; limited to flawed codebase snapshot
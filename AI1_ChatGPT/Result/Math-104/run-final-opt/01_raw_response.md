TARGETS: Gamma.regularizedGammaP(a,x) for positive a,x; possibly Q if P/Q share implementation.
ORACLES: Existing GammaTest trigger expected P(1,1)=0.632120558828558.
CASES: Positive normal inputs including (1,1); assert precision sufficient to expose 6.36e-11 error.
CASES: Boundary/error coverage only where existing API tests define behavior; no stated invalid-input contract.
RISKS: Context lacks implementation details, tolerances, and expected values beyond the trigger.
RISKS: Do not infer expected Q, logGamma, or protected getA/getB behavior from another version.
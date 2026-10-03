TARGETS: cumulativeProbability, upperCumulativeProbability, probability; support/domain bounds for extreme x  
ORACLES: Trigger testMath1021 failure (sample=-50); distribution support and probability/cumulative invariants  
CASES: population/success/sample values yielding support away from 0; x far below support (e.g., -50)  
CASES: x at lower/upper support and adjacent values; compare cumulative/upper-tail boundary results  
ORACLES: probability outside support is 0; cumulative below support is 0 and above support is 1  
RISKS: Exact constructor validation behavior and MATH-1021 expected numeric values are not provided
TARGETS: CoreOperationCompare.equal(Object,Object)
TARGETS: CoreOperationEqual.computeValue(EvalContext)
TARGETS: CoreOperationNotEqual.computeValue(EvalContext)
ORACLES: NaN != NaN (IEEE 754): = must return false, != must return true
ORACLES: Non‑NaN values compare via equals() with symmetric & null checks
CASES: both NaN; NaN+nonNaN; equal numbers; equal strings; different values
CASES: both null; one null; infinities; 0.0 vs -0.0; mixed types; same object
RISKS: Only NaN fix is known; findMatch/contains may also need NaN‑awareness
RISKS: BigDecimal/BigInteger equality semantics via equals() may be affected
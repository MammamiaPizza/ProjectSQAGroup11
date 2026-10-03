TARGETS: compute(Object,Object), evaluateCompare(int), computeValue(EvalContext)
ORACLES: IEEE 754: any relational op with NaN returns false; JXPATH-95 expected false for $nan >
$nan
ORACLES: Java Double.compare semantics with NaN; Double.NaN>Double.NaN yields false
CASES: NaN > NaN, NaN >= NaN, NaN < NaN, NaN <= NaN (all expect false)
CASES: NaN > 0, 0 > NaN, NaN > ∞, ∞ > NaN (expect false)
CASES: baseline 1 > 0→true, 0 > 1→false, 1 > 1→false; ensure no regression
CASES: reduce(NaN) may affect comparison; check compute with NaN and null/other types
CASES: findMatch with iterators containing NaN; containsMatch with NaN element
RISKS: abstract class; need concrete subclass to call evaluateCompare; implementation may cast int
to boolean wrongly
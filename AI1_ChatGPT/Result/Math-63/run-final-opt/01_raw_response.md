TARGETS: MathUtils.equals(double[],double[]) and equalsIncludingNaN(double[],double[]).
ORACLES: Trigger testArrayEquals and documented distinction between ordinary and NaN-inclusive equality.
CASES: Equal arrays; same length with one unequal value; different lengths; empty arrays.
CASES: NaN at matching positions: false for equals, true for equalsIncludingNaN.
CASES: Matching signed zeros and infinities; null-array behavior only if existing tests establish it.
RISKS: Context omits method bodies/Javadoc and exact null semantics; do not infer unspecified behavior.
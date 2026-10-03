TARGETS: ExtendedMessageFormat.equals(Object) and hashCode() with registry Map
ORACLES: equals() must be reflexive, symmetric, transitive, consistent with hashCode
ORACLES: Registry equality: map content equality (same keys, FormatFactory values equal by
.equals())
CASES: Two instances same pattern/locale/registry (identical key-value factory pairs) → equal/hash
match
CASES: Instance with registry vs instance without registry → not equal
CASES: Same registry content but different Map implementations/orderings → should be equal (bug
area)
CASES: Null registry → not equal to non-null registry; two null registries equal
RISKS: Bug likely causes hash based on Map identity not content; triggering test failure on
"registry, hashcode()"
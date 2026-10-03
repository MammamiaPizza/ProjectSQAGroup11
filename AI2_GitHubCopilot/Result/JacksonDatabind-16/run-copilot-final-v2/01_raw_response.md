TARGETS: AnnotationMap.merge
TARGETS: AnnotationMap.add
TARGETS: AnnotationMap._add
ORACLES: Failing test expected "bar" but got "stuff"; secondary mixin annotations should override
primary.
ORACLES: Behavior implied by typical Jackson mixin precedence.
CASES: merge with same Annotation key in both maps (secondary overrides primary).
CASES: merge with one empty/null map, merge with disjoint annotation sets.
CASES: Add same annotation class twice, addIfNotPresent vs add.
RISKS: merge and _add semantics not fully documented; only bug report hints at intended behavior.
RISKS: No access to fixed version; existing test failure may not cover all edge cases.
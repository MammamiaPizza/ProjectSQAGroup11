TARGETS: setReference(), getSelfReferencedType(), toString(), getGenericSignature(),
getErasedSignature()
ORACLES: Bug report #1647: serialized JSON must include base properties when sub-type loaded after
base type
CASES: Normal: setReference before serializing yields full base+sub output
CASES: Boundary: lazy resolution where _referencedType is null initially, then later set
CASES: Error: null _referencedType causes missing base properties (the bug)
CASES: Parallel: test equals/hashCode with self-referenced type after setReference
RISKS: Fix unknown; must rely on test failures as oracle; no source for recursive type resolution
context
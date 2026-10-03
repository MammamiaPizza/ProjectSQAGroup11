TARGETS: RecordType.isSubtype, ArrowType.isSubtype, FunctionType.isSubtype, UnionType.isSubtype,
EquivalenceMethod methods
ORACLES: TypeCheckTest::testIssue791 expects no type warnings;
RecordTypeTest::testSubtypeWithUnknowns2 asserts expected subtype booleans
CASES: Record(unknown props) vs Function; Arrow/union subtypes with unknown; normal records;
empty/any type boundaries
RISKS: Exact bug logic unknown; no access to patch or full source; EquivalenceMethod signatures not
provided; behavior must be inferred from trigger tests
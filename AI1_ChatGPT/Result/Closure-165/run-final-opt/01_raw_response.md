TARGETS: RecordType.isSubtype(ObjectType,RecordType) and RecordType.isEquivalentTo(JSType).
TARGETS: RecordTypeBuilder.build and duplicate-record-field reporting path.
TARGETS: JSTypeRegistry/ObjectType property and type-resolution behavior affected by Issue725.
ORACLES: Existing TypeCheckTest.testIssue725 expects a warning.
ORACLES: Record subtype/equivalence results and builder-reported duplicate-field diagnostic.
CASES: Record with required property vs object/record lacking that property; expect non-subtype/warning.
CASES: Matching property names/types; verify subtype/equivalence normal behavior.
CASES: Duplicate record field through builder; verify documented "Duplicate record field" report.
RISKS: Truncated APIs and absent Issue725 source limit exact construction and diagnostic assertions.
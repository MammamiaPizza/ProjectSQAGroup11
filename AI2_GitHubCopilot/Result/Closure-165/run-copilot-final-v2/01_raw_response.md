TARGETS: RecordTypeBuilder.build(), RecordType.isSubtype(),
JSTypeRegistry.createAnonymousObjectType(), ObjectType.getOwnSlot(), ObjectType.isSubtype()
ORACLES: TypeCheckTest expected warnings (e.g., type mismatch via isSubtype); structural typing
rules between RecordType and ObjectType
CASES: Anonymous-object vs record-type structural equivalence; duplicate record-field detection;
null/non-nullable field subtyping; empty-record boundaries
RISKS: Exact isSubtype rules (covariance/invariance) unknown without modified source; trigger test
may exercise type-check integration, not unit-level APIs
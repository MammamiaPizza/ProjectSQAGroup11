TARGETS: JacksonAnnotationIntrospector._findObjectIdInfo propagation of alwaysAsId from
@JsonIdentityInfo to ObjectIdInfo
TARGETS: ObjectIdInfo.withAlwaysAsId / _alwaysAsId field used during serialization decisions
TARGETS: BeanSerializerBase serialization path that checks _objectIdWriter._alwaysAsId before
writing full object
ORACLES: AlwaysAsReferenceFirstTest::testIssue1607 expects {"alwaysClass":[1],"alwaysProp":2} when
alwaysAsId=true on class
ORACLES: AlwaysAsReferenceFirstTest asserts top-level alwaysClass list elements are id-only, not
full objects with id/value
CASES: Class-level @JsonIdentityInfo with alwaysAsId=true → list elements serialized as IDs only
CASES: Property-level @JsonIdentityInfo with alwaysAsId=true → property serialized as ID (already
works per test output)
CASES: alwaysAsId=false (default) → standard serialization with full object on first occurrence, id
on subsequent
RISKS: Old deprecated constructors (2.3/2.4) may not accept alwaysAsId, backcompat needed
RISKS: Only one trigger test exists; undocumented edge cases for mixed class/property level
alwaysAsId settings
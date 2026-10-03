TARGETS: writeAsId decides id/reference vs full object write using idWritten flag
TARGETS: generateId creates id, sets idWritten=false initially
ORACLES: Roundtrip deserialization must not throw UnresolvedForwardReference
ORACLES: AlwaysAsReferenceFirst: first occurrence as id, then full object definition
CASES: Normal: two objects with mutual reference; first serialized as id, then full
CASES: Boundary: idWritten=true, writeAsId writes reference-only
CASES: Boundary: idWritten=false, writeAsId writes full object and sets flag
CASES: Error: writeAsId with null id (not generated) may NPE or misbehavior
RISKS: Cannot see ObjectIdWriter.write or deserialization; limited to serialization class only
TARGETS: DefaultDeserializationContext.findObjectId must not throw NPE on null id
TARGETS: ObjectIdValueProperty.set/setAndReturn must handle null object id values
ORACLES: No NullPointerException when deserializing null object-id (testNullObjectId passes)
ORACLES: Null id should resolve to a distinct identity (likely a non-null entry in _objectIds)
CASES: Single null id in JSON, multiple null ids (all refer to same resolved object)
CASES: Mix of null and non-null ids in a single deserialization payload
CASES: Forward reference with null id (may need separate handling)
RISKS: Expected behavior for null ids is not fully specified; must infer from test name + NPE
RISKS: Object identity resolution for null may differ from other ids; could introduce dupes
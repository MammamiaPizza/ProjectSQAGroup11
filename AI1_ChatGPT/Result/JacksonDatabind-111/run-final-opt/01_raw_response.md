TARGETS: AtomicReferenceDeserializer null/empty/reference/update behavior in nested deserialization.
TARGETS: Creator/Field/Method/ObjectId/Setterless property null-provider and set/setAndReturn paths.
ORACLES: Trigger test JDKAtomicTypesDeserTest::testNullWithinNested assertion.
ORACLES: Public APIs: getNullValue, getEmptyValue, referenceValue, getReferenced, updateReference.
CASES: Deserialize nested AtomicReference with explicit JSON null; assert nested reference/result state.
CASES: Nested non-null value; distinguish wrapper presence from referenced content.
CASES: Update existing AtomicReference with null and non-null contents.
CASES: Property assignment with null provider that skips null versus supplies a null value.
RISKS: Available context omits constructors/full deserialization wiring and exact trigger assertion.
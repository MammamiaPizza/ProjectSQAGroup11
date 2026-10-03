TARGETS: withNullProvider(NullValueProvider) in CreatorProperty, FieldProperty, MethodProperty,
SetterlessProperty, ObjectIdValue/ReferenceProperty; _skipNulls in set/setAndReturn.
TARGETS: AtomicReferenceDeserializer.getNullValue(), updateReference(), referenceValue() handling of
nested null; _skipNulls propagation.
ORACLES: JDKAtomicTypesDeserTest.testNullWithinNested must pass; Jackson API: NullValueProvider
should allow/disallow null set; _skipNulls=true skips null values.
ORACLES: AtomicReferenceDeserializer contract: getNullValue returns null-reference, updateReference
handles null contents, referenceValue unwraps.
CASES: Normal: deserialize nested non-null value into AtomicReference field; Boundary: nested null
JSON for AtomicReference field; missing field.
CASES: Error/boundary: top-level null for AtomicReference; null inside nested AtomicReference after
skipNulls; incompatible null for primitive-typed property.
RISKS: Truncated signatures obscure full method contracts; ObjectId*Property interaction may require
broader testing; reliance on trigger test for expected behavior.
TARGETS: CollectionDeserializer.deserialize(), _delegateDeserializer, _valueInstantiator, and
constructor resolution.
ORACLES: Deserialization of Collections$UnmodifiableSet must succeed without "No default
constructor" IllegalStateException.
ORACLES: Test expects a valid Object after deserialization; no exception thrown for unmodifiable
wrappers.
CASES: Normal: mutable collections (ArrayList, HashSet) deserialize correctly.
CASES: Boundary: empty unmodifiable set, single-element, multiple elements, nested unmodifiable.
CASES: Error: UnmodifiableSet, UnmodifiableList, UnmodifiableSortedSet, any collection lacking
default constructor.
RISKS: Fix may alter instantiation path for all non-default-constructible collections; could break
other deserializers.
RISKS: Potential regression for custom collection types that rely on default-constructor fallback;
limited test coverage for edge cases.
RISKS: Context missing full deserialization flow for delegate/builder-based creation; may need
deeper analysis.
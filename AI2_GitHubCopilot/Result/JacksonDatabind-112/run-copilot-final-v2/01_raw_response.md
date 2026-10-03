TARGETS: StringCollectionDeserializer.deserialize(JsonParser,DeserializationContext) – how it
creates the collection instance.
TARGETS: ValueInstantiator delegation (array/collection-based creator) vs default constructor
selection.
ORACLES: DelegatingArrayCreator2324Test expects no MismatchedInputException for ImmutableBag from
string-array JSON.
ORACLES: Successful deserialization of JSON array into a collection type that has only a delegating
creator.
CASES: Normal: ["a","b"] → ImmutableBag; Empty: [] → empty ImmutableBag; Missing delegating creator
→ throws exception.
CASES: Single-element array; large array; array with null values (if allowed by value deserializer).
RISKS: _valueInstantiator may have multiple creator types; must prefer delegation when no default
constructor.
RISKS: Fix must not break default-constructor collections (e.g., ArrayList, HashSet).
RISKS: Interaction with _valueDeserializer for non-String content types may hide instantiation
logic.
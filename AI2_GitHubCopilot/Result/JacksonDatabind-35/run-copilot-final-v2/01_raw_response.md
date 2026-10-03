TARGETS: AsWrapperTypeDeserializer._deserialize – parser token handling when not at START_OBJECT.
TARGETS: deserializeTypedFromObject – wrapper+object-id interaction; after identity resolution.
ORACLES: JSON that triggers the bug yields no JsonMappingException;
WrapperObjectWithObjectIdTest::testSimple passes.
CASES: JSON with @JsonIdentityInfo+WRAPPER_OBJECT typing; second occurrence of same object (parser
not at START_OBJECT).
CASES: Wrapper JSON containing "@type" id; deserialization when parser points to a FIELD_NAME (the
bug trigger).
CASES: Missing type-id in wrapper; empty wrapper object; nested wrapper objects.
RISKS: Exact internal parser state (e.g., token after object-id handling) unknown; fix may change
_deserialize advancement.
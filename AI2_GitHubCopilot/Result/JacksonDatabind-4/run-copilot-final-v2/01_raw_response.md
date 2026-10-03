TARGETS: StringArrayDeserializer.deserialize() & _deserializeCustom(); index tracking when element
is unexpected type.
ORACLES: Exception thrown for second array element must report index=1 (0‑based), as expected by
testArrayIndexForExceptions.
CASES: ["valid", 123] → error at index 1; empty array; single valid element; first element invalid
(index 0); all valid strings.
RISKS: Verify indexing for custom element deserializer, non‑scalar tokens (start‑object), and
handleNonArray; regression on normal arrays.
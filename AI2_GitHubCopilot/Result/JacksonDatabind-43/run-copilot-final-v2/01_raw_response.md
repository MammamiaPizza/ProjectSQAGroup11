TARGETS: ObjectIdValueProperty.set() and setAndReturn() with null (VALUE_NULL token) for
String-typed id.
ORACLES: Jackson docs for scalar String deserialization: null token should yield null (no
exception). Check ObjectIdReader for explicit null-handling.
CASES: null value (key), non-null String, empty String, unicode String, boundary-length String,
wrong type (e.g., numeric token).
RISKS: Precise repair behavior (null-setting vs. skip) unclear from summary; ObjectIdReader
internals may influence; only inferred fix available.
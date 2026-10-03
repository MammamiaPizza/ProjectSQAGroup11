TARGETS: ReturnsDeepStubs class must implement Serializable; its inner anonymous
GenericMetadataSupport classes must be serializable.
ORACLES: Serialization round-trip of mock with RETURNS_DEEP_STUBS does not throw
NotSerializableException.
ORACLES: After deserialization, invoking a method returns a non-null deep stub (not raw null).
CASES: Normal: mock created with Mockito.mock(Some.class, Mockito.RETURNS_DEEP_STUBS); serialize;
deserialize; call method; assert non-null return.
CASES: Boundary: mock with generic return type; after deserialization, deep stub must respect
generic type (e.g., List<String>).
RISKS: Fix may change internal fields (mockitoCore, delegate) to transient; test should not directly
access them.
RISKS: Invocation history may be lost after deserialization; test should verify only deep stub
creation, not invocation recording.
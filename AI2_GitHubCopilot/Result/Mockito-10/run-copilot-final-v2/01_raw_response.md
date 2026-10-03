TARGETS: ReturnsDeepStubs.answer(InvocationOnMock) deep-stub path;
deepStub/newDeepStubMock/withSettingsUsing.
TARGETS: recordDeepStubAnswer and returnsDeepStubsAnswerUsing wiring; actualParameterizedType
generic metadata.
TARGETS: ReturnsDeepStubsSerializationFallback.writeReplace fallback behavior for deep-stub mocks.
ORACLES: Trigger test name/method expects no MockitoException when accessing a deep stub.
ORACLES: Expected observable result: deep-stub property access returns a usable mock without
serialization error.
CASES: Normal: mock with RETURNS_DEEP_STUBS, call method returning non-serializable type, assert
non-null.
CASES: Boundary: nested deep-stub chain and generic-return metadata paths.
CASES: Error: serializable deep-stub mock attempting serialization should use fallback, not fail on
access.
RISKS: Only signatures are provided; no bodies/docs, so exact exception trigger is inferred.
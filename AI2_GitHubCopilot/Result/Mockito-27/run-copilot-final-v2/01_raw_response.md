TARGETS: resetMock(T mock) preservation of invocation listeners set in MockSettingsImpl.
TARGETS: createMock(Class<T>, MockSettingsImpl) with listener settings.
ORACLES: After resetMock, listener should still be notified on mock invocations (as per test
expectation).
CASES: Normal: reset mock with listener and verify listener fires on method call.
CASES: Boundary: reset mock multiple times; listener still fires each time.
CASES: Boundary: reset mock with no listeners; no error.
CASES: Error: resetMock(null) throws exception; resetMock(non-mock) throws.
RISKS: Listener attachment mechanism unclear; reset may replace interceptor/handler losing
listeners.
RISKS: Limited view: only MockUtil class known, not MockHandlerInterface or interceptor.
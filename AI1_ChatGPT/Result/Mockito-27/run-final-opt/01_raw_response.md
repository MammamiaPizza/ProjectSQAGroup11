TARGETS: MockUtil.resetMock(T) listener preservation on reset; related getMockHandler/getInterceptor behavior.
ORACLES: Trigger ListenersLostOnResetMockTest::listener assertion; MockUtil behavior and available signatures only.
CASES: Reset a mock configured with listeners, then invoke/reset-observable behavior to verify listeners remain active.
CASES: Normal reset of a Mockito mock; verify handler/interceptor remains obtainable after reset.
CASES: Boundary/error inputs only if supported by existing test context; null/non-mock expectations are unspecified.
RISKS: Listener APIs, invocation expectations, and exact listener callback semantics are not provided.
RISKS: Do not infer behavior from another Mockito version or invent MockSettingsImpl configuration APIs.
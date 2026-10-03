TARGETS: MockitoCore.when(T) returns OngoingStubbing; must accept a mock configured with
RETURNS_MOCKS
ORACLES: org.mockito.Mockito.RETURNS_MOCKS; mockingProgress; MockSettings; OngoingStubbing; MockUtil
CASES: stub mock with Mockito.mock(Class, RETURNS_MOCKS); call when() on that mock; normal, null
boundary
RISKS: MissingMethodInvocationException when RETURNS_MOCKS mock intercepts when() argument
evaluation; limited internals visible
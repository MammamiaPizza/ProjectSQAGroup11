TARGETS: MockHandler.handle(Invocation) and MockitoCore.verify(T, VerificationMode) interaction state handling.  
TARGETS: MockitoCore.getLastInvocation(), validateMockitoUsage(), and mocking-progress state may affect verification.  
ORACLES: Trigger test should complete without AssertionFailedError when another mock call shares the verify line.  
ORACLES: Existing Mockito verification semantics and public API behavior are the expected-result source.  
CASES: Verify an invocation on mock A while an extra invocation on mock B occurs in the same statement/line.  
CASES: Normal verify on one mock; verify after prior calls on another mock; ensure intended invocation is verified.  
CASES: Boundary: extra call before versus after verify argument evaluation, with distinct mocks.  
RISKS: Java argument evaluation/order and global/thread-local mocking progress may retain the wrong last invocation.  
RISKS: Context lacks method bodies and exact trigger source; avoid assuming unsupported verification/error details.
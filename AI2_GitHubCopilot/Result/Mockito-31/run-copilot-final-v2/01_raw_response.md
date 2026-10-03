TARGETS: ThrowingInterceptor captures invocation.getArguments() array from InvocationOnMock
TARGETS: formatMethodCall() builds string including arguments via join; verify arg insertion
ORACLES: Assert exception message (from formatMethodCall) contains "withArgs([" + argString + "])"
CASES: No arguments → message includes "withArgs([])"; single String arg → "withArgs([foo])"
CASES: Multiple args (String, int, null) → "withArgs([a, 2, null])"; primitive double arg →
"withArgs([3.14])"
CASES: Many args (e.g., 50) → message contains full arg list; boundary: args after smart null
creation unchanged
RISKS: Validates only string format; does not verify actual method invocation behavior
RISKS: Requires CGLIB proxy and mock context; formatMethodCall triggered only on smart null
interaction
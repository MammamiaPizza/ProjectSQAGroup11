TARGETS: Invocation.callRealMethod() for interface methods lacking a concrete real implementation.
ORACLES: Trigger tests expect a runtime failure, not NPE or NoSuchMethodError.
CASES: Call real method on an interface mock via Invocation; assert failure occurs at runtime.
CASES: Cover interface method with arguments, preserving method resolution and argument handling.
RISKS: RealMethod/MockitoMethod setup details are unavailable; test should use existing Mockito test patterns.
RISKS: No fixed exception class/message is specified beyond runtime failure.
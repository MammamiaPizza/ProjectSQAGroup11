TARGETS: ReturnsDeepStubs.answer/getMock generic return-type resolution and nested deep-stub mock creation.  
ORACLES: Trigger expectations: compatible deep mocks or default values; no ClassCastException on invocation chains.  
CASES: Nested generic resolving to non-mockable String returns its default value, not an incompatible mock.  
CASES: Type variables with multiple bounds yield mocks assignable to all required bounds (Comparable and Cloneable).  
CASES: Bounded wildcard generic return resolves to a mock compatible with the declared bound (e.g., Map.Entry).  
CASES: Parameterized returns and chained generic deep calls preserve resolved class/type-variable bindings.  
RISKS: Only abbreviated implementation/signatures are available; derive assertions from listed trigger behavior only.
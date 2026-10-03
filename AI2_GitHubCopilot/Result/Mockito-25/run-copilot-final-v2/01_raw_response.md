TARGETS: ReturnsDeepStubs.answer, .getMock, .recordDeepStubMock that create deep stubs for generic
return types.

ORACLES: Mock must be castable to all upper bounds of type variables (e.g., Comparable & Cloneable);
non-mockable nested generics return default value without ClassCastException.

CASES: Return type is type var with multiple bounds (Comparable & Cloneable); bounded wildcard;
parameterized type referencing class typevar; nested generic like Map.Entry<String,String>; chained
generic calls.

RISKS: Mock gets created with only first bound, not all; generic metadata resolution may miss extra
bounds; non-mockable types are incorrectly mocked.
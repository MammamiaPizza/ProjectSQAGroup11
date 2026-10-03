TARGETS: Matchers.anyInt(), any(), eq(T), and same(T) in primitive-int/Integer matcher call paths.  
ORACLES: Four named trigger tests; expected behavior is no NullPointerException.  
CASES: Mock method accepting int invoked/stubbed/verified with anyInt() and Integer-compatible any().  
CASES: Integer argument paths using eq(Integer) and same(Integer), including unboxing-sensitive invocation.  
RISKS: Matcher return values may be null and auto-unboxing can cause NPE before Mockito records the matcher.  
RISKS: Context gives only Matchers signatures and trigger outcomes; no method bodies or broader semantics.
TARGETS: Dfp.nextAfter(Dfp), especially transitions involving zero, sign, and infinities.  
ORACLES: Existing DfpTest::testIssue567 expects 0.0 rather than -Infinity.  
CASES: nextAfter from negative finite values toward zero; assert returned Dfp string/value is 0.0.  
CASES: Boundary near zero and direction changes around signed zero, if constructible through public factories.  
CASES: nextAfter with infinite operands/directions; verify only established API classification behavior.  
RISKS: Constructors with DfpField are protected; tests need existing field/test infrastructure.  
RISKS: No fixed-version comparison or full MATH-567 specification is available.
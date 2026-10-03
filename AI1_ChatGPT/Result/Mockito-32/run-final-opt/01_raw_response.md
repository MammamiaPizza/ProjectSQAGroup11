TARGETS: SpyAnnotationEngine.createMockFor(Annotation, Field); process(Class<?>, Object) for @Spy fields  
ORACLES: Trigger SpyShouldHaveNiceNameTest::shouldPrintNiceName assertion message/name expectation  
CASES: @Spy field creation through process should produce spy whose displayed name reflects the field name  
CASES: createMockFor with @Spy field; verify generated spy identity/name in failure or string output  
RISKS: Annotation conflicts are rejected by assertNoAnnotations for @Mock, legacy Mock, or @Captor  
RISKS: Context lacks full trigger assertion text and implementation details; derive expectations only from trigger test
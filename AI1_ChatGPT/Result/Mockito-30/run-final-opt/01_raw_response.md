TARGETS: ReturnsSmartNulls.answer and ThrowingInterceptor intercept/formatMethodCall for unstubbed smart-null calls  
TARGETS: Reporter behavior used to create the SmartNullPointerException message  
ORACLES: Trigger specifies exception message includes both invocation parameters "oompa" and "lumpa"  
CASES: Smart-null return followed by method call with multiple string parameters  
CASES: Normal unstubbed invocation returning empty/default values remains delegated to ReturnsMoreEmptyValues  
CASES: Boundary parameter formatting for zero, one, and multiple arguments if reachable through smart-null calls  
RISKS: Available context omits Reporter method signatures/message text and ReturnsSmartNulls implementation details  
RISKS: Do not assert full message wording beyond specified parameter inclusion
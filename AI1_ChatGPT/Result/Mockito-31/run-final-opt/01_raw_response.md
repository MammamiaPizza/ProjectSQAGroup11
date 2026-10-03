TARGETS: ReturnsSmartNulls.answer(InvocationOnMock) and smart-null ThrowingInterceptor.intercept/formatMethodCall  
ORACLES: Trigger expects smart-null error text to include unstubbed method arguments: withArgs([oompa, lumpa])  
ORACLES: Expected text source is InvocationOnMock method/arguments and mock identity; no alternate version available  
CASES: Unstubbed non-empty return yielding smart null; invoke method with two args and assert both appear in message  
CASES: Existing empty-argument method behavior should retain [] formatting in smart-null diagnostics  
CASES: Verify normal delegate defaults remain returned where ReturnsMoreEmptyValues supplies a non-null empty value  
RISKS: Smart-null exceptions may be thrown only when the returned smart-null proxy is subsequently invoked  
RISKS: Context lacks full exception type/message contract and test fixture interfaces; avoid asserting unspecified wording
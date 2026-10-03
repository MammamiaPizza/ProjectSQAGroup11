TARGETS: CallsRealMethods.answer(InvocationOnMock) for abstract, default, concrete methods.
ORACLES: Abstract method: return default value (null/0/false) instead of MockitoException.
ORACLES: Default method: invoke the interface default implementation successfully.
ORACLES: Concrete method: invoke the real implementation and return its result.
CASES: abstract object method → null; abstract int method → 0; abstract void method.
CASES: interface default method; concrete method with side effect; null invocation.
RISKS: Limited to internal API; expected return type defaults unclear without spec.
TARGETS: CallsRealMethods.answer(InvocationOnMock): dispatch concrete real methods and handle abstract methods.
ORACLES: Trigger tests specify no MockitoException for abstract stub/default and real interface method scenarios.
CASES: Concrete interface default/real method invocation should return its real implementation result.
CASES: Stubbed abstract method on constructor-created mock should return the configured stub value.
CASES: Unstubbed abstract method on constructor-created mock should return Mockito default value, not throw.
RISKS: InvocationOnMock method abstraction/default classification may differ for interfaces versus abstract classes.
RISKS: Context lacks method body and expected concrete values; derive assertions only from trigger behavior.
TARGETS: Complex.add(Complex), especially NaN operand/result handling and returned Complex state.  
ORACLES: isNaN(), equals(Complex.NaN), getReal()/getImaginary() NaN checks; existing trigger name.  
CASES: finite + finite preserves component-wise addition.  
CASES: NaN.add(finite), finite.add(NaN), and NaN.add(NaN) should yield NaN.  
CASES: boundary operands with infinity, only if behavior is established by accessible existing tests.  
RISKS: No method body or full existing test/spec context; avoid assuming null or infinity semantics.
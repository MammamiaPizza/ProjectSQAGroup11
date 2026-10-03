TARGETS: BigFraction.floatValue() and doubleValue() for very large numerator and denominator  
ORACLES: Trigger assertions require 5.0, not NaN, for large-numerator/denominator fractions  
CASES: Construct BigFraction with large BigInteger values whose mathematical quotient is 5  
CASES: Assert floatValue() == 5.0f and doubleValue() == 5.0d  
RISKS: Direct numerator/denominator floating conversion may produce Infinity/Infinity -> NaN  
RISKS: Context lacks exact trigger operands and implementation details; derive expected values from exact ratio only
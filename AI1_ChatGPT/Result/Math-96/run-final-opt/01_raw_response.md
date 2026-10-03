TARGETS: Complex.divide(Complex), especially finite numerator divided by infinite complex values  
ORACLES: Existing trigger ComplexTest.testMath221 expected Complex equality/result signs  
CASES: Divide complex values by INF; assert real/imaginary signed-zero components via getters  
CASES: Cover positive/negative finite numerator components against (+∞,+∞) denominator  
RISKS: Complex.equals/hashCode may distinguish signed zero; use component-bit-aware assertions if needed  
RISKS: Context lacks ComplexTest source and exact MATH-221 input/expected components; do not infer more APIs
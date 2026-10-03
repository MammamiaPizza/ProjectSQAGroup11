TARGETS: MathUtils.gcd(int,int), exercised by MathUtilsTest::testGcd.  
ORACLES: Existing trigger asserts gcd result 98304; buggy result is 3440640.  
CASES: Reproduce the trigger input from MathUtilsTest::testGcd; cover normal, zero, negative, and equal inputs.  
CASES: Exercise integer-boundary operands, especially values whose negation or intermediate arithmetic can overflow.  
RISKS: Trigger operands and gcd edge-case/exception semantics are not provided in this context.
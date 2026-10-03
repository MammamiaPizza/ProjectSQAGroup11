TARGETS: CoreOperationRelationalExpression.computeValue; relational comparison handling through compute/reduce  
ORACLES: Existing CoreOperationTest::testNan specifies <$nan > $nan> evaluates false  
CASES: NaN vs NaN for greater-than (trigger); assert Boolean false  
CASES: Exercise computeValue with expression/context path used by existing relational-operation tests  
CASES: Cover scalar and iterator operands if existing tests expose them, including NaN-containing values  
RISKS: NaN comparison behavior may differ after numeric coercion or iterator matching  
RISKS: Context/API construction details are not provided; reuse existing CoreOperationTest patterns
TARGETS: JSType.getTypesUnderEquality(JSType) used by equality-condition narrowing.  
TARGETS: JSType.getTypesUnderInequality(JSType) may share complementary narrowing logic.  
ORACLES: Trigger test expects `None`, but buggy result is `undefined`.  
CASES: Equality narrowing where one operand/type branch must become no type (`None`).  
CASES: Verify resulting TypePair.typeA and typeB for the trigger’s equality condition.  
CASES: Cover complementary inequality narrowing if it delegates to shared behavior.  
RISKS: Available context lacks JSType implementations/type-registry construction details.  
RISKS: Do not infer results beyond the reported `None` versus `undefined` failure.
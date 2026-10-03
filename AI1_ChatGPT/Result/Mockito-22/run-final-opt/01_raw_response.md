TARGETS: Equality.areEqual(Object,Object) and package-static array helper methods.  
ORACLES: EqualityTest.shouldKnowIfObjectsAreEqual is the only stated expected-result source.  
CASES: Same reference, both null, and exactly one null inputs to areEqual.  
CASES: Non-array objects, including equal-looking and unequal-looking instances.  
CASES: Object and primitive arrays; same/different array runtime types.  
CASES: Arrays with equal/different lengths and differing element positions.  
CASES: Nested arrays to exercise element comparison through areEqual.  
RISKS: Trigger currently throws RuntimeException; isolate input path causing it.  
RISKS: No failing input values or intended assertions are provided in this context.
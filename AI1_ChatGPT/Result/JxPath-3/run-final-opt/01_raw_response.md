TARGETS: NullPropertyPointer createPath(context), createPath(context,value), setValue, getValuePointer  
TARGETS: Actuality/container/leaf and property metadata methods under absent-property state  
ORACLES: Existing BadlyImplementedFactoryTest::testBadFactoryImplementation assertions  
ORACLES: Public method return values/exceptions; superclass behavior is not provided  
CASES: Bad factory implementation path creating a NullPropertyPointer  
CASES: createPath with and without value; setValue after unresolved property access  
CASES: Boundary property index/name values and null value where accepted by signatures  
RISKS: No source body, parent setup, factory contract, or expected assertions are supplied  
RISKS: Do not infer behavior beyond the triggering test and available signatures
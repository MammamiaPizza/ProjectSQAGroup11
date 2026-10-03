TARGETS: StdSubtypeResolver subtype resolution by class and deserialization base type  
TARGETS: registerSubtypes(NamedType...) and registerSubtypes(Class<?>...)  
ORACLES: TestTypeNames.testBaseTypeId1616 should complete without NullPointerException  
ORACLES: Resolved subtype/type-id behavior is constrained by AnnotationIntrospector and config  
CASES: Resolve a base type with registered subtypes and annotation-defined type names  
CASES: Cover empty/uninitialized registered-subtype state and null-sensitive resolution paths  
RISKS: Available context omits resolver method signatures, source body, and expected subtype set/order
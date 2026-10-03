TARGETS: Primitives.primitiveValueOrNullFor(Class<T>) primitive default lookup, especially double.class.  
TARGETS: Also validate primitiveTypeOf, primitiveWrapperOf, and isPrimitiveWrapper map-based conversions.  
ORACLES: Declared primitiveValues/wrapperReturnValues mappings are the expected-result source.  
CASES: primitiveValueOrNullFor(double.class) returns Double 0D, assignable/castable as Double.  
CASES: Cover boolean, char, byte, short, int, long, float, double primitive defaults and exact wrapper types.  
CASES: Check wrapper-to-primitive and primitive-to-wrapper conversions for all listed primitive/wrapper pairs.  
CASES: Non-wrapper/non-primitive inputs should follow existing map-miss behavior; no behavior is specified here.  
RISKS: Generic casts can hide incorrect stored value types until callers cast (Integer-to-Double failure).  
RISKS: Context lacks method bodies and null/void/unmapped-class expected behavior.
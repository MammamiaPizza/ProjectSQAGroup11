TARGETS: AnnotationIntrospector behavior supplying serialization type annotations to serializer construction  
ORACLES: Trigger tests and Bug 1231 failure expectations are the only stated expected-result sources  
CASES: Incompatible annotated serialization type Long -> String should fail with "types not related"  
CASES: Specialized abstract annotated type versus Bean1178Base should serialize without widening failure  
CASES: Normal compatible/supertype annotated serialization type behavior, if exposed by existing tests  
RISKS: Context omits the modified method body and complete AnnotationIntrospector signatures  
RISKS: Do not infer annotation precedence or exception text beyond reported trigger expectations
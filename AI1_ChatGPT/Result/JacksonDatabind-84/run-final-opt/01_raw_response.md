TARGETS: ResolvedRecursiveType.setReference and getSelfReferencedType recursive-type linkage  
TARGETS: Type transformation methods: withContentType/handlers/value handler/static typing  
ORACLES: Trigger assertion: base-first, subtype-later serialization yields {"base":1,"sub":2}  
CASES: Resolve base type before subtype, then serialize subtype with inherited base and subtype properties  
CASES: Verify referenced JavaType remains available after applicable type transformation  
RISKS: Missing recursive reference propagation can drop inherited properties during serializer construction  
RISKS: Context lacks the patch/diff and full fixture type definitions; derive expectations only from trigger/report
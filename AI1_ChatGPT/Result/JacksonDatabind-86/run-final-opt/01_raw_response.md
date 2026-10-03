TARGETS: ResolvedRecursiveType.setReference, getSelfReferencedType, signature/delegating type methods, equals/toString  
ORACLES: Existing trigger assertions; serialized base/sub JSON comparison; JavaType behavior exposed by TypeFactory  
CASES: Recursive superclass whose referenced JavaType is resolved; verify referenced type is retained/delegated  
CASES: Resolve base before subtype, then serialize subtype and assert both base and subtype properties appear  
CASES: Normal reference assignment and self-reference retrieval; generic and erased signature generation after reference  
CASES: Boundary/unresolved recursive type behavior before reference only if existing tests establish it  
RISKS: Recursive delegation can lose base properties or recurse incorrectly in signatures/equality  
RISKS: Context lacks method bodies and exact expected results for non-trigger methods
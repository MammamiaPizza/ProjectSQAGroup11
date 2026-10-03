TARGETS: GenericMetadataSupport.inferFrom and resolveGenericReturnType for nested generic types used by deep stubs  
TARGETS: TypeVariableReturnType raw-type/bounded-type extraction when nested generic metadata is raw  
ORACLES: Trigger test discoverDeepMockingOfGenerics must not throw “Raw extraction not supported for : 'null'”  
CASES: Deep-stub a nested generic return whose enclosing/inner generic type is used as a raw type  
CASES: Resolve a method return involving TypeVariable and ParameterizedType metadata  
CASES: Preserve existing handling for non-generic returns and directly parameterized return types  
RISKS: Raw Type metadata can lack actual type arguments, producing null during bound/raw-type extraction  
RISKS: Context only identifies the failing trigger and signatures; no independent expected type details are supplied
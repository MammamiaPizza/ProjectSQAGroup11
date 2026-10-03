TARGETS: TypeFactory.constructType(Type, Class<?>) and constructType(Type, JavaType) resolve local generic context.  
TARGETS: _fromVariable and _resolveVariableViaSubTypes must preserve resolvable type-variable bounds.  
ORACLES: Trigger 728 expects resolved raw class/interface CharSequence, not Object.  
ORACLES: Trigger 609 must construct type without JsonMappingException for EntityContainer context.  
CASES: Local/inner generic type whose variable has CharSequence bound, resolved through its context.  
CASES: Partially parameterized local type resolved against enclosing EntityContainer generic context.  
CASES: Compare Class and JavaType context overloads for equivalent resolved JavaType.  
RISKS: Local classes and enclosing generic bindings may be unavailable through erased reflective metadata.  
RISKS: Context only identifies triggers and TypeFactory; exact local declarations are not provided.
TARGETS: TypeFactory.findTypeParameters, _resolveVariableViaSubTypes, _fromVariable, constructType
TARGETS: constructType(Type, TypeBindings), moreSpecificType
ORACLES: resolved type must match Java reflection actual type arguments (e.g., T->CharSequence)
ORACLES: expected types from bug-trigger test assertions (CharSequence not Object)
CASES: type variable resolved through concrete subclass with explicit binding
CASES: partial type parameter (some unresolvable) leads to JsonMappingException
CASES: nested generic (Map<K,V>) resolution; unresolvable variable yields unknownType
RISKS: buggy version may resolve incorrectly to Object instead of proper upper bound
RISKS: limited context; no fixed version to confirm intended behavior
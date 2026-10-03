TARGETS: $Gson$Types.resolve(Type, Class, Type), especially recursive TypeVariable resolution.  
ORACLES: Trigger tests must complete without StackOverflowError; resolved Type equality/string forms are available.  
CASES: Self-recursive and mutually recursive generic type variables from RecursiveTypesResolveTest triggers.  
CASES: Resolve TypeVariable within ParameterizedType, WildcardType, and GenericArrayType recursive paths.  
RISKS: Recursive substitutions may loop when a variable resolves to itself or cycles through bounds/types.  
RISKS: Context exposes signatures only; exact expected resolved Types should be derived from existing trigger tests.
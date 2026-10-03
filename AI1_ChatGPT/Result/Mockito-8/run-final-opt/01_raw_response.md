TARGETS: GenericMetadataSupport.registerTypeVariablesOn/registerTypeVariableIfNotPresent recursive type variables.
TARGETS: boundsOf(TypeVariable) and TypeVarBoundedType.firstBound for self-referential bounds.
ORACLES: Trigger expectation: typeVariable_of_self_type must not throw StackOverflowError.
ORACLES: inferFrom(Type).actualTypeArguments() exposes resolved type-variable mappings.
CASES: Self-bounded type variable (for example, T extends Comparable<T>) during inferFrom/resolution.
CASES: Repeated encounter of the same TypeVariable must terminate without recursive re-registration.
CASES: Normal non-self generic class/type-variable metadata remains resolvable.
RISKS: Context lacks the exact trigger fixture assertions and intended resolved Type values.

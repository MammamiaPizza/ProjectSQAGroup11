TARGETS: TypeUtils.getTypeArguments(Type, Class<?>) – resolve actual type args.
TARGETS: TypeUtils.isAssignable(Type, Type) – generic subtype assignability.
ORACLES: getTypeArguments expected count from JLS: subtype of This<String,String> yields 2 args.
ORACLES: isAssignable: Thing<String,String> assignable to This<String,String> per JLS.
CASES: Normal: concrete parameterized subtype; same type args as super; variant args.
CASES: Boundary: wildcards (?, ? extends X, ? super X), type variable bounds, recursive generic
references.
CASES: Error: raw types, null inputs, unresolvable variable chains, non-matching hierarchies.
RISKS: Inner test class generics (Thing, This, That) partially known; exact hierarchy may be
misinterpreted.
RISKS: Buggy version substitutes type variables incorrectly; helper methods may yield wrong
intermediate results.
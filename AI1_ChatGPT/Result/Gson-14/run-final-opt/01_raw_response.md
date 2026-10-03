TARGETS: $Gson$Types.resolve(Type, Class, Type), especially recursive TypeVariable and WildcardType resolution.
TARGETS: subtypeOf/supertypeOf wildcard normalization and bound replacement during resolve.
ORACLES: Trigger assertions: avoid nested "? super ? super Number" and "? extends ? extends Number".
ORACLES: Trigger assertions: mixed extends/super bounds resolve to unbounded "?" where expected.
ORACLES: Trigger behavior: recursive generic resolution terminates without StackOverflowError.
CASES: Resolve repeated lower-bound wildcard: ? super Number remains singly bounded.
CASES: Resolve repeated upper-bound wildcard: ? extends Number remains singly bounded.
CASES: Resolve nested opposite wildcards to expected unbounded wildcard.
CASES: Exercise recursive type-variable graphs reflected by RecursiveTypesResolveTest scenarios.
RISKS: Context omits source bodies/type fixtures; derive expectations only from listed trigger outcomes.
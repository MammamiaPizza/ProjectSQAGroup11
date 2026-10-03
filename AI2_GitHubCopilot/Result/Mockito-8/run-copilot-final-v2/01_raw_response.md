TARGETS: boundsOf(TypeVariable) must not recurse infinitely for self-referencing bound (E extends E)
TARGETS: registerTypeVariableIfNotPresent must avoid cyclic re-registration
TARGETS: inferFrom(Type) for ParameterizedType with type var whose bound leads to itself
ORACLES: No StackOverflowError when resolving any TypeVariable with a cyclic bound
ORACLES: contextualActualTypeParameters maps self‑referencing variable to itself (no infinite chain)
CASES: TypeVariable with first bound == itself; indirect cycle (A ext B, B ext A)
CASES: Normal bounded vars, wildcards, multiple vars, deeply nested generics without cycles
CASES: ParameterizedReturnType / TypeVariableReturnType with self‑referencing type variable
RISKS: Must construct TypeVariable/ParameterizedType objects reflectively; Mockito mocks may not
suffice
RISKS: Undefined behavior for self‑referential bounds; cycle detection must not break valid
non‑cyclic cases
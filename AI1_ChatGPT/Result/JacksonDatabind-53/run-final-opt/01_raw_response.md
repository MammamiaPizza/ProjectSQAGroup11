TARGETS: TypeBindings bound lookup/unbound handling; TypeFactory generic map type resolution/refinement.
ORACLES: Existing trigger test must deserialize refined Map values as the configured concrete subtype.
ORACLES: TypeBindings public size/name/type accessors and documented IllegalArgumentException messages.
CASES: Map interface/base type with abstract value refined to HasUniqueId implementation via type information.
CASES: Resolve Map<K,V> bindings with normal key/value types and inherited generic supertype bindings.
CASES: Empty/no-generic bindings; one/two generic arguments; wrong argument count error paths.
RISKS: Available context truncates TypeFactory methods and does not expose the intended patch behavior.
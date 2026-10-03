TARGETS: BeanDeserializerBase.resolve() logic for setting
_arrayDelegateDeserializer/_delegateDeserializer from @JsonCreator annotations.
TARGETS: deserializeFromArray(), deserializeFromString() handling when delegate/array-delegate
exists.
ORACLES: Jackson spec: DELEGATING mode creator must deserialize from array/string if single arg
matches.
CASES: Object with @JsonCreator(DELEGATING) on array-arg ctor; JSON array → verify object created
without exception.
CASES: Object with @JsonCreator(DELEGATING) on String-arg ctor; JSON string → verify object created.
CASES: Empty JSON array with array-arg creator → verify behavior (may succeed with empty list or
fail).
CASES: Missing suitable creator for input type → JsonMappingException with "no suitable constructor
found".
RISKS: Cannot inspect actual patch; may miss regressions in other creator/delegate precedence
scenarios.
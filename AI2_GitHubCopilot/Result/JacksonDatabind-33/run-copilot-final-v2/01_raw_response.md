TARGETS: findUnwrappingNameTransformer(AnnotatedMember) – must return non-null for @JsonUnwrapped
members.
TARGETS: hasIgnoreMarker(AnnotatedMember) – must not ignore members annotated with @JsonUnwrapped.
TARGETS: Introspector property-detection logic for @JsonUnwrapped as a valid property indicator.
ORACLES: Serialization of a bean with @JsonUnwrapped member succeeds without exception.
ORACLES: The bean is considered to have properties, avoiding FAIL_ON_EMPTY_BEANS error.
CASES: Normal: @JsonUnwrapped on getter of nested bean with several properties.
CASES: Boundary: @JsonUnwrapped on field with null nested bean.
CASES: Error: @JsonUnwrapped on static method; should be ignored or handled.
RISKS: Only partial introspector code available; exact fix may involve multiple methods.
RISKS: Cannot validate serialized output correctness; only absence of exception.
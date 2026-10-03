TARGETS: deserializeWithUnwrapped, deserializeFromObject, finishBuild in BuilderBasedDeserializer
ORACLES: existing BuilderWithUnwrappedTest asserts expected values (e.g., "John", 30)
CASES: @JsonUnwrapped+@JsonCreator: single param at start/middle, multiple at start/middle; normal
(non-unwrapped) regressions
CASES: empty unwrapped, null values, multiple unwrapped props, combined with views/type-id
RISKS: limited to buggy version; fix not yet applied; other deser paths may be affected beyond
builders
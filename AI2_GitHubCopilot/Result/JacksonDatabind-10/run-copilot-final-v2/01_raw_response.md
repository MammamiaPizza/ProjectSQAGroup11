TARGETS: AnyGetterWriter.resolve, AnyGetterWriter.getAndSerialize,
BeanSerializerFactory._constructBeanSerializerBuilder.
ORACLES: Known-fixed testIssue705's expected JSON: {"stuff":"key/value"}. Compare serialized output
via ObjectMapper.writeValueAsString.
CASES: Bean with @JsonAnyGetter returning Map. Test: map entry "key"→"value", multi-entry, null
value, empty map, non-String key.
RISKS: Exact bug trigger might need specific key/value types or non-resolved MapSerializer; test may
pass on buggy version if resolve() already works.
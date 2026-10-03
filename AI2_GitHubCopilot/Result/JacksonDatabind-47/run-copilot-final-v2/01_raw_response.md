TARGETS: AnnotationIntrospector.findSerializationType / widen-type check logic for @JsonSerialize
annotation value.
ORACLES: Exception message must contain "types not related" as asserted in testBrokenAnnotation and
testSpecializedAsIntermediate.
CASES: Unrelated types (Long/String), sibling classes (Base/Abstract), no hierarchy; also compatible
subtypes (should pass).
RISKS: Exact throw method hidden; message may be wrapped; need mapper.writeValueAsString to trigger.
Limited to buggy version.
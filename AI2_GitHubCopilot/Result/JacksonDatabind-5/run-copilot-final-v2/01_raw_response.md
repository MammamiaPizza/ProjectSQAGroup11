TARGETS: resolveMemberMethods, resolveFields, _addMixUnders, _addClassMixIns; annotation retrieval
via getAnnotation, annotations().
ORACLES: Check that mixin annotations appear on target member methods/fields; verify serialization
succeeds with expected properties.
CASES: Mixin with @JsonProperty on getter; mixin with @JsonIgnore on field; multiple mixins; mixin
on superclass; mixin resolver returns null.
RISKS: Cannot inspect PersonImpl test data; internal resolution depends on
AnnotationIntrospector/MixInResolver not shown.
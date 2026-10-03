TARGETS: StdKeySerializers.getDefault()/factory for Enum.class;
Default/StringKeySerializer.serialize()
TARGETS: Enum key serialization path that should consult @JsonProperty annotation value vs. name()
ORACLES: expected key string from @JsonProperty("aleph") vs. default enum name() "A" (from failing
test)
CASES: Normal: enum key with @JsonProperty, without annotation, with @JsonValue, with multiple
constants
CASES: Boundary: enum constant with empty @JsonProperty(""), null enum, enum in nested class
RISKS: Full method signatures and internal lookup of annotation not visible; may rely on
AnnotationIntrospector
RISKS: Only local StdKeySerializers fragment available; serialization logic for Enum.class branch
incomplete
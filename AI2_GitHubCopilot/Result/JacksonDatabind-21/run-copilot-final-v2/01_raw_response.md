TARGETS: JacksonAnnotationIntrospector.findEnumValues (or related method reading @JsonProperty on
enum constants)
ORACLES: @JsonProperty value should override enum constant name during deserialization (per Jackson
docs)
CASES: Enum with @JsonProperty rename; enum without annotation (unchanged); multiple constants with
same @JsonProperty value; empty @JsonProperty value
RISKS: Only method signatures provided—no source body; cannot see current/fixed logic; fix likely in
findEnumValues or enum-name-resolution helper
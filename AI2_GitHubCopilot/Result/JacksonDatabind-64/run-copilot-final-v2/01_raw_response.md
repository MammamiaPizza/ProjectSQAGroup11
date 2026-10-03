TARGETS: getDefaultValue(JavaType) return for String, and _defaultInclusion handling when building
BeanPropertyWriter.
ORACLES: serialize with ObjectMapper and check JSON string lacks "str":null when
@JsonInclude(NON_NULL) applies.
CASES: Normal: property null, class-level @JsonInclude(NON_NULL) → exclude; per-property
@JsonInclude(ALWAYS) → include.
CASES: Boundary: property-level @JsonInclude(NON_NULL) overrides global ALWAYS; null
Map<String,String> entry with global NON_NULL.
CASES: Error: missing annotation causing USE_DEFAULTS with null value; cast / type mismatches
triggering _throwWrapped for default.
RISKS: Bug may reside in inclusion merging between annotations and _defaultInclusion; interaction
with _useRealPropertyDefaults is unknown.
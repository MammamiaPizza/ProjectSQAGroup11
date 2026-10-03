TARGETS: BeanDeserializerFactory property-collecting logic (especially handling of ignored getters)
TARGETS: AnnotatedMethod getter and @JsonIgnore annotation checking
ORACLES: Expected value "jack" per test JSON for property with ignored getter but available setter
ORACLES: Jackson deserialization should not skip a property just because its getter is ignored
CASES: Normal: property with @JsonIgnore on getter, setter present → deserialize to "jack"
CASES: Boundary: property with @JsonIgnore on getter and no setter → skip property (null/ignored)
CASES: Boundary: property with @JsonIgnore on both getter and setter → skip
CASES: Error: property with conflicting ignore config may cause unexpected skip
RISKS: Exact factory method path unknown; rely on trigger test for behavior verification
RISKS: Ensure test uses @JsonIgnore only on getter, not on field or setter
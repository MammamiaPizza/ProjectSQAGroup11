TARGETS: JacksonAnnotationIntrospector enum-name lookup behavior for @JsonProperty-renamed enum constants.  
ORACLES: Trigger expects deserialization of renamed enum values "b","a", not original names "B","A".  
CASES: Enum constants with distinct @JsonProperty names; deserialize each renamed lowercase value.  
CASES: Verify multiple renamed constants preserve their individual annotation-provided names.  
RISKS: Available context is truncated; exact modified method and annotation precedence are not shown.
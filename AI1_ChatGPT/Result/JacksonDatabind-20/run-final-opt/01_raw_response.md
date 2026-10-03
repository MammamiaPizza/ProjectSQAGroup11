TARGETS: ObjectNode#setAll(Map<String,? extends JsonNode>) and setAll(ObjectNode) bean-introspection visibility  
ORACLES: Trigger expects ObjectNode introspection without conflicting setter definitions for property "all"  
CASES: Serialize/deserialize or introspect ObjectNode through ObjectMapper using standard naming strategy  
CASES: Verify both setAll overloads do not create duplicate logical "all" setters during introspection  
RISKS: Actual intended annotation/visibility change is not provided; assert only absence of reported conflict  
RISKS: No alternate version or source diff is available; avoid assuming setAll return/mutation semantics
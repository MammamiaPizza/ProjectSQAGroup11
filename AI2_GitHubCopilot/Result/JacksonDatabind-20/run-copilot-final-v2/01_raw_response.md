TARGETS: ObjectNode.setAll(Map) and setAll(ObjectNode) conflict as setters for property "all"
ORACLES: No JsonMappingException when mapping property "all" onto ObjectNode
ORACLES: Both setAll overloads remain usable and do not break existing code
CASES: Deserialize {"all":{"a":1}} into ObjectNode and check child node existence
CASES: setAll with null, empty Map, empty ObjectNode; verify no errors or proper handling
CASES: Verify field-based access: after setAll(Map), _children contains expected entries
RISKS: Naming strategy change might hide one overload; verify both accessible via reflection
RISKS: Other ambiguous setters could still cause conflicts; need broader introspection test
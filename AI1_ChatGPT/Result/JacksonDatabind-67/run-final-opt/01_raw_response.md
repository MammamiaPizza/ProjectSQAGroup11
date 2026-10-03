TARGETS: BasicDeserializerFactory key-deserializer creation for enum Map keys and deserializer modifiers.  
ORACLES: Trigger test accepts mixed-case "REPlaceMENTS" as KeyEnum key via configured modifier.  
ORACLES: Without effective custom key deserializer, enum-key conversion throws InvalidFormatException.  
CASES: Deserialize Map<KeyEnum,...> with "REPlaceMENTS" after registering a BeanDeserializerModifier.  
CASES: Verify modifier-provided key deserializer is used for enum keys, not default enum-name matching.  
CASES: Cover normal declared enum key values alongside the mixed-case key.  
RISKS: Context omits exact modified method body and custom modifier/deserializer implementation details.
TARGETS: StdKeyDeserializer.forType(Class<?>), especially lookup for CharSequence map keys.  
TARGETS: StringKD.forType(Class<?>) and deserializeKey(String, DeserializationContext).  
ORACLES: Trigger expects Map<CharSequence,...> deserialization to find a key deserializer, not throw JsonMappingException.  
ORACLES: Deserialized CharSequence key should preserve the JSON field-name text.  
CASES: Deserialize a JSON object into Map<CharSequence,?> with one ordinary string field name.  
CASES: Verify CharSequence key lookup/value retrieval using the expected field-name text.  
CASES: Boundary: empty JSON field name as a CharSequence map key, if map binding accepts it.  
RISKS: Available context does not state intended handling of CharSequence subtypes or direct forType(CharSequence.class).
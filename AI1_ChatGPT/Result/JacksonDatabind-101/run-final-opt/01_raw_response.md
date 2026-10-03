TARGETS: BeanDeserializer.deserializeUsingPropertyBasedWithUnwrapped for creator-based unwrapped fields.  
ORACLES: Trigger assertion: deserialized result has expected value 4, not 0.  
CASES: JSON with unwrapped fields appearing after the last creator property.  
CASES: Creator properties completed before subsequent unwrapped-field tokens are processed.  
CASES: Normal creator-based unwrapped deserialization preserving post-creator field values.  
RISKS: Protected internals; test through ObjectMapper/model behavior used by existing trigger.  
RISKS: Context provides no model JSON shape or additional expected behavior.
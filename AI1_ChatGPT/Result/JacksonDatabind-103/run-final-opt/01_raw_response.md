TARGETS: JsonMappingException location/path message composition during deserialization failures  
TARGETS: DeserializationContext mapping/reporting methods that create or wrap JsonMappingException  
ORACLES: BasicExceptionTest.testLocationAddition: resulting message contains exactly one "at [" marker  
ORACLES: Invalid enum Map-key input remains reported as an InvalidFormatException-derived mapping failure  
CASES: Deserialize a Map with enum key type ABC and invalid key string "value"  
CASES: Assert failure message includes key-conversion context without duplicated location suffix  
CASES: Compare marker occurrence and preserve non-location diagnostic text where exposed  
RISKS: Many modified classes; provided trigger isolates exception-location composition only  
RISKS: Available signatures are truncated; avoid direct testing of unavailable/internal APIs
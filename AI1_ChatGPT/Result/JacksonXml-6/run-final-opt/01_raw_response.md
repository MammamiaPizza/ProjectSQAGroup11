TARGETS: ToXmlGenerator binary-stream serialization used for TestPojo.field.  
ORACLES: Existing trigger tests define successful XML/base64 output expectations for 0–4 byte streams.  
CASES: Serialize InputStream fields containing 0, 1, 2, 3, and 4 bytes.  
CASES: Verify serialization completes without JsonMappingException or unsupported-generator failure.  
CASES: Check resulting field content reflects binary encoding, including base64 padding boundaries.  
RISKS: Stream length handling may differ for empty input and 1/2-byte final base64 groups.  
RISKS: Available signatures are truncated; binary-stream method overload/length contract is not fully shown.
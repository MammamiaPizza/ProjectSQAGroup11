TARGETS: JsonParser.getValueAsString()/getValueAsString(default) via ReaderBasedJsonParser and UTF8StreamJsonParser.  
ORACLES: Trigger expectations: parsing string value "a" yields "a", not null, for char and byte inputs.  
CASES: Reader-backed parser: advance to VALUE_STRING, assert getValueAsString() returns exact unquoted text.  
CASES: UTF-8 byte-stream parser: advance to VALUE_STRING, assert getValueAsString() returns exact unquoted text.  
CASES: Exercise default-value overload on VALUE_STRING if exposed; result should remain token text.  
RISKS: Token positioning matters; value access likely requires nextToken() reaching VALUE_STRING.  
RISKS: Context omits full method bodies and parser-factory setup; avoid asserting unspecified non-string behavior.
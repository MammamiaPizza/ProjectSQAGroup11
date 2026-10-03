TARGETS: ToXmlGenerator.writeBinary, ToXmlGenerator.writeBinary(Base64Variant,byte[],int,int)
ORACLES: Expect serialization succeeds for byte[] fields (0..4 bytes), output is Base64-encoded XML
CASES: 0-byte array, 1-byte array, 2-byte array, 3-byte array, 4-byte array, null byte[] field
RISKS: Only XML-safe binary via Base64 encoding; StAX2 impl may differ from StAX emulation path
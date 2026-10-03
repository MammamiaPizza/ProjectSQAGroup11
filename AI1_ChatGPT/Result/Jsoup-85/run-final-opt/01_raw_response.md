TARGETS: Attribute(String,String), Attribute(String,String,Attributes), setKey(String), setValue(String)
ORACLES: Trigger requires IllegalArgumentException for empty attribute keys.
CASES: Construct with empty key; construct with non-empty key/value as normal control.
CASES: setKey("") should throw IllegalArgumentException; setKey(non-empty) should update key.
RISKS: Null-key/value contracts are documented, but exception type/behavior is not supplied here.
RISKS: Parent-backed rename/value synchronization may matter, but no Attributes API/expected behavior is provided.
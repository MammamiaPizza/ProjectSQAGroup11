TARGETS: TypeAdapters numeric adapters, especially INTEGER/SHORT/BYTE read(JsonReader).  
ORACLES: Trigger PrimitiveTest expects numeric JSON strings to deserialize as numbers.  
CASES: Deserialize quoted integral values into int/Integer; verify resulting numeric value.  
CASES: Cover unquoted numeric values and JSON null to preserve existing numeric/null behavior.  
CASES: Boundary valid byte/short/int values; quoted forms are high value.  
CASES: Invalid or out-of-range numeric strings should retain adapter error behavior.  
RISKS: Available context is truncated; exact modified adapters and exception contracts are incomplete.
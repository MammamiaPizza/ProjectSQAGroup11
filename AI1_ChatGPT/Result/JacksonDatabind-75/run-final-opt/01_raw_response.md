TARGETS: EnumSerializer.serialize and _serializeAsIndex for enum property format shape  
ORACLES: Trigger expects {"color":2} for GREEN, not {"color":"GREEN"}  
CASES: Enum property annotated/requested as numeric shape serializes ordinal index  
CASES: Default enum serialization retains name-based output when numeric shape is not requested  
CASES: Boundary enum constants verify first and last ordinal indices  
RISKS: Property-level format shape may be ignored in favor of serializer default  
RISKS: Context provides only one failing numeric-shape oracle; no alternate-version reference
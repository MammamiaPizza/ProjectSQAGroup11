TARGETS: DateTimeSerializerBase contextual format handling, especially format without pattern  
ORACLES: Existing trigger expected JSON for date with timezone: "1970-01-01X01:00:00"  
CASES: @JsonFormat timezone-only/no-pattern serialization should use configured default date formatting  
CASES: Normal pattern-based and timestamp-enabled paths should retain their selected representation  
RISKS: Abstract base behavior requires exercising via an existing concrete date serializer/ObjectMapper  
RISKS: Context lacks full annotation/configuration semantics; derive expectations only from trigger/spec
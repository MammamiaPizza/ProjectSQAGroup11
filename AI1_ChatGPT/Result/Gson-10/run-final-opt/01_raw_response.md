TARGETS: ReflectiveTypeAdapterFactory.getBoundFields field-level @JsonAdapter selection during serialization.  
ORACLES: Trigger expects @JsonAdapter on primitive field to override Gson default adapter: {"part":["42"]}.  
CASES: Serialize object with primitive field annotated with custom adapter producing a JSON string.  
CASES: Verify default primitive serialization remains numeric when no field @JsonAdapter is present.  
CASES: Verify annotated primitive null/field exclusion behavior only if accessible through existing Gson API.  
RISKS: Field adapter may be bypassed by primitive-type optimization or runtime-type adapter wrapping.  
RISKS: Context lacks custom adapter class/source and exact annotation configuration; derive expected output from trigger only.
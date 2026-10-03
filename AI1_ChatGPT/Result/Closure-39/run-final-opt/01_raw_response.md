TARGETS: PrototypeObjectType property lookup/count/name APIs may affect RecordType string rendering.  
ORACLES: Existing RecordTypeTest expected strings for recursive records and long records.  
CASES: Recursive self-reference renders loop property as "?" rather than "{...}".  
CASES: Long record preserves later fields (a5, a6) instead of truncating to "...".  
RISKS: Construction/setup APIs for record/prototype types are not provided in context.
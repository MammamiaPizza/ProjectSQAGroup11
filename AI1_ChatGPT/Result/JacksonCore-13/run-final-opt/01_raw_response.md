TARGETS: JsonGeneratorImpl.enable and _checkStdFeatureChanges for Feature.QUOTE_FIELD_NAMES changes  
ORACLES: Trigger comparison expects {foo:1} when field-name quoting is disabled  
ORACLES: Enabled quoting behavior is specified by TestJsonGeneratorFeatures::testFieldNameQuotingEnabled  
CASES: Default/enabled QUOTE_FIELD_NAMES writes a quoted field name  
CASES: Disable QUOTE_FIELD_NAMES before writing field "foo" and value 1; expect {foo:1}  
CASES: Toggle QUOTE_FIELD_NAMES and verify subsequent field-name output reflects the setting  
RISKS: JsonGeneratorImpl is abstract; concrete generator output path is not provided  
RISKS: No expected behavior is given for escaped/special field names while unquoted
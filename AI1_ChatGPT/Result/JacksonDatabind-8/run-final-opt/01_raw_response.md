TARGETS: CreatorCollector.addStringCreator and duplicate-creator verification for String creators  
ORACLES: Trigger testStringBuilder; StringBuilder deserialization must not throw conflicting-creator JsonMappingException  
CASES: Deserialize a JSON string into StringBuilder; verify resulting text matches input  
CASES: Empty JSON string into StringBuilder to cover boundary string value  
RISKS: Duplicate handling must still reject genuinely conflicting explicitly marked String creators  
RISKS: Modified method body and creator annotations/selection path are not provided
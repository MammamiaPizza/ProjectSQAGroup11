TARGETS: PropertyBuilder default-value resolution for String properties under inclusion filtering  
ORACLES: JsonIncludeTest.testIssue1351 expects "{}" rather than "{\"str\":null}"  
CASES: Serialize the trigger bean/property with null String value and its configured inclusion  
CASES: Cover normal non-null String serialization if exercised by the same inclusion path  
RISKS: Default-bean creation/access failures may affect property-default comparison  
RISKS: Context lacks the trigger bean annotations/configuration and exact modified implementation
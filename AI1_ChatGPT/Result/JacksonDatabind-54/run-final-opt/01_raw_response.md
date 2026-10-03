TARGETS: PropertyBuilder.getDefaultValue(JavaType) and default-value handling for serialization inclusion  
TARGETS: PropertyBuilder.getDefaultBean() when deriving property defaults from a bean  
ORACLES: Trigger testEmpty1256 expects "{}" and currently observes "{\"a\":null}"  
CASES: Serialize the trigger’s empty atomic-type bean/property under its configured inclusion rules  
CASES: Verify null-valued property is omitted when its default should suppress serialization  
CASES: Cover default-value lookup for String and non-String JavaType paths  
RISKS: Only PropertyBuilder signatures and trigger failure are available; no broader inclusion contract is given
TARGETS: StdKeyDeserializer.EnumKD._parse and enum-name resolver selection for polymorphic Map keys  
ORACLES: Trigger expects key "FOO" to deserialize as TestCustomEnumKeyDeserializer$SuperTypeEnum  
CASES: Polymorphic Map with custom enum key deserializer and valid enum-name key "FOO"  
CASES: Invalid enum-name key verifies InvalidFormat/weird-key handling remains active  
RISKS: Resolver may target a concrete enum while _keyClass is a polymorphic/supertype declaration  
RISKS: Context lacks patch details and full custom-deserializer configuration; derive assertions from trigger only
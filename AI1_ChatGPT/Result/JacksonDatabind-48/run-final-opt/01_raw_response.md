TARGETS: DeserializationConfig/SerializationConfig visibility overrides used by bean introspection.  
ORACLES: Trigger expects TCls discovery to contain 1 property, not setter-only "name" plus explicit field "groupname".  
CASES: Reproduce TestFeatures.testVisibilityFeatures with mapper visibility configuration and inspect discovered properties.  
CASES: Verify explicit-name field remains discoverable while non-visible/setter-only property is excluded.  
CASES: Exercise both serialization and deserialization introspection paths if the trigger config exposes both.  
RISKS: Available context truncates constructors and relevant visibility-mutator signatures/implementation details.
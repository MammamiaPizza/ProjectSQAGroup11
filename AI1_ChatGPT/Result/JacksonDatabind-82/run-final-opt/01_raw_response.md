TARGETS: BeanDeserializerFactory property discovery/building for getter/setter definitions during bean deserialization  
ORACLES: IgnorePropertyOnDeserTest::testIgnoreGetterNotSetter1595 expects deserialized value "jack"  
CASES: Property with ignored getter and usable setter must retain setter-based deserialization  
CASES: Deserialize input containing that property; assert setter-populated value is not null  
BOUNDARY: Getter-only ignored property versus property having both ignored getter and setter  
RISKS: Context exposes factory only; affected private/protected property-resolution path is not fully specified
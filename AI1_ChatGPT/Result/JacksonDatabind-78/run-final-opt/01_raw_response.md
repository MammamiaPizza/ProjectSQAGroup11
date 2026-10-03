TARGETS: BeanDeserializerFactory.isPotentialBeanType(Class<?>) and bean-deserializer construction path  
ORACLES: Existing trigger expects an exception message containing "Illegal type"  
CASES: Deserialize the illegal type exercised by IllegalTypesCheckTest.testIssue1599  
CASES: Verify rejection occurs before bean deserialization proceeds for that type  
RISKS: Available context omits the exact input type, call path, and full method body  
RISKS: Do not infer additional illegal-type categories or exception wording beyond the trigger
TARGETS: AsPropertyTypeDeserializer.deserializeTypedFromObject and deserializeTypedFromAny token handling.  
ORACLES: Existing trigger test testWithEmptyStringAsNullObject1533 and its expected successful null-object behavior.  
CASES: Empty-string VALUE_STRING input for an As.PROPERTY polymorphic target with missing type property/default impl.  
CASES: Normal object FIELD_NAME sequence containing the configured type-id property.  
CASES: Object missing type-id property, preserving existing missing-property error behavior where applicable.  
RISKS: Context exposes only failure message; exact assertions/configuration must be derived from the trigger test.
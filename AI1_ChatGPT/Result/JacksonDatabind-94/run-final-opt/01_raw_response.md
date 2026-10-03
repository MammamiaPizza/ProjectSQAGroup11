TARGETS: SubTypeValidator.validateSubType(DeserializationContext, JavaType) for C3P0 subtype rejection.
ORACLES: Trigger requires a JsonMappingException message containing "Illegal type".
CASES: Deserialize ComboPooledDataSource from the trigger input; assert rejection occurs before instantiation failure.
CASES: Exercise subtype validation using the raw class and its superclass traversal path.
RISKS: Available context omits the remainder of validateSubType and configured illegal-class-name contents.
RISKS: Do not assume behavior for non-C3P0 types, Spring types, or custom _cfgIllegalClassNames.
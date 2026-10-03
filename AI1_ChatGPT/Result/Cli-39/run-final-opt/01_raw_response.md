TARGETS: TypeHandler.createValue(String, Class) for FileInputStream and related file conversion.
TARGETS: PatternOptionBuilder existing-file pattern parsing through TypeHandler.
ORACLES: Trigger assertions: existing file yields FileInputStream; nonexistent file yields null.
CASES: Existing readable file path converts to FileInputStream.
CASES: Nonexistent file path for existing-file option converts to null, not File/String.
RISKS: Close any returned FileInputStream in tests.
RISKS: Context lacks complete TypeHandler branch behavior and PatternOptionBuilder constants.
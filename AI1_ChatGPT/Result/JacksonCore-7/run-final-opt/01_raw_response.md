TARGETS: JsonWriteContext.writeValue() state handling after object start and field-name expectation.
TARGETS: writeFieldName(String) / writeValue() interaction for object, array, and root contexts.
ORACLES: Trigger assertions require writeString() in object before field name to fail, not output {:"a".
ORACLES: Public status constants and JsonWriteContext state transitions are direct expected-result sources.
CASES: Object context: writeValue() before writeFieldName() returns STATUS_EXPECT_NAME.
CASES: Object context: writeFieldName("x"), then writeValue() returns STATUS_OK_AFTER_COLON.
CASES: Object context: repeated writeValue() without a new name remains/reports field-name expectation.
CASES: Array/root writeValue() normal first/subsequent status behavior; child context reuse/reset boundary.
RISKS: Generator-level exception/output behavior is indirect; available context lacks generator exception API/details.
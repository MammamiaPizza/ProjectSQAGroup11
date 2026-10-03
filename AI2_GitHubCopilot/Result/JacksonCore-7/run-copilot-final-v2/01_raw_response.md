TARGETS: JsonWriteContext.writeValue() status transitions for root/array/object contexts.
TARGETS: writeFieldName() effect on gotName and the subsequent writeValue status.
TARGETS: createRootContext/createChildObjectContext/createChildArrayContext type setup.
ORACLES: STATUS* constants; object without field name must return STATUS_EXPECT_NAME.
ORACLES: Trigger tests assert generators fail when writeString precedes required writeFieldName.
CASES: root writeValue -> STATUS_OK_AS_IS.
CASES: array first value -> STATUS_OK_AS_IS; later values -> STATUS_OK_AFTER_COMMA.
CASES: object before field name -> STATUS_EXPECT_NAME; after colon -> STATUS_OK_AFTER_COLON.
CASES: field-name/value alternation in object context resets the name/value cycle cleanly.
RISKS: Only buggy-version source; avoid asserting generator internals beyond trigger messages.
TARGETS: NumberUtils.createNumber(String), NumberUtils.isNumber(String)
ORACLES: Trigger tests specify "2." must be accepted by both methods.
CASES: createNumber("2.") returns a Number and does not throw NumberFormatException.
CASES: isNumber("2.") returns true.
CASES: Cover decimal-point boundaries: "2.", ".2", and "2.0".
RISKS: Numeric subtype for createNumber("2.") is not specified by provided context.
RISKS: No source/version comparison or broader invalid-input expectations are available.
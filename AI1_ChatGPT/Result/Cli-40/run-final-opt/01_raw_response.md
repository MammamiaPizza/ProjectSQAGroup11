TARGETS: TypeHandler.createValue(String,Object/Class), especially Integer conversion failure handling.
ORACLES: Declared ParseException; trigger expects ParseException for invalid Integer text.
CASES: Valid Integer text returns Integer; malformed, overflow, and empty Integer text throw ParseException.
CASES: Exercise createValue overloads with Integer.class and an Integer type descriptor.
RISKS: Do not assume messages, null behavior, or behavior of unrelated conversion helpers.
RISKS: Context provides only buggy version and no exact existing test inputs.
TARGETS: ReaderBasedJsonParser and UTF8StreamJsonParser floating-point number token parsing.
ORACLES: JsonParser numeric-token APIs and TestNumericValues::testLongerFloatingPoint behavior.
CASES: Long floating-point literals near/over internal buffer boundaries via Reader and UTF-8 byte input.
CASES: Assert tokenization and numeric text/value access complete without ArrayIndexOutOfBoundsException.
CASES: Include ordinary floating-point literals as regression controls for both parser input paths.
RISKS: Available context omits exact literal, buffer sizes, and expected numeric assertions from trigger source.
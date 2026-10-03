TARGETS: ParserBase.getIntValue/getLongValue; convertNumberToInt/ToLong; _reportOverflowInt/Long
ORACLES: Jackson API: getLongValue() must not fail for values within long range (even if >int max);
getIntValue() fails for out-of-int
ORACLES: JsonParseException detail messages consistent with parser version and value magnitude (test
ex. messages)
CASES: int-range (0, ±1, MAX, MIN); boundary (INT_MAX+1, INT_MIN-1) → getLong OK/getInt fail;
long-range normal
CASES: long-bounds (LONG_MAX, LONG_MIN) normal; overflow: LONG_MAX+1, LONG_MIN-1 → getLong fail;
large magnitude values
CASES: parsed VALUE_NUMBER_INT tokens; also test coercion of VALUE_NUMBER_FLOAT to long; ensure
negative zero handled
RISKS: All backends (byte/char/async) share ParserBase; async may override coercion; test concrete
parsers (UTF8StreamJsonParser etc.)
RISKS: Changes to overflow guard may affect existing BigInteger fallback; ensure float→int coercion
logic remains separate
RISKS: Token type (INT vs FLOAT) may influence coercion path; test after finishing parsing (close
parser) to catch late errors
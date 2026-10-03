TARGETS: UTF8StreamJsonParser.getTokenLocation(), getCurrentLocation(), nextToken() location tracking.
ORACLES: Existing trigger TestLocation::testOffsetWithInputOffset expects byte offset 0, not 3.
CASES: Parser created with nonzero input-buffer offset; first token location reports source-relative offset.
CASES: Advance tokens after offset input; verify token/current locations remain correctly based.
CASES: Empty/whitespace input with input offset; verify location behavior if exposed by parser API.
RISKS: Context omits parser construction and exact JsonLocation assertions; reuse only existing test helpers/API.
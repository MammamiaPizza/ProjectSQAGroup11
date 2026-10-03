TARGETS: UTF8StreamJsonParser.getTokenLocation(), getCurrentLocation(), getTextOffset() when parser
created with an input/byte offset.
TARGETS: nextToken() token-start/end offset accounting via internal fields after parsing the first
token.
ORACLES: TestLocation.testOffsetWithInputOffset asserts first-token offset 0; buggy code returns 3
(trigger failure).
ORACLES: JsonLocation getByteOffset()/getCharOffset() values asserted by TestLocation; use its
expectations only.
CASES: first token at buffer start with zero vs nonzero input offset; token after leading
whitespace/newline.
CASES: normal small JSON value; token at end of input buffer boundary; error location after invalid
char.
RISKS: truncated API hides constructor/fields storing input offset and exact offset arithmetic; do
not invent APIs or expected values.
TARGETS: JsonParserSequence.createFlattened(first, second) must flatten nested sequences into
one-level leaf parsers.
TARGETS: addFlattenedActiveParsers(List) must contribute leaf parsers, not a nested
JsonParserSequence wrapper.
TARGETS: containedParsersCount() must match flattened leaf count; bug shows 3 instead of expected 2.
TARGETS: JsonParserSequence(JsonParser[]) / _nextParser index and switchToNext() must advance
correctly over flattened parsers.
ORACLES: ParserSequenceTest.testInitialized asserts containedParsersCount()==2 for a two-parser
sequence (JUnit4 failure expected:<2> but was:<3>).
ORACLES: nextToken() token order and null at exhaustion follow each leaf parser in flattened order.
CASES: flat parser1+parser2; nested sequence as first arg; sequence plus plain parser; repeated
nested arguments.
CASES: boundary: empty/closed parser, single-parser flatten, end-of-input nextToken returning null,
count lower/upper bounds.
RISKS: cannot inspect exact test fixtures/parser tokens; only bug summary and signatures supplied,
not another version.
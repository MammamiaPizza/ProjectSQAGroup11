TARGETS: CharacterReader.consumeToAny(char...) scanning near/end at EOF.
TARGETS: CharacterReader behavior exercised by unclosed CDATA parsing at EOF.
ORACLES: Trigger tests: CharacterReaderTest::nextIndexOfUnmatched and HtmlParserTest::handlesUnclosedCdataAtEOF.
CASES: Unmatched target characters with reader position approaching or at EOF.
CASES: Unclosed CDATA input ending at EOF through HtmlParser integration.
RISKS: Current failures are ArrayIndexOutOfBoundsException (indices 8 and 11).
RISKS: Limited visible API/spec; do not infer exact consumed-string result beyond trigger expectations.
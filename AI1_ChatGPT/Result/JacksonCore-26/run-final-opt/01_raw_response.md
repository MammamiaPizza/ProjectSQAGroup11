TARGETS: NonBlockingJsonParser feedInput, nextToken, getCurrentLocation/location offsets across chunks.
ORACLES: Existing AsyncLocationTest::testLocationOffsets expects offset 1, not 3.
CASES: Feed JSON incrementally; assert token progression and current location after each chunk.
CASES: Boundary splits at initial byte, whitespace, token delimiters, and multibyte UTF-8 boundaries.
CASES: Check needMoreInput before refilling and endOfInput after final input.
RISKS: Only trigger assertion and parser signatures are provided; exact location semantics are otherwise limited.
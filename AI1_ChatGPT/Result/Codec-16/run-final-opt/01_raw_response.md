TARGETS: Base32 constructors accepting custom pad, especially Base32(boolean, byte) and full constructor.
ORACLES: CODEC-200 trigger expects Base32Test::testCodec200 not to throw IllegalArgumentException.
CASES: Construct Base32 with pad bytes that are alphabet characters or whitespace.
CASES: Verify default/custom non-alphabet, non-whitespace pad construction remains valid.
RISKS: Exact intended encoding/decoding behavior for alphabet/whitespace pad is not provided.
RISKS: No other program version or test body is available as an expected-result source.
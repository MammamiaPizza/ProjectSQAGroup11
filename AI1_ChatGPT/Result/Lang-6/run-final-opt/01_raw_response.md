TARGETS: CharSequenceTranslator.translate(CharSequence,Writer) iteration after delegate-consumed chars/code points.
ORACLES: Existing StringUtilsTest::testEscapeSurrogatePairs and CharSequenceTranslator API behavior.
CASES: Supplementary surrogate pair consumed by translator; ensure no index past input length.
CASES: Normal BMP input, unchanged/zero-consumption translation, and mixed BMP plus surrogate-pair input.
CASES: Boundary surrogate pair at final two UTF-16 chars; verify output and no StringIndexOutOfBoundsException.
RISKS: Abstract translate contract determines consumed count; avoid assuming behavior of unprovided concrete translators.
RISKS: Context lacks implementation and exact expected escaped strings; derive assertions only from available tests/API.
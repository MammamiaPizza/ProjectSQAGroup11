TARGETS: ReaderBasedJsonParser methods: nextToken, _nextAfterName, _isNextTokenNameMaybe/Yes when
ALLOW_UNQUOTED_FIELD_NAMES enabled; _icLatin1 array index.
ORACLES: Expected: no ArrayIndexOutOfBoundsException 256; must parse or throw JsonParseException
with meaningful message for chars > 255.
CASES: Unquoted name chars: code 255 (max Latin1), 256 (off-by-one), 0, 0x7F, 0x80, standard ASCII
letters.
CASES: Unquoted full names: "\u0100", "a\u0100b", "\uFFFF", mixed ASCII+nonLatin1; boundary length
names (1 char, long).
CASES: Names with spaces/punctuation where allowed; normal ASCII names to confirm regression safety.
CASES: Replicate testUnquotedIssue510: parse object with unquoted property name containing char >
255; verify no AIOOBE.
RISKS: Unclear spec for non-Latin1 chars in unquoted names; expected behavior ambiguous between
acceptance or graceful rejection.
RISKS: Must enable Feature.ALLOW_UNQUOTED_FIELD_NAMES via JsonFactory for parser; risk of testing
unrelated feature combinations.
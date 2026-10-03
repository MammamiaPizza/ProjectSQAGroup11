TARGETS: StringUtil.normaliseWhitespace(String) (and possibly appendNormalisedWhitespace) for
invisible-character stripping
ORACLES: Output must contain no invisible characters (U+00AD, U+200B–U+200D, etc.) and whitespace
normalized to space
CASES: strings with soft hyphen, zero-width space/joiner/non-joiner; empty input; only invisible
chars; mixed with spaces; leading/trailing invisible
RISKS: Implementation details of isActuallyWhitespace unknown; limited to public StringUtil API;
other invisible codepoints may need same treatment
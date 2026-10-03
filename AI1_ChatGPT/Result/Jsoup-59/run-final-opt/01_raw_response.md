TARGETS: Token.Tag appendAttributeName(String/char), newAttribute(), finaliseTag(), getAttributes()
TARGETS: Token.Tag appendTagName(String/char), name(String), normalName(), reset()
ORACLES: Existing trigger tests define no IllegalArgumentException for control characters in parsed input
ORACLES: Parser/Cleaner observable results from HtmlParserTest and CleanerTest are expected-result sources
CASES: Control code in an attribute name; finalize/start a new attribute after it
CASES: Control characters immediately after a tag name, including cleaner parsing path
CASES: Normal nonempty attribute names/values and multiple attributes preserve existing behavior
CASES: Boundary empty/pending attribute name during finaliseTag/newAttribute must not call invalid attribute creation
RISKS: Token is package-private; tests may need parser-level coverage rather than direct construction
RISKS: Context omits Token implementation and exact expected DOM/cleaned output
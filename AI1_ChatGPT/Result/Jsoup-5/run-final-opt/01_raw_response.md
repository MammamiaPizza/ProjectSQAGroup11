TARGETS: Parser.parse and parseBodyFragment attribute parsing, especially rough/malformed start-tag attributes.
ORACLES: ParserTest::parsesQuiteRoughAttributes; returned Document structure/attributes or absence of exception.
CASES: Valid attributes with quoted and unquoted values through Parser.parse and parseBodyFragment.
CASES: Rough attributes near tag/input end; delimiters, whitespace, missing values, and trailing slash/bracket.
RISKS: Trigger is StringIndexOutOfBoundsException at index 14; assert parsing completes for triggering syntax.
RISKS: Context omits exact trigger HTML and expected attributes; derive only from existing ParserTest behavior.
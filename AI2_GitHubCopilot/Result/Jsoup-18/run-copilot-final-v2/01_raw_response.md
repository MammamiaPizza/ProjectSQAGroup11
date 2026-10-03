TARGETS: CharacterReader.consumeToEnd(), consumeToUntil(char/String, boolean), advance(), rewind(),
isEmpty(), toString()
ORACLES: bug report 110 & test expectations: consumeToEnd must return all remaining chars;
CR+LF→single LF; no stray empty char in attrs
CASES: empty input, sole CR/LF/CRLF at end, input ending with CR, Unicode multi-byte after CRLF, no
newline consumeTo, attribute value with \r
RISKS: only indirect method names from test names; no source for entire API; assumptions on
line-break normalization may be incomplete
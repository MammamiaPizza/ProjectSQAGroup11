TARGETS: JsonPointer.compile(String) with index-like segments; matchElement(), getMatchingIndex(),
matchProperty() after such input.
ORACLES: JSON Pointer RFC 6901: index only if all chars are digits; else property. "1e0" must not
throw NumberFormatException.
CASES: Normal "/0","/42","/foo"; boundary "/2147483647"(max int), "/-1"(property), "/01"(property);
overflow "/999999999999".
CASES: Scientific "/1e0", hex "/0x1A", decimal "/1.5", plus "/+1", non‑digit suffix "/123abc",
explicit property "/~1e0".
RISKS: _parseIndex private; must test via compile/tail. Uncovered: _parseTail/_parseQuotedTail with
numeric-like segments.
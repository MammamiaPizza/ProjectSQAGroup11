TARGETS: QueryParser.byAttribute, contains; TokenQueue.chompBalanced, consumeAttributeKey, unescape.

ORACLES: Bug report expects SelectorParseException for unclosed attribute brackets and for single
quote inside :contains.

CASES: Normal: :contains(text), [attr=val], [attr='val'], [attr="val"]. Boundary: :contains('),
[attr=''], [], :contains(). Error: [attr=, :contains('text), [attr='val].

RISKS: Only buggy-version context; no grammar spec; unknown expected exceptions for other malformed
selectors.
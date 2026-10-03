TARGETS: W3CDom.fromJsoup/convert and W3CBuilder.copyAttributes during DOM attribute creation.
ORACLES: Conversion should not throw DOMException for jsoup documents with invalid attribute names.
CASES: Element with invalid XML attribute name (trigger handlesInvalidAttributeNames).
CASES: Mixed valid and invalid attribute names; valid attributes should remain convertible.
CASES: Boundary names involving XML-illegal characters/prefixes if constructible through jsoup.
RISKS: Private copyAttributes behavior is observable only through fromJsoup/convert output.
RISKS: No source/version context beyond signatures and trigger; avoid assuming invalid-name sanitization rule.
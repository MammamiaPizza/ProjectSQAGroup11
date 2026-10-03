TARGETS: Element HTML serialization via html() and toString(), especially when pretty printing is disabled.
ORACLES: Existing ElementTest::testNotPretty assertion is the only supplied expected-output source.
CASES: Serialize nested/block elements with pretty formatting disabled; assert no added indentation or line breaks.
CASES: Compare html() and toString() output where applicable; include inline text and empty elements.
RISKS: Trigger’s complete expected string and output-settings API details are truncated; avoid assuming unspecified formatting.
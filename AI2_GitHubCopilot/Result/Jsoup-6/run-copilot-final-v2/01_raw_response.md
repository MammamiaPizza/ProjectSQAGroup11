TARGETS: Entities.escape(String,Document.OutputSettings),
Entities.escape(String,CharsetEncoder,EscapeMode), Entities.unescape(String).
TARGETS: exercise quote/ampersand replacement paths implicated by quoteReplacements and
testYahooArticle.
ORACLES: expected behavior per ParseTest::testYahooArticle and EntitiesTest::quoteReplacements.
ORACLES: escape/unescape must not throw IllegalArgumentException on valid or quoted input.
CASES: normal named entities (" &apos; & < >) via unescape and escape round-trip.
CASES: numeric entities decimal/hex (&#34; &#x22;) with and without trailing semicolon.
CASES: boundary: empty string, no-entity text, repeated entities, trailing lone '&'.
CASES: error: malformed/truncated refs like "&#", "&#x", "&;", bare ampersand.
RISKS: only signatures available; no source or previous expected outputs supplied.
RISKS: CharsetEncoder/EscapeMode semantics unspecified; avoid asserting invented encoded results.
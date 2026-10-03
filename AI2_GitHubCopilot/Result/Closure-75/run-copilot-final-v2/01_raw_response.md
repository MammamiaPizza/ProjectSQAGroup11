TARGETS: getStringValue(Node) for IE line-separator chars \u2028/\u2029 inside string literals.
TARGETS: PeepholeFoldConstantsTest.testIEString verifies string folding preserves IE separators.
ORACLES: Expected output is the raw JS string content (including \u2028/\u2029) as defined in the
test's expected string.
ORACLES: Compare result of getStringValue vs. expected string literal after folding.
CASES: String literal containing only \u2028; containing \u20282029; mixed with other chars; empty.
CASES: Boundary: separators at start/end; consecutive separators; only separators.
CASES: Regression: strings without IE separators must return same as before fix.
RISKS: getStringValue may incorrectly strip or escape \u2028/\u2029, breaking test.
RISKS: trimJsWhiteSpace or other helpers used in getStringValue might alter separators.
RISKS: Limited info: actual bug may involve different method; need to verify other tests.
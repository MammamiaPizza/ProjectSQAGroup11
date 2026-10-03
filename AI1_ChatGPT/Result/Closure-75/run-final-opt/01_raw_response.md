TARGETS: NodeUtil.getStringNumberValue, trimJsWhiteSpace, and isStrWhiteSpaceChar handling JS string coercion.
ORACLES: Existing PeepholeFoldConstantsTest::testIEString assertion and JavaScript numeric-string semantics encoded by NodeUtil.
CASES: Numeric strings with leading/trailing JS whitespace, including IE-specific whitespace characters.
CASES: Empty or whitespace-only strings; verify numeric conversion behavior used by constant folding.
CASES: Non-numeric strings and mixed numeric/text strings; verify conversion failure result is preserved.
RISKS: Package-private methods require tests in com.google.javascript.jscomp or indirect peephole coverage.
RISKS: Context omits Node construction details and the exact failing IE string/expected assertion.
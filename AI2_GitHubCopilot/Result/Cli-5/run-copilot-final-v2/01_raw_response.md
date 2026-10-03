TARGETS: Util.stripLeadingHyphens(String)
ORACLES: Expected behavior from method contract: strips leading hyphens; null input should not throw
NPE (implied by fix)
CASES: null, "" (empty), "word", "-s", "--long", "---", "-", "--", "a-b", " -x" (space first, no
strip)
RISKS: No access to fixed version; must infer null-handling from existing test failures; use only
JUnit 3.8.1
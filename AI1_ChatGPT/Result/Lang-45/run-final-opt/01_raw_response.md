TARGETS: WordUtils.abbreviate(String,int,int,String), trigger path in WordUtilsTest::testAbbreviate.
ORACLES: Existing trigger test and LANG-419 summary; expected output must come from current test/spec context.
CASES: Normal abbreviation with valid lower/upper bounds and appendToEnd.
CASES: Boundary upper at/near string length; lower/upper positions that previously cause index 15 access.
CASES: Error/edge inputs only where existing API tests define behavior; verify no StringIndexOutOfBoundsException.
RISKS: No source body or full existing test expectations supplied; avoid inferring undocumented null/bounds semantics.
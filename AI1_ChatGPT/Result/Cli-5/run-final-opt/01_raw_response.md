TARGETS: Util.stripLeadingHyphens(String); Util.stripLeadingAndTrailingQuotes(String) as related utility behavior
ORACLES: Existing UtilTest::testStripLeadingHyphens and BugCLI133Test::testOrder expectations
CASES: stripLeadingHyphens with null must not throw NPE; verify returned value per existing test oracle
CASES: Inputs with "--", "-", and no leading hyphen; include empty string boundary if existing behavior supports it
CASES: Parser/order scenario from BugCLI133Test::testOrder, exercising Util through CLI argument handling
RISKS: Util is package-private; tests may require placement in org.apache.commons.cli
RISKS: Context lacks method bodies and explicit expected strings; derive exact outputs only from current tests/specification
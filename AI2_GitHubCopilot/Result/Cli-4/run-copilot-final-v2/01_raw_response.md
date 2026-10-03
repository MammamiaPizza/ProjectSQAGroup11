TARGETS: Parser.checkRequiredOptions() — build "Missing required option/options" messages.
ORACLES: thrown MissingOptionException message equals "Missing required option: <opt>" and "Missing
required options: <opts>".
ORACLES: Junit asserts exact exception messages in OptionsTest::testMissingOptionException and
testMissingOptionsException.
CASES: one missing required option; multiple missing required options, comma-space joined.
CASES: message prefix wording singular "option" vs plural "options".
CASES: empty/unset required options produce no MissingOptionException.
RISKS: expected values contain leading substring (e.g. "fx"), avoid over-matching; no existing impl
body shown.
TARGETS: ProperFractionFormat.parse(String, ParsePosition), especially parsing whole/numerator minus placement.
ORACLES: Trigger test FractionFormatTest::testParseProperInvalidMinus expects malformed improper fraction rejection.
CASES: Valid proper fraction parsing with whole, numerator, denominator and ParsePosition advancement.
CASES: Invalid minus within improper fraction components; verify parse failure/ParsePosition error behavior.
CASES: Boundary signs: leading whole minus versus misplaced minus before/within numerator or denominator.
RISKS: Exact accepted grammar and failure-state details are limited to the provided trigger/spec context.
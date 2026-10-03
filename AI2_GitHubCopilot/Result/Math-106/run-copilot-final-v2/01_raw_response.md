TARGETS: parse(String, ParsePosition) — detects invalid minus in proper fractions
ORACLES: testParseProperInvalidMinus expects ParseException or null for invalid minus;
FractionFormat parent provides parse logic
CASES: parse "1 2/3" (valid), "-1 2/3" (valid whole), "1 -2/3" (invalid numerator minus), "1 2/-3"
(invalid denominator minus), "- 2/3" (invalid whole minus)
CASES: parse "-3/2" (improper fraction, minus before numerator, should reject), parse "3/2"
(improper, may be invalid per spec)
RISKS: Bug may incorrectly allow minus in numerator/denominator of improper fraction; whole-part
sign handling may vary with NumberFormat locale
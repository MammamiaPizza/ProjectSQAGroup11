TARGETS: NumberUtils.createNumber(String), especially decimal/exponent parsing and numeric type selection.
ORACLES: Existing trigger testCreateNumber and LANG-638 failure: valid input must not cause StringIndexOutOfBoundsException.
CASES: Decimal strings with exponent markers/signs; vary fraction length around the reported index-4 boundary.
CASES: Valid integer, decimal, exponent, and suffix inputs; assert returned Number value/type only where existing tests specify.
CASES: Malformed/empty/null numeric strings; assert documented/observed NumberFormatException behavior if available in tests.
RISKS: Source context omits createNumber implementation and trigger inputs/expected values; avoid inferred syntax/type rules.
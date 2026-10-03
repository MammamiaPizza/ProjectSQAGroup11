TARGETS: parse(String,ParsePosition) with patterns containing both literal chars and format fields
TARGETS: getStrategy, CopyQuotedStrategy.addRegex for enforcing literal matches in regex
TARGETS: TextStrategy (day-of-week) and NumberStrategy interaction in multi-field patterns
ORACLES: parse returns null when input violates any literal sequence in the pattern
ORACLES: parse returns a correct Date only if all fields and literals are matched consecutively
ORACLES: expected null/parse-failure matches SimpleDateFormat behavior for same pattern+input
CASES: normal valid date fully matching pattern; boundary pattern with only literal characters
CASES: error: input missing a required literal (e.g., comma), input with extra trailing chars
CASES: pattern "M E,3 Tue" with input "3 Tue" (fields match but comma literal missing)
RISKS: Bug might be limited to literal-handling; other strategies (TimeZone,Era) may be unaffected
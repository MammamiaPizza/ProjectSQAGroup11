TARGETS: DateTimeFormatter.parseInto(ReadWritableInstant,String,int) for month/day parsing with default year.
ORACLES: Trigger expectation: parsing "2 29" must not reject day 29 solely because default year is non-leap.
CASES: Parse "2 29" into New_York at start-of-year; assert successful parse/returned end position.
CASES: Parse "2 29" into Tokyo at end-of-year; assert successful parse/returned end position.
CASES: Verify parsed month/day remain February 29 under both zone-sensitive trigger setups.
RISKS: Exact formatter construction, instant values, and post-parse expected year are not provided.
RISKS: No other-version comparison or unstated behavior/API assumptions should be used.
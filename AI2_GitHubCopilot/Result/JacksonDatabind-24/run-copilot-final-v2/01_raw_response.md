TARGETS: BaseSettings.withDateFormat(DateFormat) – must update internal _timeZone from given
DateFormat.
TARGETS: BaseSettings.getTimeZone() – return the timezone set via withDateFormat or with(TimeZone).
ORACLES: After withDateFormat(df) where df has America/Los_Angeles, getTimeZone() must return that
zone.
ORACLES: After with(TimeZone) override, getTimeZone() must return the override; compare with test
expectation.
CASES: Normal: DateFormat with custom timezone → getTimeZone reflects it.
CASES: Boundary: DateFormat with null timezone → getTimeZone may keep previous, or null handling.
CASES: Boundary: multiple withDateFormat calls, last one wins for timezone.
CASES: Error: DateFormat with UTC → verify not confused with default GMT.
RISKS: Only BaseSettings signatures provided; TestConfig code not shown, so exact assertion logic
must be inferred.
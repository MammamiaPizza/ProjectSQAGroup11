TARGETS: StdDateFormat.parse(String), parseAsISO8601(String,ParsePosition,boolean),
withTimeZone(TimeZone), setTimeZone(TimeZone)
ORACLES: Expected Date from parse of no-timezone ISO8601 string equals Date from parse of same
string+"Z" when configured timezone=UTC (issue #1657)
ORACLES: Expected epoch-millisecond value for "1970-01-01T00:00:00" with UTC StdDateFormat; test
assert uses DateFormat.format in PST to verify
CASES: Normal: parse "1970-01-01T00:00:00" with UTC, PST, and default timezones; parse with "Z"
suffix; with "+01:00" offset; date-only "1970-01-01"
CASES: Boundary: epoch-millisecond exact, DST transition dates, year 0001, year 9999; parse empty
string, null, malformed ISO8601, whitespace
RISKS: Cached static DATE_FORMAT_ISO8601 may ignore instance _timezone after withTimeZone() or
setTimeZone() calls; _clearFormats not called on timezone change
RISKS: parse() may fallback to system default timezone when string lacks timezone, ignoring
configured _timezone; thread-safety for shared instance
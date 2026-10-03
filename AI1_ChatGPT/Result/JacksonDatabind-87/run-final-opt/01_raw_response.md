TARGETS: StdDateFormat.parse(String) and parse(String, ParsePosition) for ISO-8601 inputs without timezone
TARGETS: withTimeZone(TimeZone) and setTimeZone(TimeZone) interaction with no-timezone ISO parsing
ORACLES: Trigger assertion: expected Wed Dec 31 16:00:00 PST 1969, got Wed Dec 31 18:00:00 PST 1969
CASES: ISO-8601 date-time without timezone using a non-default configured timezone
CASES: Compare timezone-less ISO parsing against explicit timezone configuration and ParsePosition path
CASES: Normal ISO-8601 with timezone, plain date, and RFC1123 parsing as regression coverage
RISKS: Date.toString() is default-timezone dependent; compare Date instants rather than rendered strings
RISKS: Input string and configured non-default timezone from trigger are not provided in this context
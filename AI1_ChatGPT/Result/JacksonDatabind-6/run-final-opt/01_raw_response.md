TARGETS: StdDateFormat.parse(String) and parse(String,ParsePosition), especially parseAsISO8601 timezone/fraction handling.
ORACLES: Trigger inputs must parse to Date; compare instants using expected UTC offset semantics.
CASES: "1997-07-16T19:20+01:00" accepts ISO-8601 time lacking seconds with colon offset.
CASES: "2014-10-03T18:00:00.6-05:00" accepts one-digit fractional milliseconds with colon offset.
CASES: Boundary fractional precision and timezone forms only where supported by existing standard formats.
RISKS: ParsePosition error/index behavior is unspecified here; avoid asserting it beyond successful parsing.
RISKS: Context lacks fixed expected epoch values and exact malformed-input rejection rules.
TARGETS: StdDateFormat.parse(String,ParsePosition), parseAsISO8601, looksLikeISO8601,
getISO8601Format
ORACLES: Compare Date.getTime() to expected millis from UTC offset; no InvalidFormatException;
success on previously failing inputs.
CASES: Normal missing seconds: "1997-07-16T19:20+01:00", "1997-07-16T19:20Z",
"1997-07-16T19:20-05:00"
CASES: Normal partial ms: "18:00:00.6-05:00", ".60Z", ".600+00:00", ".0Z", ".00+01:00", ".000-05:00"
CASES: Full precision: "18:00:00.123Z", "1997-07-16T19:20:30.123+01:00" (ensure existing patterns
still work)
CASES: Boundary: date-only "2020-01-01", RFC1123 "Wed, 02 Oct 2002 15:00:00 +0200", missing timezone
in ISO
CASES: Error: null, "", "invalid", "1997-07-16T19:20:00.600+01:00extra", missing "T", partial date
components
RISKS: Fix may add lenient parsing or append missing seconds; existing formats must not regress;
locale/timezone settings in StdDateFormat influence parse; JDK differences in DateFormat leniency.
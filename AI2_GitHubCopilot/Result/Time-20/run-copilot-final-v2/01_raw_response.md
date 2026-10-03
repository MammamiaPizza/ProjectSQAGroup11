TARGETS: DateTimeFormatterBuilder.appendTimeZoneName / appendTimeZoneId parse into DateTime
TARGETS: TimeZoneName parseInto (underscore handling), print via toFormatter
ORACLES: DateTimeZone.forID parse equality with America/Dawson_Creek
ORACLES: DateTimeZone.getAvailableIDs contains America/Dawson_Creek
CASES: parse "America/Dawson_Creek" at end, mid, trailing underscore
CASES: round-trip print then parse full zone names (short/long)
CASES: boundary IDs: "America/Port-au-Prince", "America/Argentina/Buenos_Aires", "Etc/GMT+10"
CASES: error: malformed input stopping at "_Creek" must not throw IllegalArgumentException
RISKS: truncated API hides appendTimeZoneName signatures and TimeZoneName internals
RISKS: expected-result source limited; rely on DateTimeZone.forID and available IDs
TARGETS: StdDateFormat.format(Date) / internal _format(…) and helpers pad4, pad2, pad3 handling
extended-year formatting.
TARGETS: StdDateFormat.parse(String), _parseDate, parseAsISO8601 for extended ISO-8601 year parsing.
ORACLES: ISO 8601 requires "+" for years > 4 digits, "-" for negative years; year 0 => "+0000".
ORACLES: Bug report expected strings: "+10204-01-01T00:00:00.000+0000" and
"[+0000]-01-01T00:00:00.000+0000".
CASES: Year 9999 (max 4-digit); Year 10000 (5-digit with + sign); Year 10204 (bug case); Year 0
(BCE→"+0000").
CASES: Negative years: -1, -9999, -10000 (needs - sign and 5 digits); very large years: +123456,
-54321.
RISKS: pad4 may truncate >4-digit years; sign may be lost; parsing may misinterpret missing sign or
year length.
RISKS: Not a complete DateFormat subclass; must exercise with various TimeZone/Locale/Bool lenient;
test serialization round-trip.
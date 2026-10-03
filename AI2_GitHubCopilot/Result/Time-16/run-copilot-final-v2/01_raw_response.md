TARGETS: DateTimeFormatter.parseInto(ReadWritableInstant,String,int) with withDefaultYear
TARGETS: withDefaultYear(int) resolving year for month/day-only parsed fields
TARGETS: parseLocalDate/parseDateTime wrappers over parseInto
ORACLES: trigger tests expect 2004-based instants (e.g., 2004-05-01T12:20:30.000+09:00)
ORACLES: expected values from TestDateTimeFormatter assertion messages
CASES: monthOnly "05" with base start/end year, parse start/end position, default 2004
CASES: monthDay "02/29" leap-day with default year 2004
CASES: parseInto MutableDateTime seeded at 2000 must yield 2004
CASES: malformed text, parse position stop, non-leap default-year 02/29 error
RISKS: no source beyond prompt; internal parser resolution path unconfirmed
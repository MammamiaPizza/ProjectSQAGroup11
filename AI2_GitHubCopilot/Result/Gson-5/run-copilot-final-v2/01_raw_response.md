TARGETS: parse(String, ParsePosition) with timezone offsets +HH:MM, +HH, -HH, Z, empty
TARGETS: checkOffset and indexOfNonDigit used in parsing separators and timezone
ORACLES: Expected Date values from java.text.SimpleDateFormat; roundtrip with format()
CASES: "1970-01-01T01:00:00+01" should parse to UTC epoch (1970-01-01T00:00:00Z)
CASES: Offsets with minutes: "+05:30", "+01", "-08:00", "Z" (no offset)
CASES: Milliseconds: "1970-01-01T00:00:00.123+01:00"; date without time: "1970-01-01"
CASES: Error inputs: invalid offset "+25:00", missing colon, trailing characters
CASES: Boundary: year 9999-12-31T23:59:59.999Z, year 0001-01-01, years before epoch
RISKS: Cannot inspect ISO8601Utils source; only API signatures and failing test
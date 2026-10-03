TARGETS: StdDateFormat.setLenient (via setTimeZone/constructor), parse(String),
parse(String,ParsePosition), isLenient()
ORACLES: java.text.DateFormat lenient semantics; StdDateFormat Javadoc; Date/Calendar contract for
parse/format
CASES: lenient=true parses looser dates; lenient=false rejects invalid; null/empty input; clone
retains lenient
CASES: boundary: leap second; ISO8601 with Z; RFC1123; timezone offset; cross-DST; epoch zero; year
9999
RISKS: Cannot inspect StdDateFormat source internals; exactly which format fields are null when NPE
occurs is unknown
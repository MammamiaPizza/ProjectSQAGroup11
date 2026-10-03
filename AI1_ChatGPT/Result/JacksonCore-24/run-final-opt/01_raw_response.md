TARGETS: ParserBase numeric access/coercion and overflow-reporting paths; ParserMinimalBase error construction/reporting.
ORACLES: Triggered tests specify JsonParseException for out-of-range int/long numeric values.
ORACLES: Valid long parsing is evidenced by NumberParsingTest::testSimpleLong trigger context.
CASES: 2147483648 requested as int; expect int out-of-range JsonParseException.
CASES: 9223372036854775817 and -9223372036854775809 requested as long; expect range exceptions.
CASES: Very long integer (199999 digits) requested as int; expect int out-of-range JsonParseException.
CASES: 12345678907 should parse/access as long rather than fail as int in simple-long flow.
CASES: Exercise both blocking and async parsers, as listed triggers cover both.
RISKS: Exact public parser setup and assertions are not fully provided; avoid assuming unlisted APIs/messages.
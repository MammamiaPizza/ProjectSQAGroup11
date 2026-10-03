TARGETS: HttpConnection.Base.header(name,value) must combine duplicate values with ', '
TARGETS: HttpConnection.Base.headers() must return comma-separated values for same-name headers
ORACLES: HTTP/1.1 (RFC2616 §4.2) requires combining same-name message-header fields with comma; test
expects "no-cache, no-store"
ORACLES: Trigger test org.jsoup.helper.HttpConnectionTest::sameHeadersCombineWithComma asserts
combined value
CASES: Normal: single header call retains value unchanged; different names stored as separate
entries
CASES: Multiple header() calls with same name: values joined with ", "
CASES: Boundary: null value call may need to skip or store empty string
RISKS: Changing internal Map<String,String> to store list affects hasHeaderWithValue, removeHeader,
scanHeaders, getHeaderCaseInsensitive
RISKS: Request serialisation must emit combined header; removal of one value from a combined header
is ambiguous
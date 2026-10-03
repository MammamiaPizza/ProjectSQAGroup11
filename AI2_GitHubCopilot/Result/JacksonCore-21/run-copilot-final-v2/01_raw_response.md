TARGETS: public JsonToken nextToken() — returns next token; bug causes null instead of END_OBJECT
after path-match.
TARGETS: protected JsonToken _nextToken2() — helper for nextToken(); may skip closing tokens when
_includePath is true.
TARGETS: protected JsonToken _nextTokenWithBuffering() — handles buffered token path; likely root of
missing END_OBJECT.
ORACLES: Test assertions expect nextToken() to return END_OBJECT after matched scalar, e.g.,
testBasicSingleMatchFilteringWithPath.
ORACLES: Output string must contain closing braces for all opened objects/arrays, not truncated
before closing.
CASES: Normal: match a named field value; verify parent END_OBJECT emitted immediately after value
token.
CASES: Boundary: match last field of object; END_OBJECT must follow value (not null).
CASES: Varied structures: nested objects, arrays, mixed; each must emit matching
END_ARRAY/END_OBJECT after filtered tokens.
CASES: Single match with path; verify that closing tokens are emitted even when no further matches
exist.
RISKS: Private buffering state limits observable checks; rely on token stream and skipChildren()
behavior.
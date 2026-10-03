TARGETS: encode(byte[]), decode(byte[]), static encodeBase64(byte[],..), decodeBase64(byte[]),
encodeToString(byte[]), decode(String)
ORACLES: For empty/zero-length input: return empty byte array or empty string; never null; no NPE
ORACLES: Roundtrip: encode then decode yields original bytes for all lengths (including empty)
CASES: Empty (length 0), single-byte, typical 3-76 bytes, chunk boundary (76), after
lineSeparator=null construction
CASES: Zero or negative lineLength in constructor (edge); encode then immediately flush via stream
wrapper
RISKS: No source for Base64InputStream/OutputStream; only public Base64 API guarantees can be tested
RISKS: encode/decode thread-safety not required; internal resizeBuffer may overflow if buffer
zero-length
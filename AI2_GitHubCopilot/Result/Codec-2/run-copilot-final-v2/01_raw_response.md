TARGETS: encode(byte[]) with empty input; decode(byte[]) with empty input; static
encodeBase64(byte[],boolean) with empty array
TARGETS: Constructor with lineLength>0 and empty input to ensure no line separator is emitted
ORACLES: encode(empty) must return empty byte[] per CODEC-77; the fix removes spurious newline on
zero-length input
ORACLES: decode(empty) must return empty byte[]; streaming encoder must not prepend line separator
for empty input
CASES: encode(new byte[0]) with default Base64; decode(new byte[0]) with default Base64
CASES: encode(new byte[0]) with chunked lineLength=76; encode(new byte[0]) with urlSafe=true and
lineLength=0
CASES: encodeBase64(new byte[0], true); encodeBase64(new byte[0], false); decodeBase64(new byte[0])
CASES: encode(new byte[0]) after constructing with custom lineSeparator (e.g., CRLF) to verify no
added separator
RISKS: Cannot test stream wrapping; bug may also affect decode of empty stream; test isolation
relies on Base64 alone
RISKS: Must verify that internal resizeBuf does not produce spurious bytes for zero-length input;
line-length defaults may differ
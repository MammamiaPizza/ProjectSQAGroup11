TARGETS: Base64InputStream.read() and read(byte[],int,int) in both encode/decode modes
TARGETS: Base64InputStream constructors with lineLength, lineSeparator, doEncode params
TARGETS: Base64.encode(byte[]) and decode(byte[]) as called internally by the stream
ORACLES: no ArrayIndexOutOfBoundsException; output matches expected Base64 encoding/decoding
ORACLES: encode: stream output equals Base64.encodeBase64(original); decode: recovered bytes equal
original
CASES: empty, single-byte, boundary sizes at MIME_CHUNK_SIZE (76) and PEM_CHUNK_SIZE (64)
CASES: lengths 1 less, equal, 1 more than internal buffer capacity; non-null lineSeparator with
varying sizes
CASES: doEncode=true/false, urlSafe mode, maxResultSize limits in encodeBase64 overloads
CASES: read(byte[],offset,len) with offset+len at array edge, len=0, len>buffer, partial reads after
-1
RISKS: bug exact root unknown; must detect any IndexOutOfBoundsException; cannot access private
resizeBuffer; rely only on public API behavior
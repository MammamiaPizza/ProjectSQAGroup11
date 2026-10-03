TARGETS: BZip2CompressorInputStream.read(byte[], int, int), especially zero-length reads.
ORACLES: InputStream contract; trigger expects read(dest, offs, 0) returns 0, including at EOF.
CASES: Valid BZip2 stream: zero-length read before consuming data returns 0 without consuming bytes.
CASES: Valid BZip2 stream at EOF: zero-length read returns 0; positive-length read returns -1.
CASES: Check valid offsets for zero length, including offs == dest.length.
RISKS: Constructor requires valid BZip2 input; no fixture/content details are provided.

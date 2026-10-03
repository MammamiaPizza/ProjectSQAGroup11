TARGETS: Base64InputStream.read(), read(byte[],off,len), all 3 constructors, markSupported().
TARGETS: decode path (doEncode=false) EOF/empty-stream behavior; encode path round-trip.
ORACLES: java.io.InputStream contract: -1 at EOF; 0 only when len==0.
ORACLES: CODEC-101/testCodec101 expects empty input read() == -1, not 0.
ORACLES: expected bytes via Commons Codec Base64 encode/decode round-trip.
CASES: empty input -> read() == -1 and read(b) == -1.
CASES: single byte, multi-chunk streams, len=0 returns 0, boundary offset==len.
CASES: EOF mid base64 block, repeated read() calls, encode vs decode constructors.
RISKS: transient zero returns may be internal-only; assert public API outcomes.
RISKS: lineLength/lineSeparator ctor output may vary; avoid unverified exact strings.
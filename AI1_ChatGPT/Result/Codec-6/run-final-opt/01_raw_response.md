TARGETS: Base64InputStream.read() and read(byte[], int, int) decoding behavior.
ORACLES: Existing trigger test testCodec101; Base64 stream output and InputStream EOF conventions.
CASES: First single-byte read from decoding stream must return decoded data, not premature EOF/zero.
CASES: Repeated reads across internal buffer boundaries; final read returns -1 only after all decoded bytes.
CASES: Bulk read with valid offset/length, including len=0 and partial destination capacity.
RISKS: Context lacks trigger input/expected bytes and Base64InputStream implementation details.
RISKS: Do not infer behavior beyond documented InputStream semantics and existing project tests.